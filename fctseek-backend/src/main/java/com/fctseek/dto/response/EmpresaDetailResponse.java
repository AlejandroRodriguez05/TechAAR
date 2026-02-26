package com.fctseek.dto.response;

import com.fctseek.model.Empresa;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para la respuesta detallada de empresa.
 * Incluye todos los datos, cursos, contactos, plazas, etc.
 */
public class EmpresaDetailResponse {

    private Long id;
    private String cif;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String telefono;
    private String email;
    private String web;
    private String personaContacto;
    private String telefonoContacto;
    private String emailContacto;
    private String descripcion;
    private Boolean activa;
    private Double valoracionMedia;
    private Integer totalValoraciones;
    private List<CursoResponse> cursos;
    private List<DepartamentoResponse> departamentos;
    private List<ContactadaPorResponse> contactadaPor;
    private List<PlazaResponse> plazas;
    private Boolean esFavorita;
    private Integer miValoracion;

    // Constructores
    public EmpresaDetailResponse() {
    }

    // Constructor desde entidad
    public static EmpresaDetailResponse fromEntity(Empresa empresa) {
        EmpresaDetailResponse response = new EmpresaDetailResponse();
        response.setId(empresa.getId());
        response.setCif(empresa.getCif());
        response.setNombre(empresa.getNombre());
        response.setDireccion(empresa.getDireccion());
        response.setCiudad(empresa.getCiudad());
        response.setCodigoPostal(empresa.getCodigoPostal());
        response.setTelefono(empresa.getTelefono());
        response.setEmail(empresa.getEmail());
        response.setWeb(empresa.getWeb());
        response.setPersonaContacto(empresa.getPersonaContacto());
        response.setTelefonoContacto(empresa.getTelefonoContacto());
        response.setEmailContacto(empresa.getEmailContacto());
        response.setDescripcion(empresa.getDescripcion());
        response.setActiva(empresa.getActiva());
        
        // Mapear cursos
        if (empresa.getEmpresaCursos() != null) {
            response.setCursos(
                empresa.getEmpresaCursos().stream()
                    .map(ec -> CursoResponse.fromEntity(ec.getCurso()))
                    .collect(Collectors.toList())
            );
            
            // Departamentos únicos
            response.setDepartamentos(
                empresa.getEmpresaCursos().stream()
                    .map(ec -> ec.getCurso().getDepartamento())
                    .distinct()
                    .map(DepartamentoResponse::fromEntity)
                    .collect(Collectors.toList())
            );
        }
        
        // Mapear contactos
        if (empresa.getContactos() != null) {
            response.setContactadaPor(
                empresa.getContactos().stream()
                    .map(ContactadaPorResponse::fromEntity)
                    .collect(Collectors.toList())
            );
        }
        
        return response;
    }

    // DTO interno para contactos
    public static class ContactadaPorResponse {
        private Long departamentoId;
        private String departamentoNombre;
        private String profesorNombre;
        private String fecha;
        private String notas;

        public static ContactadaPorResponse fromEntity(com.fctseek.model.EmpresaContactada contacto) {
            ContactadaPorResponse response = new ContactadaPorResponse();
            response.setDepartamentoId(contacto.getDepartamento().getId());
            response.setDepartamentoNombre(contacto.getDepartamento().getNombre());
            response.setProfesorNombre(contacto.getProfesor().getNombreCompleto());
            response.setFecha(contacto.getFecha().toString());
            response.setNotas(contacto.getNotas());
            return response;
        }

        // Getters y Setters
        public Long getDepartamentoId() { return departamentoId; }
        public void setDepartamentoId(Long departamentoId) { this.departamentoId = departamentoId; }
        public String getDepartamentoNombre() { return departamentoNombre; }
        public void setDepartamentoNombre(String departamentoNombre) { this.departamentoNombre = departamentoNombre; }
        public String getProfesorNombre() { return profesorNombre; }
        public void setProfesorNombre(String profesorNombre) { this.profesorNombre = profesorNombre; }
        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }
        public String getNotas() { return notas; }
        public void setNotas(String notas) { this.notas = notas; }
    }

    // Getters y Setters principales
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getWeb() { return web; }
    public void setWeb(String web) { this.web = web; }
    
    public String getPersonaContacto() { return personaContacto; }
    public void setPersonaContacto(String personaContacto) { this.personaContacto = personaContacto; }
    
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
    
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
    
    public Double getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Double valoracionMedia) { this.valoracionMedia = valoracionMedia; }
    
    public Integer getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(Integer totalValoraciones) { this.totalValoraciones = totalValoraciones; }
    
    public List<CursoResponse> getCursos() { return cursos; }
    public void setCursos(List<CursoResponse> cursos) { this.cursos = cursos; }
    
    public List<DepartamentoResponse> getDepartamentos() { return departamentos; }
    public void setDepartamentos(List<DepartamentoResponse> departamentos) { this.departamentos = departamentos; }
    
    public List<ContactadaPorResponse> getContactadaPor() { return contactadaPor; }
    public void setContactadaPor(List<ContactadaPorResponse> contactadaPor) { this.contactadaPor = contactadaPor; }
    
    public List<PlazaResponse> getPlazas() { return plazas; }
    public void setPlazas(List<PlazaResponse> plazas) { this.plazas = plazas; }
    
    public Boolean getEsFavorita() { return esFavorita; }
    public void setEsFavorita(Boolean esFavorita) { this.esFavorita = esFavorita; }
    
    public Integer getMiValoracion() { return miValoracion; }
    public void setMiValoracion(Integer miValoracion) { this.miValoracion = miValoracion; }
}
