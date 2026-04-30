# ─── Stage 1: Build (Maven oficial con Java 17, soporta ARM64) ────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre

WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring

COPY --from=build /build/target/ms-reasignacion-*.jar app.jar

USER spring

EXPOSE 8082

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
