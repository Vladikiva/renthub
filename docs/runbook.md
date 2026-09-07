# Runbook RentHub

Дежурная документация: как быстро понять, что происходит с сервисом, и что делать по каждому алерту.

## Первые действия при инциденте

```bash
# состояние стенда
docker compose ps                       # локально
kubectl -n renthub get pods,hpa,events --sort-by=.lastTimestamp

# здоровье и версия
curl -s localhost:8080/actuator/health | jq
curl -s localhost:8080/actuator/info | jq

# логи (JSON, ищем по traceId)
docker compose logs -f app
kubectl -n renthub logs deploy/renthub --tail=200 -f
```

Полезные ссылки: дашборд «RentHub / Golden signals» в Grafana, Prometheus http://localhost:9090,
Alertmanager http://localhost:9093.

## RenthubDown

Prometheus не может собрать метрики с инстанса.

1. Проверить, живы ли поды: `kubectl -n renthub get pods` (`CrashLoopBackOff` → `kubectl logs --previous`).
2. Частая причина — недоступна БД: под не проходит readiness (`/actuator/health/readiness` = `DOWN`, компонент `db`).
3. Проверить сетевую политику и Service: `kubectl -n renthub get endpoints renthub`.
4. Быстрый откат: `kubectl -n renthub rollout undo deploy/renthub`.

## RenthubHighErrorRate

Более 1% ответов 5xx за 5 минут — прямое сжигание бюджета ошибок.

1. Найти проблемный эндпоинт:
   `sum by (uri, status) (rate(http_server_requests_seconds_count{status=~"5.."}[5m]))`.
2. Сопоставить с последним деплоем (`/actuator/info` → `app.version`), при совпадении — откат.
3. Посмотреть исключения в логах: `kubectl -n renthub logs deploy/renthub | jq 'select(.level=="ERROR")'`.
4. Если ошибки только на `/admin/**` — проверить, не сломалась ли конфигурация учётных данных (Secret).

## RenthubHighLatency

p99 выше 500 мс более 10 минут.

1. Проверить насыщение пула БД (`hikaricp_connections_pending`) и heap (панель Saturation).
2. Проверить долгие запросы в PostgreSQL:
   `SELECT pid, now()-query_start AS age, query FROM pg_stat_activity WHERE state='active' ORDER BY age DESC;`
3. При росте трафика — убедиться, что HPA отработал: `kubectl -n renthub get hpa renthub`.
4. Временная мера: увеличить `DB_POOL_MAX` и число реплик.

## RenthubConnectionPoolSaturated

Потоки ждут соединение с БД.

1. `hikaricp_connections_active` близко к `hikaricp_connections_max` → пул мал либо есть медленные запросы.
2. Проверить блокировки: `SELECT * FROM pg_locks WHERE NOT granted;`
3. Увеличить `DB_POOL_MAX` (учитывая `max_connections` PostgreSQL) и перевыкатить.

## RenthubHeapPressure

Heap выше 90% более 10 минут.

1. Снять дамп: `curl -u admin:*** localhost:8080/actuator/heapdump -o heap.hprof`.
2. Проверить частоту и длительность GC: `jvm_gc_pause_seconds`.
3. Быстрая мера — рестарт пода (`kubectl -n renthub rollout restart deploy/renthub`) и увеличение лимита памяти;
   контейнер использует `-XX:MaxRAMPercentage=75`, поэтому heap растёт вместе с лимитом.

## RenthubNoRentalsCreated

За час не создано ни одной аренды — информационный сигнал.

1. Проверить, ожидаемо ли это (ночь, выходные).
2. Проверить работоспособность формы: `make smoke` или ручной сценарий в UI.
3. Если трафик есть, а аренды не создаются — искать ошибки валидации в логах.

## Типовые операции

### Выкатка новой версии

```bash
kubectl -n renthub set image deploy/renthub app=ghcr.io/vladikiva/renthub:<tag>
kubectl -n renthub rollout status deploy/renthub
```

### Откат

```bash
kubectl -n renthub rollout undo deploy/renthub
```

### Миграции БД

Flyway применяет миграции при старте приложения. Правила: миграции только «вперёд»,
изменения обратно совместимы (сначала добавляем колонку, затем код, затем удаляем старое).
Состояние: `curl -u admin:*** localhost:8080/actuator/flyway`.

### Ротация учётных данных

Обновить Secret `renthub-secrets` и перезапустить деплой; в compose — изменить `.env` и `make up`.

### Восстановление БД

```bash
docker compose exec postgres pg_dump -U renthub renthub > backup.sql   # бэкап
docker compose exec -T postgres psql -U renthub renthub < backup.sql   # восстановление
```

## Постмортем

После каждого инцидента с расходом бюджета ошибок — постмортем без поиска виноватых:
таймлайн, влияние на пользователей, причина, что сработало и что нет, конкретные действия с ответственными.
