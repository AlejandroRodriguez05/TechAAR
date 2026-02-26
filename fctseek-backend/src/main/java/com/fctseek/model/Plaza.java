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
 * Entidad que representa una Plaza de FCT ofertada por una empresa.
 * Puede ser general (cualquier ciclo del departamento) o específica para un curso.
 */
@Entity
@Table(name = "plazas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"empresa_id", "departamento_id", "curso_id", "curso_academico"})
})
public class Plaza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "es_general", nullable = false)
    private Boolean esGeneral = false;

    @Column(name = "curso_academico", nullable = false, length = 9)
    private String cursoAcademico;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;  // null si es_general = true

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Usuario createdBy;

    @OneToMany(mappedBy = "plaza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    // Constructores
    public Plaza() {
    }

    public Plaza(Empresa empresa, Departamento departamento, Integer cantidad, String cursoAcademico) {
        this.empresa = empresa;
        this.departamento = departamento;
        this.cantidad = cantidad;
        this.cursoAcademico = cursoAcademico;
        this.esGeneral = true;
    }

    public Plaza(Empresa empresa, Departamento departamento, Curso curso, Integer cantidad, String cursoAcademico) {
        this.empresa = empresa;
        this.departamento = departamento;
        this.curso = curso;
        this.cantidad = cantidad;
        this.cursoAcademico = cursoAcademico;
        this.esGeneral = false;
    }

    // Callbacks JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (esGeneral == null) esGeneral = (curso == null);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public int getPlazasReservadas() {
        return reservas.stream()
                .filter(r -> !"CANCELADA".equals(r.getEstado()))
                .mapToInt(Reserva::getCantidad)
                .sum();
    }

    public int getPlazasDisponibles() {
        return cantidad - getPlazasReservadas();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getEsGeneral() {
        return esGeneral;
    }

    public void setEsGeneral(Boolean esGeneral) {
        this.esGeneral = esGeneral;
    }

    public String getCursoAcademico() {
        return cursoAcademico;
    }

    public void setCursoAcademico(String cursoAcademico) {
        this.cursoAcademico = cursoAcademico;
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}

