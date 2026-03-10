// src/services/reservaService.ts
import { api } from '../config/api';
import { ReservaPlaza } from '../types';

export interface ReservaRequest {
  plazaId: number;
  cursoId: number;
  cantidad: number;
  clase?: string;
  notas?: string;
}

export const reservaService = {
  getByEmpresa: (empresaId: number) =>
    api.get<ReservaPlaza[]>(`/reservas/empresa/${empresaId}`),

  getMisReservas: () =>
    api.get<ReservaPlaza[]>('/reservas/mis-reservas'),

  crear: (data: ReservaRequest) =>
    api.post<ReservaPlaza>('/reservas', data),

  confirmar: (id: number) =>
    api.put<ReservaPlaza>(`/reservas/${id}/confirmar`),

  cancelar: (id: number) =>
    api.put<ReservaPlaza>(`/reservas/${id}/cancelar`),

  eliminar: (id: number) => api.delete<void>(`/reservas/${id}`),
};
