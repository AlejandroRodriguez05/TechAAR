// src/services/plazaService.ts
import { api } from '../config/api';

export interface PlazaResponse {
  id: number;
  empresaId: number;
  empresaNombre: string;
  departamentoId: number;
  departamentoNombre: string;
  cursoId?: number;
  cursoSiglas?: string;
  cursoNombre?: string;
  cantidad: number;
  esGeneral: boolean;
  cursoAcademico: string;
  plazasReservadas: number;
  plazasDisponibles: number;
  creadorId?: number;
}

export interface PlazaRequest {
  empresaId: number;
  departamentoId: number;
  cursoId?: number;
  cantidad: number;
  cursoAcademico: string;
}

export const plazaService = {
  getByEmpresa: (empresaId: number) =>
    api.get<PlazaResponse[]>(`/plazas/empresa/${empresaId}`),

  crear: (data: PlazaRequest) =>
    api.post<PlazaResponse>('/plazas', data),

  eliminar: (id: number) => api.delete<void>(`/plazas/${id}`),
};
