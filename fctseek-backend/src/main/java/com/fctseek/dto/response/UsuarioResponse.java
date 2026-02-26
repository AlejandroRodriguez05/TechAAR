package com.fctseek.dto.response;

import com.fctseek.model.Usuario;

/**
 * DTO para la respuesta de datos de usuario.
 */
public class UsuarioResponse {

    private Long id;
    private String email;
    private String nif;
    private String nombre;
    private String apellidos;
    private String rol;
    private Boolean activo;
    private Long departamentoId;
    private String departamentoNombre;
    private String departamentoCodigo;

    // Constructores
    public UsuarioResponse() {
    }

    // Constructor desde entidad
    public static UsuarioResponse fromEntity(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setEmail(usuario.getEmail());
        response.setNif(usuario.getNif());
        response.setNombre(usuario.getNombre());
        response.setApellidos(usuario.getApellidos());
        response.setRol(usuario.getRol());
        response.setActivo(usuario.getActivo());
        
        if (usuario.getDepartamento() != null) {
            response.setDepartamentoId(usuario.getDepartamento().getId());
            response.setDepartamentoNombre(usuario.getDepartamento().getNombre());
            response.setDepartamentoCodigo(usuario.getDepartamento().getCodigo());
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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

    public String getDepartamentoCodigo() {
        return departamentoCodigo;
    }

    public void setDepartamentoCodigo(String departamentoCodigo) {
        this.departamentoCodigo = departamentoCodigo;
    }
}
