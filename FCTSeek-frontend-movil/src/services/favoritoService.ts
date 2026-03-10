// src/services/favoritoService.ts
import { api } from '../config/api';
import { Empresa } from '../types';

export const favoritoService = {
  getMisFavoritos: () => api.get<Empresa[]>('/favoritos'),

  toggle: (empresaId: number) =>
    api.post<{ esFavorita: boolean }>(`/favoritos/empresa/${empresaId}/toggle`),

  isFavorito: (empresaId: number) =>
    api.get<{ esFavorita: boolean }>(`/favoritos/empresa/${empresaId}`),
};
