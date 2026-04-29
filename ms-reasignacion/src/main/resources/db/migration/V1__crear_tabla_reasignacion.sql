CREATE TABLE reasignaciones ( -- Plural, como en tu @Table
    id UUID PRIMARY KEY,
    paciente_rut VARCHAR(50) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL,
    estado VARCHAR(50) NOT NULL,
    cupo_origen_id UUID NOT NULL
);