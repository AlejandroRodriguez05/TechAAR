/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fctseek.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa la relación entre una Lista y una Empresa.
 * Permite añadir notas específicas para cada empresa dentro de una lista.
 */
@Entity
@Table(name = "lista_empresas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"lista_id", "empresa_id"})
})
public class ListaEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_id", nullable = false)
    private Lista lista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Constructores
    public ListaEmpresa() {
    }

    public ListaEmpresa(Lista lista, Empresa empresa) {
        this.lista = lista;
        this.empresa = empresa;
    }

    public ListaEmpresa(Lista lista, Empresa empresa, String notas) {
        this.lista = lista;
        this.empresa = empresa;
        this.notas = notas;
    }

    // Callbacks JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Lista getLista() {
        return lista;
    }

    public void setLista(Lista lista) {
        this.lista = lista;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
