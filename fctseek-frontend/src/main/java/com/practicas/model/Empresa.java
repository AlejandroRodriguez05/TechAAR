package com.practicas.model;

/**
 * Clase que representa una empresa de prácticas
 */
public class Empresa {
    private String nombre;
    private String descripcion;
    private String resena;
    private String sector;
    private String ubicacion;
    private int plazasTotales;
    private int plazasOcupadas;

    public Empresa(String nombre, String descripcion, String resena, String sector, 
                   String ubicacion, int plazasTotales, int plazasOcupadas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.resena = resena;
        this.sector = sector;
        this.ubicacion = ubicacion;
        this.plazasTotales = plazasTotales;
        this.plazasOcupadas = plazasOcupadas;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getResena() {
        return resena;
    }

    public void setResena(String resena) {
        this.resena = resena;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getPlazasTotales() {
        return plazasTotales;
    }

    public void setPlazasTotales(int plazasTotales) {
        this.plazasTotales = plazasTotales;
    }

    public int getPlazasOcupadas() {
        return plazasOcupadas;
    }

    public void setPlazasOcupadas(int plazasOcupadas) {
        this.plazasOcupadas = plazasOcupadas;
    }

    /**
     * Verifica si la empresa está ocupada (sin plazas disponibles)
     */
    public boolean estaOcupada() {
        return plazasOcupadas >= plazasTotales;
    }

    /**
     * Devuelve el estado de plazas como string (ej: "2/4")
     */
    public String getEstadoPlazas() {
        return plazasOcupadas + "/" + plazasTotales;
    }

    /**
     * Devuelve el estado como texto
     */
    public String getEstadoTexto() {
        return estaOcupada() ? "OCUPADA" : "LIBRE";
    }

    /**
     * Incrementa las plazas ocupadas
     */
    public boolean ocuparPlaza() {
        if (!estaOcupada()) {
            plazasOcupadas++;
            return true;
        }
        return false;
    }

    /**
     * Libera una plaza
     */
    public boolean liberarPlaza() {
        if (plazasOcupadas > 0) {
            plazasOcupadas--;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return nombre + " - " + getEstadoPlazas() + " (" + getEstadoTexto() + ")";
    }
}
