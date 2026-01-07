-- =====================================================
-- FCT-SEEK - Base de datos
-- Sistema de gestión de FCT para CIFP Villa de Agüimes
-- =====================================================

-- Eliminar tablas si existen (en orden inverso por dependencias)
DROP TABLE IF EXISTS lista_empresas CASCADE;
DROP TABLE IF EXISTS listas CASCADE;
DROP TABLE IF EXISTS favoritos CASCADE;
DROP TABLE IF EXISTS valoraciones CASCADE;
DROP TABLE IF EXISTS comentarios CASCADE;
DROP TABLE IF EXISTS reservas CASCADE;
DROP TABLE IF EXISTS plazas CASCADE;
DROP TABLE IF EXISTS empresa_contactada CASCADE;
DROP TABLE IF EXISTS empresa_cursos CASCADE;
DROP TABLE IF EXISTS empresas CASCADE;
DROP TABLE IF EXISTS cursos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS departamentos CASCADE;

-- =====================================================
-- TABLAS PRINCIPALES
-- =====================================================

-- Departamentos (Familias Profesionales) del centro
CREATE TABLE departamentos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuarios (profesores y alumnos)
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nif VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('PROFESOR', 'ALUMNO')),
    departamento_id INTEGER REFERENCES departamentos(id),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cursos/Ciclos formativos
CREATE TABLE cursos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    siglas VARCHAR(10) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    departamento_id INTEGER NOT NULL REFERENCES departamentos(id),
    grado VARCHAR(20) NOT NULL CHECK (grado IN ('BASICO', 'MEDIO', 'SUPERIOR', 'ESPECIALIZACION')),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Empresas colaboradoras
CREATE TABLE empresas (
    id SERIAL PRIMARY KEY,
    cif VARCHAR(15) UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(200),
    ciudad VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10),
    telefono VARCHAR(20),
    email VARCHAR(150),
    web VARCHAR(200),
    persona_contacto VARCHAR(100),
    telefono_contacto VARCHAR(20),
    email_contacto VARCHAR(150),
    descripcion TEXT,
    activa BOOLEAN DEFAULT TRUE,
    created_by INTEGER REFERENCES usuarios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- RELACIONES EMPRESA-CURSOS Y CONTACTOS
-- =====================================================

-- Cursos que acepta cada empresa
CREATE TABLE empresa_cursos (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    curso_id INTEGER NOT NULL REFERENCES cursos(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empresa_id, curso_id)
);

-- Registro de contactos con empresas (qué departamento contactó)
CREATE TABLE empresa_contactada (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    departamento_id INTEGER NOT NULL REFERENCES departamentos(id),
    profesor_id INTEGER NOT NULL REFERENCES usuarios(id),
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    notas TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empresa_id, departamento_id)
);

-- =====================================================
-- SISTEMA DE PLAZAS Y RESERVAS
-- =====================================================

-- Plazas ofertadas por empresa/departamento
CREATE TABLE plazas (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    departamento_id INTEGER NOT NULL REFERENCES departamentos(id),
    curso_id INTEGER REFERENCES cursos(id),
    cantidad INTEGER NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    es_general BOOLEAN DEFAULT FALSE,
    curso_academico VARCHAR(9) NOT NULL,
    created_by INTEGER REFERENCES usuarios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empresa_id, departamento_id, curso_id, curso_academico)
);

-- Reservas de plazas por profesores
CREATE TABLE reservas (
    id SERIAL PRIMARY KEY,
    plaza_id INTEGER NOT NULL REFERENCES plazas(id) ON DELETE CASCADE,
    profesor_id INTEGER NOT NULL REFERENCES usuarios(id),
    curso_id INTEGER NOT NULL REFERENCES cursos(id),
    cantidad INTEGER NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    clase VARCHAR(10),
    estado VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA')),
    notas TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- COMENTARIOS Y VALORACIONES
-- =====================================================

-- Comentarios sobre empresas
CREATE TABLE comentarios (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id),
    texto TEXT NOT NULL,
    es_privado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Valoraciones de empresas (1-5 estrellas)
CREATE TABLE valoraciones (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id),
    puntuacion INTEGER NOT NULL CHECK (puntuacion >= 1 AND puntuacion <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empresa_id, usuario_id)
);

-- =====================================================
-- FAVORITOS Y LISTAS PERSONALIZADAS
-- =====================================================

-- Empresas favoritas de cada usuario
CREATE TABLE favoritos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(usuario_id, empresa_id)
);

-- Listas personalizadas de usuarios
CREATE TABLE listas (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    es_favoritos BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Empresas en cada lista
CREATE TABLE lista_empresas (
    id SERIAL PRIMARY KEY,
    lista_id INTEGER NOT NULL REFERENCES listas(id) ON DELETE CASCADE,
    empresa_id INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    notas TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(lista_id, empresa_id)
);

-- =====================================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_departamento ON usuarios(departamento_id);

CREATE INDEX idx_cursos_departamento ON cursos(departamento_id);
CREATE INDEX idx_cursos_siglas ON cursos(siglas);

CREATE INDEX idx_empresas_nombre ON empresas(nombre);
CREATE INDEX idx_empresas_ciudad ON empresas(ciudad);
CREATE INDEX idx_empresas_activa ON empresas(activa);

CREATE INDEX idx_empresa_cursos_empresa ON empresa_cursos(empresa_id);
CREATE INDEX idx_empresa_cursos_curso ON empresa_cursos(curso_id);

CREATE INDEX idx_empresa_contactada_empresa ON empresa_contactada(empresa_id);
CREATE INDEX idx_empresa_contactada_departamento ON empresa_contactada(departamento_id);
CREATE INDEX idx_empresa_contactada_profesor ON empresa_contactada(profesor_id);

CREATE INDEX idx_plazas_empresa ON plazas(empresa_id);
CREATE INDEX idx_plazas_departamento ON plazas(departamento_id);
CREATE INDEX idx_plazas_curso_academico ON plazas(curso_academico);

CREATE INDEX idx_reservas_plaza ON reservas(plaza_id);
CREATE INDEX idx_reservas_profesor ON reservas(profesor_id);
CREATE INDEX idx_reservas_estado ON reservas(estado);

CREATE INDEX idx_comentarios_empresa ON comentarios(empresa_id);
CREATE INDEX idx_comentarios_usuario ON comentarios(usuario_id);
CREATE INDEX idx_comentarios_privado ON comentarios(es_privado);

CREATE INDEX idx_valoraciones_empresa ON valoraciones(empresa_id);
CREATE INDEX idx_favoritos_usuario ON favoritos(usuario_id);
CREATE INDEX idx_listas_usuario ON listas(usuario_id);

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista de empresas con valoración media
CREATE VIEW v_empresas_valoracion AS
SELECT 
    e.id,
    e.nombre,
    e.ciudad,
    COALESCE(AVG(v.puntuacion), 0) AS valoracion_media,
    COUNT(v.id) AS total_valoraciones
FROM empresas e
LEFT JOIN valoraciones v ON e.id = v.empresa_id
WHERE e.activa = TRUE
GROUP BY e.id, e.nombre, e.ciudad;

-- Vista de plazas disponibles por empresa/departamento
CREATE VIEW v_plazas_disponibles AS
SELECT 
    p.id AS plaza_id,
    p.empresa_id,
    e.nombre AS empresa_nombre,
    p.departamento_id,
    d.nombre AS departamento_nombre,
    p.curso_id,
    c.siglas AS curso_siglas,
    p.es_general,
    p.cantidad AS plazas_ofertadas,
    COALESCE(SUM(r.cantidad) FILTER (WHERE r.estado != 'CANCELADA'), 0) AS plazas_reservadas,
    p.cantidad - COALESCE(SUM(r.cantidad) FILTER (WHERE r.estado != 'CANCELADA'), 0) AS plazas_libres,
    p.curso_academico
FROM plazas p
JOIN empresas e ON p.empresa_id = e.id
JOIN departamentos d ON p.departamento_id = d.id
LEFT JOIN cursos c ON p.curso_id = c.id
LEFT JOIN reservas r ON p.id = r.plaza_id
WHERE e.activa = TRUE
GROUP BY p.id, e.nombre, d.nombre, c.siglas;

-- Vista de empresas contactadas por departamento
CREATE VIEW v_empresas_contactadas AS
SELECT 
    ec.id,
    ec.empresa_id,
    e.nombre AS empresa_nombre,
    ec.departamento_id,
    d.codigo AS departamento_codigo,
    d.nombre AS departamento_nombre,
    ec.profesor_id,
    u.nombre || ' ' || u.apellidos AS profesor_nombre,
    ec.fecha,
    ec.notas
FROM empresa_contactada ec
JOIN empresas e ON ec.empresa_id = e.id
JOIN departamentos d ON ec.departamento_id = d.id
JOIN usuarios u ON ec.profesor_id = u.id
WHERE e.activa = TRUE;

-- =====================================================
-- FUNCIONES Y TRIGGERS
-- =====================================================

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers para updated_at
CREATE TRIGGER update_departamentos_updated_at BEFORE UPDATE ON departamentos FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_usuarios_updated_at BEFORE UPDATE ON usuarios FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cursos_updated_at BEFORE UPDATE ON cursos FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_empresas_updated_at BEFORE UPDATE ON empresas FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_empresa_contactada_updated_at BEFORE UPDATE ON empresa_contactada FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_plazas_updated_at BEFORE UPDATE ON plazas FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_reservas_updated_at BEFORE UPDATE ON reservas FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_comentarios_updated_at BEFORE UPDATE ON comentarios FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_valoraciones_updated_at BEFORE UPDATE ON valoraciones FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_listas_updated_at BEFORE UPDATE ON listas FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Función para verificar que no se reserven más plazas de las disponibles
CREATE OR REPLACE FUNCTION check_plazas_disponibles()
RETURNS TRIGGER AS $$
DECLARE
    plazas_totales INTEGER;
    plazas_reservadas INTEGER;
BEGIN
    SELECT cantidad INTO plazas_totales FROM plazas WHERE id = NEW.plaza_id;
    
    SELECT COALESCE(SUM(cantidad), 0) INTO plazas_reservadas 
    FROM reservas 
    WHERE plaza_id = NEW.plaza_id 
      AND estado != 'CANCELADA'
      AND id != COALESCE(NEW.id, 0);
    
    IF (plazas_reservadas + NEW.cantidad) > plazas_totales THEN
        RAISE EXCEPTION 'No hay suficientes plazas disponibles. Disponibles: %, Solicitadas: %', 
            (plazas_totales - plazas_reservadas), NEW.cantidad;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_plazas_before_reserva
BEFORE INSERT OR UPDATE ON reservas
FOR EACH ROW EXECUTE FUNCTION check_plazas_disponibles();

-- Función para crear lista de favoritos automáticamente al crear usuario
CREATE OR REPLACE FUNCTION create_lista_favoritos()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO listas (usuario_id, nombre, es_favoritos)
    VALUES (NEW.id, 'Favoritos', TRUE);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_favoritos_on_user_create
AFTER INSERT ON usuarios
FOR EACH ROW EXECUTE FUNCTION create_lista_favoritos();

-- =====================================================
-- DATOS REALES - CIFP VILLA DE AGÜIMES
-- =====================================================

-- Departamentos (Familias Profesionales)
INSERT INTO departamentos (codigo, nombre) VALUES
('AFD', 'Actividades Físicas y Deportivas'),
('AYG', 'Administración y Gestión'),
('EYA', 'Energía y Agua'),
('HYT', 'Hostelería y Turismo'),
('INF', 'Informática y Comunicaciones'),
('SAN', 'Sanidad'),
('SYM', 'Seguridad y Medio Ambiente'),
('SSC', 'Servicios Socioculturales y a la Comunidad'),
('MMC', 'Madera, Mueble y Corcho');

-- Cursos/Ciclos Formativos
INSERT INTO cursos (codigo, siglas, nombre, departamento_id, grado) VALUES
-- Actividades Físicas y Deportivas (AFD) - id: 1
('CFGS-AFI', 'AFI', 'Acondicionamiento Físico', 1, 'SUPERIOR'),
('CFGS-EAS', 'EAS', 'Enseñanza y Animación Sociodeportiva', 1, 'SUPERIOR'),

-- Administración y Gestión (AYG) - id: 2
('CFGM-GAD', 'GAD', 'Gestión Administrativa', 2, 'MEDIO'),
('CFGS-FIN', 'FIN', 'Administración y Finanzas', 2, 'SUPERIOR'),
('CFGS-ADIR', 'ADIR', 'Asistencia a la Dirección', 2, 'SUPERIOR'),

-- Energía y Agua (EYA) - id: 3
('CFGS-ENER', 'ENER', 'Energías Renovables', 3, 'SUPERIOR'),
('CFGS-EFIC', 'EFIC', 'Eficiencia Energética y Energía Solar Térmica', 3, 'SUPERIOR'),
('CFGS-RET', 'RET', 'Redes y Estaciones de Tratamiento de Aguas', 3, 'SUPERIOR'),

-- Hostelería y Turismo (HYT) - id: 4
('CFGM-SERV', 'SERV', 'Servicios en Restauración', 4, 'MEDIO'),
('CFGM-COC', 'COC', 'Cocina y Gastronomía', 4, 'MEDIO'),
('CFGM-PAST', 'PAST', 'Panadería, Repostería y Confitería', 4, 'MEDIO'),
('CFGS-ALOJ', 'ALOJ', 'Gestión de Alojamientos Turísticos', 4, 'SUPERIOR'),
('CFGS-DCOC', 'DCOC', 'Dirección de Cocina', 4, 'SUPERIOR'),
('CFGS-DSERV', 'DSERV', 'Dirección de Servicios en Restauración', 4, 'SUPERIOR'),

-- Informática y Comunicaciones (INF) - id: 5
('CFGM-SMR', 'SMR', 'Sistemas Microinformáticos y Redes', 5, 'MEDIO'),
('CFGS-DAM', 'DAM', 'Desarrollo de Aplicaciones Multiplataforma', 5, 'SUPERIOR'),
('CFGS-DAW', 'DAW', 'Desarrollo de Aplicaciones Web', 5, 'SUPERIOR'),
('CFGS-ASIR', 'ASIR', 'Administración de Sistemas Informáticos en Red', 5, 'SUPERIOR'),
('CE-CETI', 'CETI', 'Ciberseguridad en Entornos de las TI', 5, 'ESPECIALIZACION'),
('CE-IABD', 'IABD', 'Inteligencia Artificial y Big Data', 5, 'ESPECIALIZACION'),

-- Sanidad (SAN) - id: 6
('CFGM-CAE', 'CAE', 'Cuidados Auxiliares de Enfermería', 6, 'MEDIO'),
('CFGM-EMER', 'EMER', 'Emergencias Sanitarias', 6, 'MEDIO'),

-- Seguridad y Medio Ambiente (SYM) - id: 7
('CFGS-PRP', 'PRP', 'Prevención de Riesgos Profesionales', 7, 'SUPERIOR'),

-- Servicios Socioculturales y a la Comunidad (SSC) - id: 8
('CFGM-APD', 'APD', 'Atención a Personas en Situación de Dependencia', 8, 'MEDIO'),
('CFGS-EIN', 'EIN', 'Educación Infantil', 8, 'SUPERIOR'),
('CFGS-ITE', 'ITE', 'Integración Social', 8, 'SUPERIOR'),
('CFGS-TASOCT', 'TASOCT', 'Animación Sociocultural y Turística', 8, 'SUPERIOR'),

-- Madera, Mueble y Corcho (MMC) - id: 9
('CFGM-INA', 'INA', 'Instalación y Amueblamiento', 9, 'MEDIO');

-- Usuarios de ejemplo (password: 123456)
INSERT INTO usuarios (email, password_hash, nif, nombre, apellidos, rol, departamento_id) VALUES
-- Profesores (uno por cada departamento)
('juan.garcia@cifpvillaaguimes.es', '$2b$10$hash_simulado', '12345678A', 'Juan', 'García López', 'PROFESOR', 5),
('ana.martinez@cifpvillaaguimes.es', '$2b$10$hash_simulado', '23456789B', 'Ana', 'Martínez Ruiz', 'PROFESOR', 5),
('pedro.sanchez@cifpvillaaguimes.es', '$2b$10$hash_simulado', '34567890C', 'Pedro', 'Sánchez Vega', 'PROFESOR', 2),
('maria.lopez@cifpvillaaguimes.es', '$2b$10$hash_simulado', '45678901D', 'María', 'López Díaz', 'PROFESOR', 4),
('carlos.rodriguez@cifpvillaaguimes.es', '$2b$10$hash_simulado', '56789012E', 'Carlos', 'Rodríguez Pérez', 'PROFESOR', 1),
('laura.fernandez@cifpvillaaguimes.es', '$2b$10$hash_simulado', '67890123F', 'Laura', 'Fernández Soto', 'PROFESOR', 6),
('jose.gonzalez@cifpvillaaguimes.es', '$2b$10$hash_simulado', '78901234G', 'José', 'González Cruz', 'PROFESOR', 3),
('carmen.diaz@cifpvillaaguimes.es', '$2b$10$hash_simulado', '89012345H', 'Carmen', 'Díaz Navarro', 'PROFESOR', 8),
('antonio.ruiz@cifpvillaaguimes.es', '$2b$10$hash_simulado', '90123456I', 'Antonio', 'Ruiz Molina', 'PROFESOR', 7),
('elena.torres@cifpvillaaguimes.es', '$2b$10$hash_simulado', '01234567J', 'Elena', 'Torres Ramos', 'PROFESOR', 9),

-- Alumnos
('alumno.dam@cifpvillaaguimes.es', '$2b$10$hash_simulado', '11111111A', 'Pablo', 'Hernández Gil', 'ALUMNO', 5),
('alumno.daw@cifpvillaaguimes.es', '$2b$10$hash_simulado', '22222222B', 'Sara', 'Moreno Ruiz', 'ALUMNO', 5),
('alumno.smr@cifpvillaaguimes.es', '$2b$10$hash_simulado', '33333333C', 'David', 'Jiménez López', 'ALUMNO', 5),
('alumno.coc@cifpvillaaguimes.es', '$2b$10$hash_simulado', '44444444D', 'Lucía', 'Alonso Martín', 'ALUMNO', 4),
('alumno.gad@cifpvillaaguimes.es', '$2b$10$hash_simulado', '55555555E', 'Daniel', 'Romero Sánchez', 'ALUMNO', 2),
('alumno.cae@cifpvillaaguimes.es', '$2b$10$hash_simulado', '66666666F', 'Marta', 'Navarro Pérez', 'ALUMNO', 6),
('alumno.ein@cifpvillaaguimes.es', '$2b$10$hash_simulado', '77777777G', 'Sergio', 'Vega Torres', 'ALUMNO', 8),
('alumno.ener@cifpvillaaguimes.es', '$2b$10$hash_simulado', '88888888H', 'Claudia', 'Prieto Ramos', 'ALUMNO', 3);

-- Empresas colaboradoras
INSERT INTO empresas (cif, nombre, direccion, ciudad, codigo_postal, telefono, email, persona_contacto, telefono_contacto, descripcion, created_by) VALUES
-- Empresas de Informática
('A12345678', 'TechSolutions Canarias', 'Calle Mayor 123', 'Las Palmas de Gran Canaria', '35001', '928123456', 'contacto@techsolutions.es', 'Roberto Méndez', '628123456', 'Empresa de desarrollo de software especializada en soluciones empresariales y aplicaciones móviles.', 1),
('B23456789', 'Canary Software SL', 'Av. Mesa y López 45', 'Las Palmas de Gran Canaria', '35010', '928234567', 'info@canarysoftware.com', 'Elena Torres', '629234567', 'Consultora tecnológica con más de 15 años de experiencia en el sector.', 1),
('C34567890', 'DataCenter Islas', 'Polígono Arinaga, Nave 15', 'Agüimes', '35118', '928678901', 'comercial@datacenterislas.com', 'Alberto Díaz', '633678901', 'Centro de datos y servicios cloud para empresas canarias.', 1),
('D45678901', 'CiberSegur GC', 'Calle Primero de Mayo 88', 'Las Palmas de Gran Canaria', '35002', '928111222', 'info@cibersegurgc.com', 'Patricia Ramos', '634111222', 'Empresa especializada en ciberseguridad y auditorías de sistemas.', 2),

-- Empresas de Hostelería
('E56789012', 'Hotel Gran Canaria Palace', 'Paseo de Las Canteras 88', 'Las Palmas de Gran Canaria', '35008', '928345678', 'rrhh@grancanariapalace.com', 'Carmen Suárez', '630345678', 'Hotel de 5 estrellas con restaurante galardonado y amplia oferta gastronómica.', 4),
('F67890123', 'Restaurante La Marinera', 'Puerto de Mogán 12', 'Mogán', '35138', '928789012', 'reservas@lamarinera.es', 'José Antonio Pérez', '634789012', 'Restaurante especializado en cocina canaria y pescado fresco.', 4),
('G78901234', 'Pastelería Dulce Canarias', 'Calle Triana 78', 'Las Palmas de Gran Canaria', '35002', '928222333', 'info@dulcecanarias.es', 'Rosa María López', '635222333', 'Pastelería artesanal con productos tradicionales canarios.', 4),

-- Empresas de Administración
('H89012345', 'Administraciones Atlántico', 'Calle Triana 56', 'Las Palmas de Gran Canaria', '35002', '928456789', 'admin@atlantico.es', 'Francisco Vera', '631456789', 'Gestoría y asesoría fiscal con servicios integrales para empresas.', 3),
('I90123456', 'Consulting Gran Canaria', 'Av. José Mesa y López 100', 'Las Palmas de Gran Canaria', '35010', '928333444', 'contacto@consultinggc.com', 'Raquel Santana', '636333444', 'Consultoría empresarial especializada en PYMES.', 3),

-- Empresas de Sanidad
('J01234567', 'Clínica Dental Smile', 'Calle León y Castillo 200', 'Las Palmas de Gran Canaria', '35004', '928567890', 'info@dentalsmile.es', 'Dra. Patricia Ruiz', '632567890', 'Clínica dental moderna con todas las especialidades odontológicas.', 6),
('K12345679', 'Residencia Años Dorados', 'Calle El Doctoral 45', 'Agüimes', '35260', '928444555', 'direccion@anosdorados.es', 'Manuel García', '637444555', 'Residencia de mayores con atención especializada 24 horas.', 6),

-- Empresas de Energía
('L23456780', 'Renovables del Sur', 'Polígono Industrial Arinaga', 'Agüimes', '35118', '928555666', 'info@renovablesdelsur.es', 'Pedro Cabrera', '638555666', 'Empresa instaladora de sistemas de energía solar y eólica.', 7),
('M34567891', 'AguaCanarias', 'Calle Real de San Fernando 30', 'Las Palmas de Gran Canaria', '35003', '928666777', 'contacto@aguacanarias.com', 'Isabel Medina', '639666777', 'Gestión integral del ciclo del agua.', 7),

-- Empresas de Servicios Sociales
('N45678902', 'Guardería Pequeños Pasos', 'Calle Alcalde Henríquez Pitti 15', 'Agüimes', '35260', '928777888', 'info@pequenospasos.es', 'Ana Belén Rodríguez', '640777888', 'Centro de educación infantil de 0 a 3 años.', 8),
('O56789013', 'Centro de Día El Sol', 'Av. de los Artesanos 25', 'Ingenio', '35250', '928888999', 'direccion@centrodiasol.es', 'Fernando Suárez', '641888999', 'Centro de día para personas mayores y dependientes.', 8),

-- Empresas de Actividades Físicas
('P67890124', 'Gimnasio FitLife', 'Centro Comercial Las Arenas', 'Las Palmas de Gran Canaria', '35010', '928999000', 'info@fitlifegym.es', 'Carlos Betancor', '642999000', 'Gimnasio con las últimas tecnologías en fitness y bienestar.', 5),

-- Empresas de Madera
('Q78901235', 'Carpintería Artesanal GC', 'Polígono Industrial Salinetas', 'Telde', '35214', '928000111', 'info@carpinteriaartesanalgc.es', 'Juan Miguel Pérez', '643000111', 'Carpintería especializada en muebles a medida y restauración.', 10),

-- Empresas de Seguridad
('R89012346', 'Prevención Total SL', 'Calle Secretario Artiles 50', 'Las Palmas de Gran Canaria', '35004', '928111000', 'contacto@prevenciontotal.es', 'Alejandro Vega', '644111000', 'Servicios integrales de prevención de riesgos laborales.', 9);

-- Relación empresas-cursos
INSERT INTO empresa_cursos (empresa_id, curso_id) VALUES
-- TechSolutions: DAM, DAW, ASIR, SMR
(1, 16), (1, 17), (1, 18), (1, 15),
-- Canary Software: DAM, DAW
(2, 16), (2, 17),
-- DataCenter: ASIR, SMR, DAW, CETI
(3, 18), (3, 15), (3, 17), (3, 19),
-- CiberSegur: CETI, ASIR, IABD
(4, 19), (4, 18), (4, 20),
-- Hotel Gran Canaria: COC, SERV, DCOC, DSERV, ALOJ
(5, 10), (5, 9), (5, 13), (5, 14), (5, 12),
-- La Marinera: COC, DCOC
(6, 10), (6, 13),
-- Dulce Canarias: PAST
(7, 11),
-- Administraciones: FIN, GAD, ADIR
(8, 4), (8, 3), (8, 5),
-- Consulting GC: FIN, ADIR
(9, 4), (9, 5),
-- Clínica Dental: CAE
(10, 21),
-- Residencia: CAE, APD
(11, 21), (11, 24),
-- Renovables: ENER, EFIC
(12, 6), (12, 7),
-- AguaCanarias: RET, ENER
(13, 8), (13, 6),
-- Guardería: EIN
(14, 25),
-- Centro de Día: APD, ITE
(15, 24), (15, 26),
-- FitLife: AFI, EAS
(16, 1), (16, 2),
-- Carpintería: INA
(17, 28),
-- Prevención Total: PRP
(18, 23);

-- Empresas contactadas
INSERT INTO empresa_contactada (empresa_id, departamento_id, profesor_id, fecha, notas) VALUES
(1, 5, 1, '2025-01-15', 'Muy interesados en alumnos de DAM y DAW. Posibilidad de contratación.'),
(2, 5, 2, '2025-01-18', 'Contacto inicial positivo. Enviar currículums.'),
(3, 5, 1, '2025-01-20', 'Nueva colaboración. Interesados en perfiles de sistemas.'),
(4, 5, 2, '2025-01-22', 'Empresa especializada en ciberseguridad. Ideal para CETI.'),
(5, 4, 4, '2025-01-10', 'Colaboración habitual. Excelente feedback de años anteriores.'),
(6, 4, 4, '2025-01-12', 'Restaurante familiar muy comprometido con la formación.'),
(8, 2, 3, '2025-01-08', 'Gestoría de confianza. Buenos resultados anteriores.'),
(10, 6, 6, '2025-01-14', 'Clínica moderna. Formación práctica de calidad.'),
(11, 6, 6, '2025-01-16', 'Residencia con mucha demanda de auxiliares.'),
(11, 8, 8, '2025-01-17', 'También necesitan alumnos de APD.'),
(12, 3, 7, '2025-01-19', 'Sector en auge. Buenas perspectivas.'),
(14, 8, 8, '2025-01-21', 'Guardería con excelente equipo pedagógico.'),
(16, 1, 5, '2025-01-23', 'Gimnasio interesado en monitores deportivos.'),
(18, 7, 9, '2025-01-25', 'Empresa líder en PRL en la isla.');

-- Plazas
INSERT INTO plazas (empresa_id, departamento_id, curso_id, cantidad, es_general, curso_academico, created_by) VALUES
-- TechSolutions: 5 plazas generales para Informática
(1, 5, NULL, 5, TRUE, '2024-2025', 1),
-- Canary Software: 2 DAM, 2 DAW
(2, 5, 16, 2, FALSE, '2024-2025', 2),
(2, 5, 17, 2, FALSE, '2024-2025', 2),
-- DataCenter: 4 plazas generales
(3, 5, NULL, 4, TRUE, '2024-2025', 1),
-- CiberSegur: 2 CETI
(4, 5, 19, 2, FALSE, '2024-2025', 2),
-- Hotel: 6 plazas generales hostelería
(5, 4, NULL, 6, TRUE, '2024-2025', 4),
-- La Marinera: 2 COC, 1 DCOC
(6, 4, 10, 2, FALSE, '2024-2025', 4),
(6, 4, 13, 1, FALSE, '2024-2025', 4),
-- Administraciones: 3 FIN, 2 GAD
(8, 2, 4, 3, FALSE, '2024-2025', 3),
(8, 2, 3, 2, FALSE, '2024-2025', 3),
-- Clínica: 2 CAE
(10, 6, 21, 2, FALSE, '2024-2025', 6),
-- Residencia: 3 CAE, 2 APD
(11, 6, 21, 3, FALSE, '2024-2025', 6),
(11, 8, 24, 2, FALSE, '2024-2025', 8),
-- Renovables: 3 plazas generales
(12, 3, NULL, 3, TRUE, '2024-2025', 7),
-- Guardería: 4 EIN
(14, 8, 25, 4, FALSE, '2024-2025', 8),
-- FitLife: 2 AFI, 2 EAS
(16, 1, 1, 2, FALSE, '2024-2025', 5),
(16, 1, 2, 2, FALSE, '2024-2025', 5),
-- Prevención: 2 PRP
(18, 7, 23, 2, FALSE, '2024-2025', 9);

-- Reservas
INSERT INTO reservas (plaza_id, profesor_id, curso_id, cantidad, clase, estado, notas) VALUES
(1, 1, 16, 2, '2A', 'CONFIRMADA', 'Alumnos con buen expediente'),
(1, 2, 17, 1, '2A', 'PENDIENTE', NULL),
(2, 2, 16, 1, '2B', 'CONFIRMADA', NULL),
(5, 2, 19, 1, '1A', 'PENDIENTE', 'Alumno muy interesado en ciberseguridad'),
(6, 4, 10, 2, '2A', 'CONFIRMADA', 'Para prácticas de verano'),
(9, 3, 4, 2, '2A', 'CONFIRMADA', 'Alumnos motivados'),
(11, 6, 21, 2, '2A', 'CONFIRMADA', NULL),
(14, 8, 25, 2, '2A', 'PENDIENTE', 'Preferencia por turno de mañana'),
(15, 5, 1, 1, '2A', 'CONFIRMADA', NULL);

-- Comentarios
INSERT INTO comentarios (empresa_id, usuario_id, texto, es_privado) VALUES
-- Comentarios públicos
(1, 11, 'Hice las prácticas aquí y fue una experiencia genial. Muy buen ambiente de trabajo y tecnologías modernas.', FALSE),
(1, 12, 'Los tutores son muy atentos y te enseñan mucho. Recomendado 100%.', FALSE),
(2, 11, 'Buena empresa para aprender tecnologías modernas. El equipo es muy profesional.', FALSE),
(5, 14, 'Excelente lugar para prácticas de hostelería. Aprendes de todo y el chef es muy buen maestro.', FALSE),
(8, 15, 'Muy buena experiencia en administración. Te forman bien y el ambiente es familiar.', FALSE),
(10, 16, 'Clínica muy moderna y profesional. Los doctores explican todo muy bien.', FALSE),
(14, 17, 'Las educadoras son maravillosas y aprendes muchísimo sobre educación infantil.', FALSE),

-- Comentarios privados (solo profesores)
(1, 1, 'Empresa muy comprometida con la formación. Siempre cumplen los objetivos del programa.', TRUE),
(2, 2, 'Tuvimos un problema con horarios el año pasado, pero lo solucionaron rápidamente.', TRUE),
(5, 4, 'Hotel de referencia para nuestros alumnos. Muy profesionales y buena formación.', TRUE),
(8, 3, 'Colaboración excelente desde hace 5 años. Muy recomendable para alumnos de administración.', TRUE),
(11, 6, 'Residencia con mucha carga de trabajo pero muy formativa. Preparar bien a los alumnos.', TRUE),
(11, 8, 'Buen trato a los alumnos de APD. Personal muy implicado en la formación.', TRUE);

-- Valoraciones
INSERT INTO valoraciones (empresa_id, usuario_id, puntuacion) VALUES
(1, 11, 5),
(1, 12, 4),
(1, 1, 5),
(2, 11, 4),
(2, 2, 4),
(3, 1, 4),
(5, 14, 5),
(5, 4, 5),
(6, 14, 4),
(8, 15, 5),
(8, 3, 5),
(10, 16, 5),
(11, 16, 4),
(14, 17, 5),
(16, 5, 4);

-- Favoritos
INSERT INTO favoritos (usuario_id, empresa_id) VALUES
(11, 1),
(11, 2),
(11, 3),
(12, 1),
(12, 4),
(14, 5),
(14, 6),
(15, 8),
(16, 10),
(17, 14);

-- Listas personalizadas adicionales
INSERT INTO listas (usuario_id, nombre, descripcion, es_favoritos) VALUES
(11, 'Para enviar CV', 'Empresas a las que quiero enviar mi currículum', FALSE),
(12, 'Cerca de casa', 'Empresas que me quedan bien de ubicación', FALSE),
(14, 'Hoteles 5 estrellas', 'Hoteles de alta categoría para prácticas', FALSE);

-- Empresas en listas
INSERT INTO lista_empresas (lista_id, empresa_id, notas) VALUES
(19, 1, 'Enviar CV actualizado con proyectos'),
(19, 2, 'Preguntar por ofertas de empleo'),
(19, 3, 'Interesa por el tema cloud'),
(19, 4, 'Para después del curso de CETI'),
(20, 3, 'En Arinaga, muy cerca'),
(20, 8, 'A 10 min en guagua'),
(21, 5, 'El mejor hotel para prácticas');

-- =====================================================
-- CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Ver todos los ciclos por departamento
-- SELECT d.nombre AS departamento, c.siglas, c.nombre, c.grado 
-- FROM cursos c JOIN departamentos d ON c.departamento_id = d.id 
-- ORDER BY d.nombre, c.grado, c.siglas;

-- Ver empresas con su valoración media
-- SELECT * FROM v_empresas_valoracion ORDER BY valoracion_media DESC;

-- Ver plazas disponibles
-- SELECT * FROM v_plazas_disponibles WHERE plazas_libres > 0;

-- Ver empresas contactadas por departamento
-- SELECT * FROM v_empresas_contactadas ORDER BY departamento_nombre, fecha DESC;