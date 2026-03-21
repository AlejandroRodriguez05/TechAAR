package com.practicas.model;

public class Usuario {
    private long id;
    private String nif;
    private String nombre;
    private String apellidos;
    private String email;
    private String rol;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean esProfesor() { return "PROFESOR".equalsIgnoreCase(rol); }

    public String getNombreCompleto() {
        return ((nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "")).trim();
    }

    public String getIniciales() {
        String n = nombre != null && !nombre.isEmpty() ? nombre.substring(0, 1) : "";
        String a = apellidos != null && !apellidos.isEmpty() ? apellidos.substring(0, 1) : "";
        return (n + a).toUpperCase();
    }
}
