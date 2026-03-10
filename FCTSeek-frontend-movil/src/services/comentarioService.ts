// src/services/comentarioService.ts
import { api } from '../config/api';
import { Comentario } from '../types';

export interface ComentarioRequest {
  empresaId: number;
  texto: string;
  esPrivado: boolean;
}

export const comentarioService = {
  getByEmpresa: (empresaId: number) =>
    api.get<Comentario[]>(`/comentarios/empresa/${empresaId}`),

  crear: (data: ComentarioRequest) =>
    api.post<Comentario>('/comentarios', data),

  eliminar: (id: number) => api.delete<void>(`/comentarios/${id}`),
};
