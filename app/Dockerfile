# syntax=docker/dockerfile:1.7
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
# `maven_settings` is optional: pass `--secret id=maven_settings,src=$HOME/.m2/settings.xml`
# to build behind a Maven mirror/proxy.
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn -B -ntp dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn -B -ntp -DskipTests package && \
    java -Djarmode=layertools -jar target/renthub-*.jar extract --destination /build/layers

FROM eclipse-temurin:17-jre-jammy AS runtime
RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd --system --gid 10001 renthub && \
    useradd --system --uid 10001 --gid renthub --home /app renthub
WORKDIR /app
COPY --from=build --chown=renthub:renthub /build/layers/dependencies/ ./
COPY --from=build --chown=renthub:renthub /build/layers/spring-boot-loader/ ./
COPY --from=build --chown=renthub:renthub /build/layers/snapshot-dependencies/ ./
COPY --from=build --chown=renthub:renthub /build/layers/application/ ./

USER 10001:10001
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=5 \
    CMD curl -fsS http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
