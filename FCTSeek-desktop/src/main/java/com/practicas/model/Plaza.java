package com.practicas.model;

public class Plaza {
    private long id;
    private long empresaId;
    private long departamentoId;
    private String departamentoNombre;
    private long profesorId;
    private String profesorNombre;
    private String fecha;
    private String nota;
    private int plazasOfertadas;
    private boolean esGeneral;
    private int reservadas;
    private int libres;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getEmpresaId() { return empresaId; }
    public void setEmpresaId(long eid) { this.empresaId = eid; }
    public long getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(long did) { this.departamentoId = did; }
    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String dn) { this.departamentoNombre = dn; }
    public long getProfesorId() { return profesorId; }
    public void setProfesorId(long pid) { this.profesorId = pid; }
    public String getProfesorNombre() { return profesorNombre; }
    public void setProfesorNombre(String pn) { this.profesorNombre = pn; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    public int getPlazasOfertadas() { return plazasOfertadas; }
    public void setPlazasOfertadas(int po) { this.plazasOfertadas = po; }
    public boolean isEsGeneral() { return esGeneral; }
    public void setEsGeneral(boolean eg) { this.esGeneral = eg; }
    public int getReservadas() { return reservadas; }
    public void setReservadas(int r) { this.reservadas = r; }
    public int getLibres() { return libres; }
    public void setLibres(int l) { this.libres = l; }
}
