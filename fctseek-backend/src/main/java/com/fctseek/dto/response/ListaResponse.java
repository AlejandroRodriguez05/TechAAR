package com.fctseek.dto.response;

import com.fctseek.model.Lista;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para la respuesta de datos de lista.
 */
public class ListaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean esFavoritos;
    private Integer cantidadEmpresas;
    private List<EmpresaResponse> empresas;

    // Constructores
    public ListaResponse() {
    }

    // Constructor básico desde entidad
    public static ListaResponse fromEntity(Lista lista) {
        ListaResponse response = new ListaResponse();
        response.setId(lista.getId());
        response.setNombre(lista.getNombre());
        response.setDescripcion(lista.getDescripcion());
        response.setEsFavoritos(lista.getEsFavoritos());
        response.setCantidadEmpresas(lista.getCantidadEmpresas());
        return response;
    }

    // Constructor completo con empresas
    public static ListaResponse fromEntityWithEmpresas(Lista lista) {
        ListaResponse response = fromEntity(lista);
        
        if (lista.getListaEmpresas() != null) {
            response.setEmpresas(
                lista.getListaEmpresas().stream()
                    .map(le -> EmpresaResponse.fromEntity(le.getEmpresa()))
                    .collect(Collectors.toList())
            );
        }
        
        return response;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsFavoritos() {
        return esFavoritos;
    }

    public void setEsFavoritos(Boolean esFavoritos) {
        this.esFavoritos = esFavoritos;
    }

    public Integer getCantidadEmpresas() {
        return cantidadEmpresas;
    }

    public void setCantidadEmpresas(Integer cantidadEmpresas) {
        this.cantidadEmpresas = cantidadEmpresas;
    }

    public List<EmpresaResponse> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<EmpresaResponse> empresas) {
        this.empresas = empresas;
    }
}
