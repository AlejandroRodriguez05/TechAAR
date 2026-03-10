// src/services/empresaService.ts
import { api } from '../config/api';
import { Empresa } from '../types';

export const empresaService = {
  getAll: () => api.get<Empresa[]>('/empresas'),

  getById: (id: number) => api.get<Empresa>(`/empresas/${id}`),

  buscar: (query: string, departamentoId?: number) => {
    let endpoint = `/empresas/buscar?q=${encodeURIComponent(query)}`;
    if (departamentoId) endpoint += `&departamentoId=${departamentoId}`;
    return api.get<Empresa[]>(endpoint);
  },

  crear: (data: Partial<Empresa>) => api.post<Empresa>('/empresas', data),

  actualizar: (id: number, data: Partial<Empresa>) => api.put<Empresa>(`/empresas/${id}`, data),

  eliminar: (id: number) => api.delete<void>(`/empresas/${id}`),
};
