package com.practicas.model;

public class Departamento {
    private long id;
    private String nombre;
    private String codigo;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    @Override
    public String toString() {
        return codigo != null ? codigo + " - " + nombre : nombre;
    }
}
