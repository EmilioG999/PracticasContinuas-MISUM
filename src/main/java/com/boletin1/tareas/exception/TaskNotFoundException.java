package com.boletin1.tareas.exception;

public class TaskNotFoundException extends RuntimeException {

  public TaskNotFoundException(Long id) {
    super("Tarea con id " + id + " no encontrada");
  }
}
