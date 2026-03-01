package com.practicas.model;

public class Curso {
    private long id;
    private long departamentoId;
    private String nombre;
    private String siglas;
    private String grado;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(long did) { this.departamentoId = did; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSiglas() { return siglas; }
    public void setSiglas(String siglas) { this.siglas = siglas; }
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    @Override
    public String toString() { return siglas + " - " + nombre; }
}
