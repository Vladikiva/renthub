#!/usr/bin/env bash
# Smoke test for a running RentHub instance (compose stack by default).
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_USERNAME="${ADMIN_USERNAME:-admin}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-admin}"
TIMEOUT="${TIMEOUT:-120}"

log() { printf '[smoke] %s\n' "$*"; }
fail() { printf '[smoke] FAILED: %s\n' "$*" >&2; exit 1; }

log "waiting for ${BASE_URL} to become ready (max ${TIMEOUT}s)"
deadline=$((SECONDS + TIMEOUT))
until curl -fsS "${BASE_URL}/actuator/health/readiness" >/dev/null 2>&1; do
  ((SECONDS < deadline)) || fail "application did not become ready in ${TIMEOUT}s"
  sleep 3
done

log "liveness / readiness"
curl -fsS "${BASE_URL}/actuator/health/liveness" | grep -q '"status":"UP"' || fail "liveness is not UP"
curl -fsS "${BASE_URL}/actuator/health/readiness" | grep -q '"status":"UP"' || fail "readiness is not UP"

log "public page renders"
curl -fsS "${BASE_URL}/" >/dev/null || fail "index page is not reachable"

log "metrics are exported"
metrics=$(curl -fsS "${BASE_URL}/actuator/prometheus")
grep -q 'renthub_rentals_running' <<<"${metrics}" || fail "business metrics are missing"
grep -q 'hikaricp_connections' <<<"${metrics}" || fail "database metrics are missing"

log "admin API is protected"
code=$(curl -s -o /dev/null -w '%{http_code}' "${BASE_URL}/admin/running-rentals")
[[ "${code}" == "401" ]] || fail "unauthenticated admin call returned ${code}, expected 401"

log "admin API works with credentials"
curl -fsS -u "${ADMIN_USERNAME}:${ADMIN_PASSWORD}" "${BASE_URL}/admin/running-rentals" >/dev/null \
  || fail "authenticated admin call failed"

log "all checks passed"
