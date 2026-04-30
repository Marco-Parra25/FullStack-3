Microservicio de Notificaciones (MS-Notificaciones) 🚀 Este microservicio es el encargado de gestionar y enviar alertas a los pacientes cuando se les ha reasignado un cupo médico. Es un componente reactivo y orientado a eventos, lo que garantiza que las notificaciones se procesen de forma asíncrona sin bloquear otros procesos del sistema.

🏗️ Arquitectura El proyecto sigue los principios de la Arquitectura Hexagonal (Puertos y Adaptadores), lo que permite un desacoplamiento total entre la lógica de negocio y las tecnologías externas (Kafka, JavaMail, etc.).

Getty Images Explorar

Estructura de Paquetes: domain: Contiene los modelos de negocio, eventos de dominio y las interfaces (puertos) de salida.

application: Contiene los casos de uso que orquestan la lógica de notificación.

infrastructure: Implementa los adaptadores (Kafka Consumer para entrada y Messaging Adapter para salida) y las configuraciones técnicas.

🛠️ Tecnologías Utilizadas Java 17

Spring Boot 3.x

Spring Kafka (Consumidor de eventos)

Lombok / SLF4J (Logs y limpieza de código)

JUnit 5 / Mockito (Pruebas unitarias)

📡 Comunicación por Eventos El microservicio se suscribe al tópico de Kafka:

Tópico: cupo-asignado

Grupo de Consumidor: notificaciones-group

Al recibir un evento, el sistema simula el envío de un Email y un SMS con los datos reales del paciente y la especialidad reasignada.

🚀 Instalación y Ejecución Requisitos Previos Tener Apache Kafka corriendo en localhost:9092.

JDK 17 o superior.

Maven 3.6+.

Pasos para ejecutar: Clonar el repositorio y situarse en la rama: feat-ms-notificaciones-hp.

Navegar a la carpeta del proyecto:

Bash cd ms-notificaciones Compilar el proyecto:

Bash mvn clean install Ejecutar la aplicación:

Bash mvn spring-boot:run La aplicación iniciará por defecto en el puerto 8081 para evitar conflictos con otros microservicios.

🧪 Pruebas Unitarias Para validar la lógica de notificación sin depender de Kafka, ejecuta:

Bash mvn test Esto ejecutará el NotificarPacienteUseCaseTest, verificando que el flujo de datos hacia los adaptadores sea correcto.

Desarrollado por: Héctor Peña (HP)

Proyecto: Sistema de Gestión Hospitalaria - RedNorte
