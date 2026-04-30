# Etapa 1: Construcción (Build)
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copiar el archivo pom.xml y descargar dependencias (esto optimiza el cache de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y construir el JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el JAR generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto del microservicio
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]