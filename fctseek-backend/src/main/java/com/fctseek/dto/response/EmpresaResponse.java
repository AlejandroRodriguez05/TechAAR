package com.fctseek.dto.response;

import com.fctseek.model.Empresa;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para la respuesta básica de empresa (listados).
 */
public class EmpresaResponse {

    private Long id;
    private String cif;
    private String nombre;
    private String ciudad;
    private String codigoPostal;
    private String telefono;
    private String email;
    private String personaContacto;
    private Boolean activa;
    private Double valoracionMedia;
    private Integer totalValoraciones;
    private List<CursoResponse> cursos;
    private List<DepartamentoResponse> departamentos;

    // Constructores
    public EmpresaResponse() {
    }

    // Constructor desde entidad (básico)
    public static EmpresaResponse fromEntity(Empresa empresa) {
        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setCif(empresa.getCif());
        response.setNombre(empresa.getNombre());
        response.setCiudad(empresa.getCiudad());
        response.setCodigoPostal(empresa.getCodigoPostal());
        response.setTelefono(empresa.getTelefono());
        response.setEmail(empresa.getEmail());
        response.setPersonaContacto(empresa.getPersonaContacto());
        response.setActiva(empresa.getActiva());
        return response;
    }

    // Constructor con valoraciones y cursos
    public static EmpresaResponse fromEntityWithDetails(Empresa empresa, Double valoracionMedia, 
                                                         Integer totalValoraciones) {
        EmpresaResponse response = fromEntity(empresa);
        response.setValoracionMedia(valoracionMedia);
        response.setTotalValoraciones(totalValoraciones);
        
        // Mapear cursos
        if (empresa.getEmpresaCursos() != null) {
            response.setCursos(
                empresa.getEmpresaCursos().stream()
                    .map(ec -> CursoResponse.fromEntity(ec.getCurso()))
                    .collect(Collectors.toList())
            );
            
            // Mapear departamentos únicos
            response.setDepartamentos(
                empresa.getEmpresaCursos().stream()
                    .map(ec -> ec.getCurso().getDepartamento())
                    .distinct()
                    .map(DepartamentoResponse::fromEntity)
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

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public Double getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(Double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }

    public Integer getTotalValoraciones() {
        return totalValoraciones;
    }

    public void setTotalValoraciones(Integer totalValoraciones) {
        this.totalValoraciones = totalValoraciones;
    }

    public List<CursoResponse> getCursos() {
        return cursos;
    }

    public void setCursos(List<CursoResponse> cursos) {
        this.cursos = cursos;
    }

    public List<DepartamentoResponse> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<DepartamentoResponse> departamentos) {
        this.departamentos = departamentos;
    }
}
