package com.boletin1.tareas.service;

import com.boletin1.tareas.exception.TaskNotFoundException;
import com.boletin1.tareas.model.EstadoTarea;
import com.boletin1.tareas.model.Task;
import com.boletin1.tareas.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;

  public TaskServiceImpl(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Override
  public Page<Task> obtenerTodas(Pageable pageable) {
    return taskRepository.findAll(pageable);
  }

  @Override
  public Page<Task> obtenerPorEstado(EstadoTarea estado, Pageable pageable) {
    return taskRepository.findByEstado(estado, pageable);
  }

  @Override
  public Page<Task> buscarPorTitulo(String titulo, Pageable pageable) {
    if (titulo == null || titulo.isBlank()) {
      throw new IllegalArgumentException("El parametro 'titulo' no puede estar vacio");
    }
    return taskRepository.findByTituloContainingIgnoreCase(titulo, pageable);
  }

  @Override
  public Task obtenerPorId(Long id) {
    return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
  }

  @Override
  public Task crear(Task task) {
    task.setId(null);
    return taskRepository.save(task);
  }

  @Override
  public Task actualizar(Long id, Task datosActualizados) {
    Task existente = obtenerPorId(id);
    existente.setTitulo(datosActualizados.getTitulo());
    existente.setDescripcion(datosActualizados.getDescripcion());
    existente.setEstado(datosActualizados.getEstado());
    existente.setPrioridad(datosActualizados.getPrioridad());
    existente.setFechaLimite(datosActualizados.getFechaLimite());
    return taskRepository.save(existente);
  }

  @Override
  public void eliminar(Long id) {
    if (!taskRepository.existsById(id)) {
      throw new TaskNotFoundException(id);
    }
    taskRepository.deleteById(id);
  }
}
