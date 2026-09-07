SHELL := /bin/bash
APP_DIR := app
COMPOSE := docker compose
IMAGE ?= ghcr.io/vladikiva/renthub:dev

.DEFAULT_GOAL := help

.PHONY: help
help: ## Show this help
	@grep -hE '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}'

.PHONY: run
run: ## Run the app locally on H2 (no external dependencies)
	cd $(APP_DIR) && ./mvnw -B spring-boot:run -Dspring-boot.run.profiles=local

.PHONY: test
test: ## Unit + MockMvc tests
	cd $(APP_DIR) && ./mvnw -B test

.PHONY: verify
verify: ## Full build incl. Testcontainers integration tests and coverage
	cd $(APP_DIR) && ./mvnw -B verify

.PHONY: fmt
fmt: ## Apply code formatting
	cd $(APP_DIR) && ./mvnw -B spotless:apply

.PHONY: lint
lint: ## Check formatting and lint the Dockerfile
	cd $(APP_DIR) && ./mvnw -B spotless:check
	docker run --rm -i hadolint/hadolint < $(APP_DIR)/Dockerfile

.PHONY: image
image: ## Build the container image
	docker build -t $(IMAGE) $(APP_DIR)

.PHONY: scan
scan: image ## Scan the image with Trivy
	docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest \
		image --severity HIGH,CRITICAL --ignore-unfixed $(IMAGE)

.PHONY: up
up: ## Start app + postgres + prometheus + alertmanager + grafana
	$(COMPOSE) up -d --build

.PHONY: down
down: ## Stop the stack
	$(COMPOSE) down

.PHONY: clean
clean: ## Stop the stack and delete volumes
	$(COMPOSE) down -v

.PHONY: logs
logs: ## Tail application logs
	$(COMPOSE) logs -f app

.PHONY: smoke
smoke: ## Smoke test the running stack
	./scripts/smoke-test.sh

.PHONY: load
load: ## Run the k6 load test against the compose stack
	docker run --rm --network host -v $$PWD/tests/load:/scripts grafana/k6:latest run /scripts/rentals.js

.PHONY: k8s-build
k8s-build: ## Render the production kustomize overlay
	kustomize build deploy/k8s/overlays/prod
