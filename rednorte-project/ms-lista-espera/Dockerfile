FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/ms-lista-espera-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
