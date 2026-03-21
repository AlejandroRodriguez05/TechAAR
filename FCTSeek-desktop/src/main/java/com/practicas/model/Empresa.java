package com.practicas.model;

import java.util.ArrayList;
import java.util.List;

public class Empresa {
    private long id;
    private String nombre;
    private String cif;
    private String telefono;
    private String email;
    private String web;
    private String personaContacto;
    private String telefonoContacto;
    private String emailContacto;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String provincia;
    private String descripcion;
    private boolean activa;
    private List<Departamento> departamentos;
    private List<Curso> cursos;
    private Double valoracionMedia;
    private Integer totalValoraciones;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getWeb() { return web; }
    public void setWeb(String web) { this.web = web; }
    public String getPersonaContacto() { return personaContacto; }
    public void setPersonaContacto(String pc) { this.personaContacto = pc; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String tc) { this.telefonoContacto = tc; }
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String ec) { this.emailContacto = ec; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String cp) { this.codigoPostal = cp; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public List<Departamento> getDepartamentos() { return departamentos != null ? departamentos : new ArrayList<>(); }
    public void setDepartamentos(List<Departamento> d) { this.departamentos = d; }
    public List<Curso> getCursos() { return cursos != null ? cursos : new ArrayList<>(); }
    public void setCursos(List<Curso> c) { this.cursos = c; }
    private List<EmpresaContactada> contactadaPor;
    public List<EmpresaContactada> getContactadaPor() { return contactadaPor != null ? contactadaPor : new ArrayList<>(); }
    public void setContactadaPor(List<EmpresaContactada> cp) { this.contactadaPor = cp; }
    public Double getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Double v) { this.valoracionMedia = v; }
    public Integer getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(Integer t) { this.totalValoraciones = t; }

    public String getEstrellasTexto() {
        if (valoracionMedia == null || valoracionMedia == 0) return "☆☆☆☆☆";
        int full = (int) Math.round(valoracionMedia);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i <= full ? "★" : "☆");
        return sb.toString();
    }

    public String getDepartamentosTexto() {
        if (departamentos == null || departamentos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Departamento d : departamentos) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(d.getCodigo() != null ? d.getCodigo() : d.getNombre());
        }
        return sb.toString();
    }
}
