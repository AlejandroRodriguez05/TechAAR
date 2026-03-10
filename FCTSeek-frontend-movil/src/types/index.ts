// src/types/index.ts

export interface Usuario {
  id: number;
  nif: string;
  nombre: string;
  apellidos: string;
  email: string;
  rol: 'PROFESOR' | 'ALUMNO';
}

export interface Departamento {
  id: number;
  nombre: string;
  codigo: string;
}

export interface Curso {
  id: number;
  departamentoId: number;
  nombre: string;
  siglas: string;
  grado: 'MEDIO' | 'SUPERIOR';
}

export interface Empresa {
  id: number;
  nombre: string;
  cif?: string;
  telefono?: string;
  email?: string;
  web?: string;
  personaContacto?: string;
  telefonoContacto?: string;
  direccion?: string;
  ciudad: string;
  codigoPostal?: string;
  provincia?: string;
  descripcion?: string;
  activa: boolean;
  departamentos: Departamento[];
  cursos: Curso[];
  valoracionMedia?: number;
  totalValoraciones?: number;
  contactadaPor?: ContactadaPor[];
}

export interface ContactadaPor {
  departamentoId: number;
  departamentoNombre: string;
  profesorId: number;
  profesorNombre: string;
  fecha: string;
  nota?: string;
  plazasOfertadas: number;
  esGeneral: boolean;
}

export interface Valoracion {
  id: number;
  empresaId: number;
  usuarioId: number;
  puntuacion: number;
  rolValorador: 'PROFESOR' | 'ALUMNO';
  anioAcademico?: string;
}

export interface Comentario {
  id: number;
  empresaId: number;
  usuarioId: number;
  usuarioNombre: string;
  texto: string;
  esPrivado: boolean;
  fecha: string;
}

export interface Lista {
  id: number;
  nombre: string;
  usuarioId: number;
  esFavoritos: boolean;
  empresas: Empresa[];
}

export interface ReservaPlaza {
  id: number;
  empresaId: number;
  departamentoId: number;
  profesorId: number;
  profesorNombre: string;
  cantidad: number;
  cursoId?: number;
  cursoSiglas?: string;
  clase?: string;
  anioAcademico: string;
}

export interface PlazaCurso {
  cursoId: number | null;
  cursoSiglas: string;
  cantidad: number;
}