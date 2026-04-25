# 🏥 Microservicio de Reasignación de Citas - RedNorte

Este microservicio forma parte del ecosistema **RedNorte** y es el encargado de gestionar la lógica de **reasignación automática de cupos** cuando un paciente libera una cita.

## 🚀 Funcionalidades Principales

* **Consumo de Eventos:** Escucha mensajes de Kafka provenientes del tópico de citas liberadas.
* **Gestión de Lista de Espera:** Identifica al siguiente paciente con mayor prioridad según los criterios de RedNorte.
* **Notificación Automática:** Publica eventos de reasignación para que el sistema de notificaciones avise al paciente.
* **Persistencia:** Registra el historial de reasignaciones en una base de datos PostgreSQL.

## 🛠️ Tecnologías Utilizadas

* **Java 17** & **Spring Boot 3.2.4**
* **Spring Data JPA:** Para la interacción con la base de datos.
* **Apache Kafka:** Comunicación asíncrona basada en eventos.
* **PostgreSQL:** Base de datos relacional para persistencia.
* **Flyway:** Gestión de migraciones de base de datos.
* **H2 Database:** Utilizada para la ejecución de tests en memoria.
* **Lombok:** Para reducir el código repetitivo (Boilerplate).

## 🏗️ Arquitectura

El proyecto sigue los principios de la **Arquitectura Hexagonal (Puertos y Adaptadores)**, lo que permite desacoplar la lógica de negocio de las tecnologías externas (Base de datos, Kafka, APIs).

```text
src/main/java/org/msreasignacion/
├── application/     # Casos de uso (Lógica de aplicación)
├── domain/          # Entidades, eventos y puertos (Núcleo del negocio)
└── infrastructure/  # Adaptadores de entrada/salida y configuración
