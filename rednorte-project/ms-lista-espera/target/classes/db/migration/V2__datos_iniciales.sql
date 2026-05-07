-- V2: Datos iniciales de prueba (20 pacientes para testing)

INSERT INTO pacientes (rut, nombre, apellido, telefono, email) VALUES
('12345678-9',  'María',    'González',   '+56912345678', 'maria.gonzalez@email.cl'),
('98765432-1',  'Pedro',    'Muñoz',      '+56987654321', 'pedro.munoz@email.cl'),
('11222333-4',  'Carolina', 'Soto',       '+56911223344', 'carolina.soto@email.cl'),
('44555666-7',  'Juan',     'Pérez',      '+56944556677', 'juan.perez@email.cl'),
('77888999-0',  'Ana',      'Rodríguez',  '+56977889900', 'ana.rodriguez@email.cl'),
('55666777-8',  'Carlos',   'López',      '+56955667778', 'carlos.lopez@email.cl'),
('22333444-5',  'Patricia', 'Fernández',  '+56922334445', 'patricia.fernandez@email.cl'),
('33444555-6',  'Roberto',  'Gómez',      '+56933445556', 'roberto.gomez@email.cl'),
('66777888-9',  'Gabriela', 'Martínez',   '+56966778889', 'gabriela.martinez@email.cl'),
('88999000-1',  'David',    'Jiménez',    '+56988999001', 'david.jimenez@email.cl'),
('19191919-1',  'Rosa',     'Valenzuela', '+56919191919', 'rosa.valenzuela@email.cl'),
('20202020-2',  'Felipe',   'Castillo',   '+56920202020', 'felipe.castillo@email.cl'),
('21212121-3',  'Verónica', 'Herrera',    '+56921212121', 'veronica.herrera@email.cl'),
('22222222-4',  'Andrés',   'Flores',     '+56922222222', 'andres.flores@email.cl'),
('23232323-5',  'Marcela',  'Rivera',     '+56923232323', 'marcela.rivera@email.cl'),
('24242424-6',  'Miguel',   'Acosta',     '+56924242424', 'miguel.acosta@email.cl'),
('25252525-7',  'Lorena',   'Bravo',      '+56925252525', 'lorena.bravo@email.cl'),
('26262626-8',  'Sergio',   'Cortés',     '+56926262626', 'sergio.cortes@email.cl'),
('27272727-9',  'Daniela',  'Díaz',       '+56927272727', 'daniela.diaz@email.cl'),
('28282828-0',  'Mauricio', 'Escobar',    '+56928282828', 'mauricio.escobar@email.cl');

INSERT INTO waitlist_items (paciente_id, tipo_atencion, especialidad, prioridad, estado, fecha_ingreso) VALUES
(1, 'CONSULTA',          'Cardiología',    3, 'EN_ESPERA', '2025-06-15'),
(2, 'CIRUGIA',           'Traumatología',  2, 'EN_ESPERA', '2025-05-20'),
(3, 'URGENCIA_DIFERIDA', 'Neurología',     1, 'EN_ESPERA', '2025-07-01'),
(4, 'CONSULTA',          'Cardiología',    3, 'EN_ESPERA', '2025-08-10'),
(5, 'CIRUGIA',           'Oftalmología',   2, 'EN_ESPERA', '2025-04-30');
