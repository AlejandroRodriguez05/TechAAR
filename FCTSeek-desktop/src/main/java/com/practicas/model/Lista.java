package com.practicas.model;

import java.util.ArrayList;
import java.util.List;

public class Lista {
    private long id;
    private String nombre;
    private long usuarioId;
    private boolean esFavoritos;
    private List<Empresa> empresas;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long uid) { this.usuarioId = uid; }
    public boolean isEsFavoritos() { return esFavoritos; }
    public void setEsFavoritos(boolean ef) { this.esFavoritos = ef; }
    public List<Empresa> getEmpresas() { return empresas != null ? empresas : new ArrayList<>(); }
    public void setEmpresas(List<Empresa> e) { this.empresas = e; }
}
