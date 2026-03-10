// src/services/departamentoService.ts
import { api } from '../config/api';
import { Departamento, Curso } from '../types';

export const departamentoService = {
  getAll: () => api.get<Departamento[]>('/departamentos'),

  getCursos: (departamentoId: number) =>
    api.get<Curso[]>(`/cursos/departamento/${departamentoId}`),

  getAllCursos: () => api.get<Curso[]>('/cursos'),
};
