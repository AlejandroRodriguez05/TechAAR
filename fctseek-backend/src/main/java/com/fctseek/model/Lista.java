/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fctseek.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Lista personalizada de empresas.
 * Los usuarios pueden crear listas para organizar empresas (ej: "Para contactar", "FCT 2025").
 */
@Entity
@Table(name = "listas")
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "es_favoritos", nullable = false)
    private Boolean esFavoritos = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListaEmpresa> listaEmpresas = new ArrayList<>();

    // Constructores
    public Lista() {
    }

    public Lista(Usuario usuario, String nombre) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.esFavoritos = false;
    }

    public Lista(Usuario usuario, String nombre, String descripcion) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esFavoritos = false;
    }

    // Callbacks JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (esFavoritos == null) esFavoritos = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public int getCantidadEmpresas() {
        return listaEmpresas.size();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getEsFavoritos() {
        return esFavoritos;
    }

    public void setEsFavoritos(Boolean esFavoritos) {
        this.esFavoritos = esFavoritos;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ListaEmpresa> getListaEmpresas() {
        return listaEmpresas;
    }

    public void setListaEmpresas(List<ListaEmpresa> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }
}
