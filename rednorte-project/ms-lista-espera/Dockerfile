FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/ms-lista-espera-1.0.0-SNAPSHOT.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring

USER spring

EXPOSE 8081

ENV JAVA_OPTS=""

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
