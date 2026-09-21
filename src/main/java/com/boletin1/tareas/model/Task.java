package com.boletin1.tareas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El titulo no puede estar vacio")
    @Size(max = 100, message = "El titulo no puede superar los 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoTarea estado;

    @NotNull(message = "La prioridad es obligatoria")
    @Enumerated(EnumType.STRING)
    private PrioridadTarea prioridad;

    @NotNull(message = "La fecha limite es obligatoria")
    @FutureOrPresent(message = "La fecha limite no puede estar en el pasado")
    private LocalDate fechaLimite;

    public Task() {
    }

    public Task(
            Long id,
            String titulo,
            String descripcion,
            EstadoTarea estado,
            PrioridadTarea prioridad,
            LocalDate fechaLimite) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public PrioridadTarea getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadTarea prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
