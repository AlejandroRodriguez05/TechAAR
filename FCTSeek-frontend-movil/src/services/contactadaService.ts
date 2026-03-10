// src/services/contactadaService.ts
import { api } from '../config/api';

export interface EmpresaContactadaRequest {
  empresaId: number;
  departamentoId: number;
  notas?: string;
}

export interface EmpresaContactadaResponse {
  id: number;
  empresaId: number;
  empresaNombre: string;
  departamentoId: number;
  departamentoNombre: string;
  profesorId: number;
  profesorNombre: string;
  fecha: string;
  notas?: string;
}

export const contactadaService = {
  getByEmpresa: (empresaId: number) =>
    api.get<EmpresaContactadaResponse[]>(`/empresas-contactadas/empresa/${empresaId}`),

  crear: (data: EmpresaContactadaRequest) =>
    api.post<EmpresaContactadaResponse>('/empresas-contactadas', data),

  eliminar: (id: number) => api.delete<void>(`/empresas-contactadas/${id}`),
};
