package com.fctseek.dto.response;

import com.fctseek.model.Plaza;

/**
 * DTO para la respuesta de datos de plaza.
 */
public class PlazaResponse {

    private Long id;
    private Long empresaId;
    private String empresaNombre;
    private Long departamentoId;
    private String departamentoNombre;
    private Long cursoId;
    private String cursoSiglas;
    private String cursoNombre;
    private Integer cantidad;
    private Boolean esGeneral;
    private String cursoAcademico;
    private Integer plazasReservadas;
    private Integer plazasDisponibles;
    private Long creadorId;

    // Constructores
    public PlazaResponse() {
    }

    // Constructor desde entidad
    public static PlazaResponse fromEntity(Plaza plaza) {
        PlazaResponse response = new PlazaResponse();
        response.setId(plaza.getId());
        response.setEmpresaId(plaza.getEmpresa().getId());
        if (plaza.getCreatedBy() != null) {
            response.setCreadorId(plaza.getCreatedBy().getId());
        }
        response.setEmpresaNombre(plaza.getEmpresa().getNombre());
        response.setDepartamentoId(plaza.getDepartamento().getId());
        response.setDepartamentoNombre(plaza.getDepartamento().getNombre());
        response.setCantidad(plaza.getCantidad());
        response.setEsGeneral(plaza.getEsGeneral());
        response.setCursoAcademico(plaza.getCursoAcademico());
        
        if (plaza.getCurso() != null) {
            response.setCursoId(plaza.getCurso().getId());
            response.setCursoSiglas(plaza.getCurso().getSiglas());
            response.setCursoNombre(plaza.getCurso().getNombre());
        } else {
            response.setCursoSiglas("General");
            response.setCursoNombre("Cualquier ciclo del departamento");
        }
        
        // Calcular plazas reservadas y disponibles
        int reservadas = plaza.getPlazasReservadas();
        response.setPlazasReservadas(reservadas);
        response.setPlazasDisponibles(plaza.getCantidad() - reservadas);
        
        return response;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getDepartamentoNombre() {
        return departamentoNombre;
    }

    public void setDepartamentoNombre(String departamentoNombre) {
        this.departamentoNombre = departamentoNombre;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoSiglas() {
        return cursoSiglas;
    }

    public void setCursoSiglas(String cursoSiglas) {
        this.cursoSiglas = cursoSiglas;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getEsGeneral() {
        return esGeneral;
    }

    public void setEsGeneral(Boolean esGeneral) {
        this.esGeneral = esGeneral;
    }

    public String getCursoAcademico() {
        return cursoAcademico;
    }

    public void setCursoAcademico(String cursoAcademico) {
        this.cursoAcademico = cursoAcademico;
    }

    public Integer getPlazasReservadas() {
        return plazasReservadas;
    }

    public void setPlazasReservadas(Integer plazasReservadas) {
        this.plazasReservadas = plazasReservadas;
    }

    public Integer getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public void setPlazasDisponibles(Integer plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }
}
