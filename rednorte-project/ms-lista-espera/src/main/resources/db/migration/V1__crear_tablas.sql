-- V1: Creación de tablas para MS Lista de Espera

CREATE TABLE pacientes (
    id          BIGSERIAL PRIMARY KEY,
    rut         VARCHAR(12)  NOT NULL UNIQUE,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    telefono    VARCHAR(15),
    email       VARCHAR(100)
);

CREATE TABLE waitlist_items (
    id               BIGSERIAL PRIMARY KEY,
    paciente_id      BIGINT       NOT NULL REFERENCES pacientes(id),
    tipo_atencion    VARCHAR(20)  NOT NULL,
    especialidad     VARCHAR(100) NOT NULL,
    prioridad        INT          NOT NULL,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'EN_ESPERA',
    fecha_ingreso    DATE         NOT NULL DEFAULT CURRENT_DATE,
    fecha_asignacion TIMESTAMP
);

CREATE INDEX idx_waitlist_especialidad ON waitlist_items(especialidad);
CREATE INDEX idx_waitlist_estado ON waitlist_items(estado);
CREATE INDEX idx_waitlist_prioridad ON waitlist_items(prioridad, fecha_ingreso);
