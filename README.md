# RentHub

Сервис аренды помещений (Spring Boot 3 / Java 17) с полным DevOps/SRE-обвесом:
контейнеризация, наблюдаемость, алерты, Kubernetes-манифесты, CI/CD и нагрузочные тесты.

Изначально это учебный CRUD на Spring Boot 2.2, который не собирался и не запускался.
Приложение починено и превращено в стенд, на котором можно показывать эксплуатацию сервиса.

## Что внутри

| Слой | Содержимое |
| --- | --- |
| Приложение | Spring Boot 3.3, Java 17, Thymeleaf + REST, Spring Security, JPA |
| Данные | PostgreSQL 16, миграции Flyway, H2 в PostgreSQL-режиме для локального профиля |
| Наблюдаемость | Actuator (liveness/readiness), Micrometer + Prometheus, бизнес-метрики, JSON-логи с traceId, трассировка |
| Локальный стенд | `docker compose`: app, postgres, prometheus, alertmanager, grafana с преднастроенным дашбордом |
| Kubernetes | kustomize base + prod overlay: probes, HPA, PDB, NetworkPolicy, ServiceMonitor, non-root/read-only контейнер |
| CI/CD | GitHub Actions: формат, тесты + Testcontainers, покрытие, Trivy (fs + image), hadolint, kubeconform, promtool, публикация образа в GHCR |
| Тесты | unit (Mockito), MockMvc + security, Testcontainers на реальном PostgreSQL, smoke-скрипт, k6 |
| Документация | [runbook](docs/runbook.md), [SLO](docs/slo.md), [архитектура](docs/architecture.md) |

## Быстрый старт

### Без зависимостей (H2 в памяти)

```bash
make run          # http://localhost:8080, профиль local
```

### Полный стенд

```bash
cp .env.example .env
make up           # app + postgres + prometheus + alertmanager + grafana
make smoke        # проверка health, метрик и защищённого API
make down
```

| Сервис | URL |
| --- | --- |
| Приложение | http://localhost:8080 |
| Actuator health | http://localhost:8080/actuator/health |
| Метрики Prometheus | http://localhost:8080/actuator/prometheus |
| Prometheus | http://localhost:9090 |
| Alertmanager | http://localhost:9093 |
| Grafana (дашборд «RentHub / Golden signals») | http://localhost:3000 |

Учётные данные по умолчанию — `admin/admin` и `emp/emp`; они задаются переменными окружения
(`ADMIN_USERNAME`, `ADMIN_PASSWORD`, `EMPLOYEE_USERNAME`, `EMPLOYEE_PASSWORD`) и должны
переопределяться везде, кроме локальной разработки.

## Разработка

```bash
make test         # unit + MockMvc
make verify       # + Testcontainers (нужен Docker) + JaCoCo
make fmt lint     # spotless + hadolint
make image scan   # сборка образа и Trivy
make load         # k6 против запущенного стенда
```

Все команды: `make help`.

Если Docker Engine 25+ отвергает клиент Testcontainers (`client version 1.32 is too old`),
зафиксируйте версию API:

```bash
echo "api.version=1.44" > ~/.docker-java.properties
```

## Kubernetes

```bash
kubectl apply -k deploy/k8s/overlays/prod
```

`deploy/k8s/base/secret.example.yaml` — только пример: в реальном кластере секреты
подставляются через External Secrets / Sealed Secrets / SOPS.

## Наблюдаемость

Бизнес- и технические метрики, на которых построены дашборд и алерты:

| Метрика | Смысл |
| --- | --- |
| `renthub_rentals_created_total` | созданные аренды |
| `renthub_rentals_finished_total` | завершённые аренды |
| `renthub_rentals_running` | активные аренды прямо сейчас |
| `renthub_rentals_creation_seconds` | латентность создания аренды |
| `http_server_requests_seconds` | RED-метрики HTTP |
| `hikaricp_connections_*` | насыщение пула соединений |

Правила алертов: `deploy/monitoring/prometheus/alerts.yml`, каждый алерт ссылается на
соответствующий раздел [runbook](docs/runbook.md).

## Конфигурация

Вся конфигурация — через переменные окружения (12-factor):

| Переменная | По умолчанию | Назначение |
| --- | --- | --- |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | `jdbc:postgresql://localhost:5432/renthub`, `renthub`, `renthub` | доступ к БД |
| `DB_POOL_MAX` / `DB_POOL_MIN` | `10` / `2` | размер пула HikariCP |
| `SERVER_PORT` | `8080` | порт HTTP |
| `TOMCAT_MAX_THREADS` | `100` | размер пула потоков |
| `TRACING_SAMPLE_RATE` | `0.1` | доля трассируемых запросов |
| `LOG_LEVEL_ROOT` / `LOG_LEVEL_APP` | `INFO` | уровни логирования |
| `APP_VERSION` / `APP_ENVIRONMENT` | `dev` / `local` | метки в `/actuator/info` и логах |
