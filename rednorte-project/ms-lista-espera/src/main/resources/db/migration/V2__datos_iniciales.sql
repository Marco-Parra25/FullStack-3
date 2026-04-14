-- V2: Datos iniciales de prueba

INSERT INTO pacientes (rut, nombre, apellido, telefono, email) VALUES
('12345678-9', 'María',   'González',  '+56912345678', 'maria.gonzalez@email.cl'),
('98765432-1', 'Pedro',   'Muñoz',     '+56987654321', 'pedro.munoz@email.cl'),
('11222333-4', 'Carolina','Soto',      '+56911223344', 'carolina.soto@email.cl'),
('44555666-7', 'Juan',    'Pérez',     '+56944556677', 'juan.perez@email.cl'),
('77888999-0', 'Ana',     'Rodríguez', '+56977889900', 'ana.rodriguez@email.cl');

INSERT INTO waitlist_items (paciente_id, tipo_atencion, especialidad, prioridad, estado, fecha_ingreso) VALUES
(1, 'CONSULTA',          'Cardiología',    3, 'EN_ESPERA', '2025-06-15'),
(2, 'CIRUGIA',           'Traumatología',  2, 'EN_ESPERA', '2025-05-20'),
(3, 'URGENCIA_DIFERIDA', 'Neurología',     1, 'EN_ESPERA', '2025-07-01'),
(4, 'CONSULTA',          'Cardiología',    3, 'EN_ESPERA', '2025-08-10'),
(5, 'CIRUGIA',           'Oftalmología',   2, 'EN_ESPERA', '2025-04-30');
