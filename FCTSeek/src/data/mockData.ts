// src/data/mockData.ts

import { Departamento, Curso, Empresa, Usuario, Comentario, Lista } from '../types';

// Usuario actual (simulado)
export const currentUser: Usuario = {
  id: 1,
  nif: '12345678A',
  nombre: 'Juan',
  apellidos: 'García López',
  email: 'juan.garcia@cifpvillaaguimes.es',
  rol: 'PROFESOR',
};

// Departamentos
export const departamentos: Departamento[] = [
  { id: 1, nombre: 'Informática y Comunicaciones', codigo: 'INF' },
  { id: 2, nombre: 'Hostelería y Turismo', codigo: 'HOT' },
  { id: 3, nombre: 'Administración y Gestión', codigo: 'ADM' },
];

// Cursos
export const cursos: Curso[] = [
  { id: 1, nombre: 'Desarrollo de Aplicaciones Multiplataforma', siglas: 'DAM', grado: 'Superior', departamentoId: 1 },
  { id: 2, nombre: 'Desarrollo de Aplicaciones Web', siglas: 'DAW', grado: 'Superior', departamentoId: 1 },
  { id: 3, nombre: 'Administración de Sistemas Informáticos en Red', siglas: 'ASIR', grado: 'Superior', departamentoId: 1 },
  { id: 4, nombre: 'Sistemas Microinformáticos y Redes', siglas: 'SMR', grado: 'Medio', departamentoId: 1 },
  { id: 5, nombre: 'Dirección de Cocina', siglas: 'DC', grado: 'Superior', departamentoId: 2 },
  { id: 6, nombre: 'Administración y Finanzas', siglas: 'AF', grado: 'Superior', departamentoId: 3 },
];

// Interfaz para ContactadaPor con plazas
export interface ContactadaPor {
  departamentoId: number;
  departamentoNombre: string;
  profesorId: number;
  profesorNombre: string;
  fecha: string;
  nota?: string;
  plazasOfertadas: number;
  esGeneral: boolean;  // true = plazas para cualquier ciclo del depto
}

// Interfaz para reservas de plazas
export interface ReservaPlaza {
  id: number;
  empresaId: number;
  departamentoId: number;
  profesorId: number;
  profesorNombre: string;
  cantidad: number;
  cursoId?: number;       // undefined/null = reserva de plaza general
  cursoSiglas?: string;
  clase?: string;
  anioAcademico: string;
}

// Reservas de plazas
export const reservasPlazas: ReservaPlaza[] = [
  {
    id: 1,
    empresaId: 1,
    departamentoId: 1,
    profesorId: 1,
    profesorNombre: 'Juan García',
    cantidad: 2,
    cursoId: 1,
    cursoSiglas: 'DAM',
    clase: '2A',
    anioAcademico: '2024-2025',
  },
  {
    id: 2,
    empresaId: 4,
    departamentoId: 1,
    profesorId: 2,
    profesorNombre: 'Ana Martín',
    cantidad: 1,
    // Sin cursoId = plaza general asignada a ASIR
    cursoSiglas: 'ASIR',
    anioAcademico: '2024-2025',
  },
];

// Empresas
export const empresas: Empresa[] = [
  {
    id: 1,
    nombre: 'TechSolutions Canarias S.L.',
    telefono: '928 123 456',
    email: 'info@techsolutions.es',
    web: 'www.techsolutions.es',
    personaContacto: 'María Rodríguez',
    telefonoContacto: '666 123 456',
    direccion: 'C/ León y Castillo, 45',
    ciudad: 'Las Palmas de Gran Canaria',
    codigoPostal: '35003',
    provincia: 'Las Palmas',
    descripcion: 'Empresa dedicada al desarrollo de software y aplicaciones móviles.',
    activa: true,
    departamentos: [departamentos[0]],
    cursos: [cursos[0], cursos[1]],
    valoracionMedia: 4.5,
    totalValoraciones: 12,
    contactadaPor: [
      {
        departamentoId: 1,
        departamentoNombre: 'Informática y Comunicaciones',
        profesorId: 1,
        profesorNombre: 'Juan García',
        fecha: '2025-01-15',
        nota: 'Hablé con María, muy interesados en alumnos de DAM',
        plazasOfertadas: 5,
        esGeneral: false,  // Plazas específicas para DAM
      },
    ],
  },
  {
    id: 2,
    nombre: 'Hotel Gran Canaria Palace',
    telefono: '928 234 567',
    email: 'rrhh@grancanariapalace.com',
    web: 'www.grancanariapalace.com',
    personaContacto: 'Carlos Santana',
    telefonoContacto: '666 234 567',
    direccion: 'Av. de las Canteras, 100',
    ciudad: 'Las Palmas de Gran Canaria',
    codigoPostal: '35010',
    provincia: 'Las Palmas',
    descripcion: 'Hotel de 5 estrellas con restaurante galardonado.',
    activa: true,
    departamentos: [departamentos[1]],
    cursos: [cursos[4]],
    valoracionMedia: 4.2,
    totalValoraciones: 8,
    contactadaPor: [
      {
        departamentoId: 2,
        departamentoNombre: 'Hostelería y Turismo',
        profesorId: 2,
        profesorNombre: 'Ana Martín',
        fecha: '2025-01-10',
        nota: 'Buscan alumnos para cocina',
        plazasOfertadas: 3,
        esGeneral: true,  // Cualquier ciclo de Hostelería
      },
    ],
  },
  {
    id: 3,
    nombre: 'Gestión Integral Canaria',
    telefono: '928 345 678',
    email: 'contacto@gestionintegral.es',
    personaContacto: 'Laura Pérez',
    direccion: 'C/ Triana, 22',
    ciudad: 'Las Palmas de Gran Canaria',
    codigoPostal: '35002',
    provincia: 'Las Palmas',
    descripcion: 'Asesoría fiscal, laboral y contable.',
    activa: true,
    departamentos: [departamentos[2]],
    cursos: [cursos[5]],
    valoracionMedia: 3.8,
    totalValoraciones: 5,
    contactadaPor: [],
  },
  {
    id: 4,
    nombre: 'DataCenter Islas',
    telefono: '928 456 789',
    email: 'info@datacenterislas.com',
    web: 'www.datacenterislas.com',
    personaContacto: 'Pedro Alonso',
    telefonoContacto: '666 456 789',
    direccion: 'Polígono Industrial Arinaga, Nave 15',
    ciudad: 'Agüimes',
    codigoPostal: '35118',
    provincia: 'Las Palmas',
    descripcion: 'Servicios de hosting, cloud computing y administración de sistemas.',
    activa: true,
    departamentos: [departamentos[0]],
    cursos: [cursos[2], cursos[3]],
    valoracionMedia: 4.8,
    totalValoraciones: 15,
    contactadaPor: [
      {
        departamentoId: 1,
        departamentoNombre: 'Informática y Comunicaciones',
        profesorId: 1,
        profesorNombre: 'Juan García',
        fecha: '2025-01-20',
        nota: 'Aceptan cualquier ciclo de informática',
        plazasOfertadas: 4,
        esGeneral: true,  // Cualquier ciclo de Informática
      },
    ],
  },
  {
    id: 5,
    nombre: 'Restaurante El Marinero',
    telefono: '928 567 890',
    email: 'reservas@elmarinero.es',
    personaContacto: 'José Medina',
    direccion: 'Puerto de Mogán, s/n',
    ciudad: 'Mogán',
    codigoPostal: '35138',
    provincia: 'Las Palmas',
    descripcion: 'Restaurante especializado en cocina marinera canaria.',
    activa: true,
    departamentos: [departamentos[1]],
    cursos: [cursos[4]],
    valoracionMedia: 4.0,
    totalValoraciones: 6,
    contactadaPor: [],
  },
  {
    id: 6,
    nombre: 'AppDev Studio',
    telefono: '928 678 901',
    email: 'hello@appdevstudio.es',
    web: 'www.appdevstudio.es',
    personaContacto: 'Lucía Hernández',
    telefonoContacto: '666 678 901',
    direccion: 'C/ Mesa y López, 60',
    ciudad: 'Las Palmas de Gran Canaria',
    codigoPostal: '35006',
    provincia: 'Las Palmas',
    descripcion: 'Startup de desarrollo de apps móviles y diseño UX/UI.',
    activa: true,
    departamentos: [departamentos[0]],
    cursos: [cursos[0], cursos[1]],
    valoracionMedia: 4.7,
    totalValoraciones: 20,
    contactadaPor: [],
  },
{
  id: 7,
  nombre: 'Hotel Resort & Tech',
  telefono: '928 111 222',
  email: 'info@hoteltech.com',
  personaContacto: 'Antonio Ruiz',
  direccion: 'Av. del Mar, 50',
  ciudad: 'Maspalomas',
  codigoPostal: '35100',
  provincia: 'Las Palmas',
  descripcion: 'Hotel con sistema de domótica propio, buscan perfiles técnicos y de hostelería.',
  activa: true,
  // Dos departamentos
  departamentos: [departamentos[0], departamentos[1]], // INF y HOT
  cursos: [cursos[0], cursos[2], cursos[4]], // DAM, ASIR, DC
  valoracionMedia: 4.3,
  totalValoraciones: 7,
  // Contactada por DOS profesores de diferentes departamentos
  contactadaPor: [
    {
      departamentoId: 1,
      departamentoNombre: 'Informática y Comunicaciones',
      profesorId: 1,
      profesorNombre: 'Juan García',
      fecha: '2025-01-18',
      nota: 'Buscan 3 alumnos para mantener su sistema domótico',
      plazasOfertadas: 3,
      esGeneral: false, // Solo ASIR
    },
    {
      departamentoId: 2,
      departamentoNombre: 'Hostelería y Turismo',
      profesorId: 2,
      profesorNombre: 'Ana Martín',
      fecha: '2025-01-20',
      nota: 'Necesitan alumnos para el restaurante del hotel',
      plazasOfertadas: 4,
      esGeneral: true, // Cualquier ciclo de hostelería
    },
  ],
},
];

// Comentarios
export const comentarios: Comentario[] = [
  {
    id: 1,
    empresaId: 1,
    usuarioId: 1,
    usuarioNombre: 'Juan García',
    texto: 'Excelente trato con los alumnos. El tutor de empresa es muy profesional.',
    fecha: '2024-06-15',
    esPrivado: false,
  },
  {
    id: 2,
    empresaId: 1,
    usuarioId: 2,
    usuarioNombre: 'María López',
    texto: 'OJO: Piden que los alumnos tengan conocimientos de React Native antes de empezar.',
    fecha: '2024-09-20',
    esPrivado: true,
  },
  {
    id: 3,
    empresaId: 4,
    usuarioId: 1,
    usuarioNombre: 'Juan García',
    texto: 'Muy buena experiencia. Los alumnos aprenden mucho sobre infraestructura.',
    fecha: '2024-05-10',
    esPrivado: false,
  },
];

// Listas del usuario
export const listas: Lista[] = [
  {
    id: 1,
    nombre: 'Favoritos',
    usuarioId: 1,
    esFavoritos: true,
    empresas: [empresas[0], empresas[3]],
  },
  {
    id: 2,
    nombre: 'Para contactar',
    usuarioId: 1,
    esFavoritos: false,
    empresas: [empresas[5]],
  },
  {
    id: 3,
    nombre: 'FCT 2025',
    usuarioId: 1,
    esFavoritos: false,
    empresas: [empresas[0], empresas[3], empresas[5]],
  },
];

// Funciones de búsqueda
export function buscarEmpresas(texto: string, departamentoId?: number): Empresa[] {
  return empresas.filter((emp) => {
    const coincideTexto = texto
      ? emp.nombre.toLowerCase().includes(texto.toLowerCase()) ||
        emp.ciudad.toLowerCase().includes(texto.toLowerCase())
      : true;

    const coincideDepto = departamentoId
      ? emp.departamentos.some((d) => d.id === departamentoId)
      : true;

    return coincideTexto && coincideDepto && emp.activa;
  });
}

export function getEmpresaById(id: number): Empresa | undefined {
  return empresas.find((emp) => emp.id === id);
}

export function getComentariosEmpresa(empresaId: number, incluirPrivados: boolean): Comentario[] {
  return comentarios.filter(
    (c) => c.empresaId === empresaId && (incluirPrivados || !c.esPrivado)
  );
}

// Funciones para plazas
export function getPlazasDisponibles(empresaId: number, departamentoId: number): {
  ofertadas: number;
  reservadas: number;
  libres: number;
  esGeneral: boolean;
} {
  const empresa = empresas.find(e => e.id === empresaId);
  if (!empresa || !empresa.contactadaPor) {
    return { ofertadas: 0, reservadas: 0, libres: 0, esGeneral: false };
  }

  const contacto = empresa.contactadaPor.find(c => c.departamentoId === departamentoId);
  if (!contacto) {
    return { ofertadas: 0, reservadas: 0, libres: 0, esGeneral: false };
  }

  const reservas = reservasPlazas.filter(
    r => r.empresaId === empresaId && r.departamentoId === departamentoId
  );
  const reservadas = reservas.reduce((sum, r) => sum + r.cantidad, 0);

  return {
    ofertadas: contacto.plazasOfertadas,
    reservadas,
    libres: contacto.plazasOfertadas - reservadas,
    esGeneral: contacto.esGeneral,
  };
}

export function getReservasEmpresa(empresaId: number, departamentoId: number): ReservaPlaza[] {
  return reservasPlazas.filter(
    r => r.empresaId === empresaId && r.departamentoId === departamentoId
  );
}

// Obtener cursos de un departamento
export function getCursosByDepartamento(departamentoId: number): Curso[] {
  return cursos.filter(c => c.departamentoId === departamentoId);
}