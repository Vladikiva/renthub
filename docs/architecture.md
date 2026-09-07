# Архитектура

## Компоненты

```
            ┌────────────────┐        scrape /actuator/prometheus
   браузер →│   RentHub app  │◄──────────────── Prometheus ──► Alertmanager
            │ Spring Boot 3  │                        │
            └───────┬────────┘                        ▼
                    │ JDBC                         Grafana
                    ▼
             ┌────────────┐
             │ PostgreSQL │  ← схема управляется Flyway
             └────────────┘
```

## Слои приложения

- `controller/view` — Thymeleaf-страницы для сотрудников и публичного поиска.
- `controller/rest` — административный JSON API (`/admin/**`, роль `ADMIN`).
- `service` — бизнес-правила аренды и бизнес-метрики Micrometer.
- `repository` — Spring Data JPA.
- `configuration` — security (`SecurityFilterChain`, пользователи из конфигурации), i18n.

## Бизнес-правила

- Аренду можно создать, только если помещение относится к выбранной станции.
- Завершить можно только аренду без даты возврата.
- Дата возврата не может быть раньше даты начала аренды.

## Данные

- `V1__baseline_schema.sql` — таблицы `station`, `customer`, `house`, `rental`, ключи и индексы.
- `V2__demo_data.sql` — демо-данные (4 станции, 4 клиента, 10 помещений, 5 аренд, одна активная).
- Hibernate работает в режиме `ddl-auto: validate`: схему меняет только Flyway.

## Профили

| Профиль | Назначение |
| --- | --- |
| `local` | H2 в памяти в режиме PostgreSQL, человекочитаемые логи, полная трассировка |
| `docker` | compose-стенд поверх PostgreSQL |
| `k8s` | кластер, конфигурация из ConfigMap/Secret |

## Эксплуатационные свойства

- Graceful shutdown (20 с) плюс `preStop sleep 10` — запросы не рвутся при выкатке.
- Liveness/readiness разделены; readiness включает проверку БД, поэтому под уходит из балансировки при проблемах с базой.
- Контейнер non-root, read-only rootfs, все capabilities сброшены.
- Rolling update с `maxUnavailable: 0`, PDB `minAvailable: 1`, HPA по CPU 70%.
