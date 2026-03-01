package com.practicas.model;

public class Comentario {
    private long id;
    private long empresaId;
    private long usuarioId;
    private String usuarioNombre;
    private String texto;
    private boolean esPrivado;
    private String fecha;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getEmpresaId() { return empresaId; }
    public void setEmpresaId(long eid) { this.empresaId = eid; }
    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long uid) { this.usuarioId = uid; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String un) { this.usuarioNombre = un; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public boolean isEsPrivado() { return esPrivado; }
    public void setEsPrivado(boolean ep) { this.esPrivado = ep; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
