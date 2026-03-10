// src/services/valoracionService.ts
import { api } from '../config/api';

export interface ValoracionRequest {
  empresaId: number;
  puntuacion: number;
}

export interface ValoracionResponse {
  id: number;
  empresaId: number;
  usuarioId: number;
  puntuacion: number;
  rolValorador: string;
  anioAcademico?: string;
}

export const valoracionService = {
  getByEmpresa: (empresaId: number) =>
    api.get<ValoracionResponse[]>(`/valoraciones/empresa/${empresaId}`),

  crear: (data: ValoracionRequest) =>
    api.post<ValoracionResponse>('/valoraciones', data),
};
