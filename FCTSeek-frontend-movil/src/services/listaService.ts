// src/services/listaService.ts
import { api } from '../config/api';
import { Lista } from '../types';

export interface ListaRequest {
  nombre: string;
}

export const listaService = {
  getMisListas: () => api.get<Lista[]>('/listas'),

  getById: (id: number) => api.get<Lista>(`/listas/${id}`),

  crear: (data: ListaRequest) => api.post<Lista>('/listas', data),

  eliminar: (id: number) => api.delete<void>(`/listas/${id}`),

  addEmpresa: (listaId: number, empresaId: number) =>
    api.post<Lista>(`/listas/${listaId}/empresas/${empresaId}`),

  removeEmpresa: (listaId: number, empresaId: number) =>
    api.delete<void>(`/listas/${listaId}/empresas/${empresaId}`),
};
