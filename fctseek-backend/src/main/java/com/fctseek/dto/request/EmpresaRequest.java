package com.fctseek.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO para crear/actualizar una empresa.
 */
public class EmpresaRequest {

    @Size(max = 15, message = "El CIF no puede superar los 15 caracteres")
    private String cif;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String ciudad;

    @Size(max = 10, message = "El código postal no puede superar los 10 caracteres")
    private String codigoPostal;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telefono;

    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @Size(max = 200, message = "La web no puede superar los 200 caracteres")
    private String web;

    @Size(max = 100, message = "El nombre de contacto no puede superar los 100 caracteres")
    private String personaContacto;

    @Size(max = 20, message = "El teléfono de contacto no puede superar los 20 caracteres")
    private String telefonoContacto;

    @Size(max = 150, message = "El email de contacto no puede superar los 150 caracteres")
    private String emailContacto;

    private String descripcion;

    private Boolean activa = true;

    // IDs de los cursos que acepta la empresa
    private List<Long> cursosIds;

    // IDs de departamentos que acepta la empresa (genera las etiquetas)
    private List<Long> departamentosIds;

    // Constructores
    public EmpresaRequest() {
    }

    // Getters y Setters
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public List<Long> getCursosIds() {
        return cursosIds;
    }

    public void setCursosIds(List<Long> cursosIds) {
        this.cursosIds = cursosIds;
    }

    public List<Long> getDepartamentosIds() {
        return departamentosIds;
    }

    public void setDepartamentosIds(List<Long> departamentosIds) {
        this.departamentosIds = departamentosIds;
    }
}
