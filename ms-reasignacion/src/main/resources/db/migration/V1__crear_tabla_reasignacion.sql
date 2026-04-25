CREATE TABLE reasignacion (
                              id UUID PRIMARY KEY,
                              paciente_rut VARCHAR(50) NOT NULL, -- Cambiado de paciente_id a paciente_rut
                              especialidad VARCHAR(100) NOT NULL,
                              fecha_asignacion TIMESTAMP NOT NULL,
                              estado VARCHAR(50) NOT NULL,
                              cupo_origen_id UUID NOT NULL
);