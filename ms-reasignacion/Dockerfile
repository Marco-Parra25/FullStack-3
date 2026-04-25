# Etapa 1: Compilación (Build)
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copiar el pom.xml y descargar dependencias (esto optimiza la caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar el proyecto saltando los tests para acelerar el build
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución (Runtime)
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto que configuraste en tu application.yml
EXPOSE 8082

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]