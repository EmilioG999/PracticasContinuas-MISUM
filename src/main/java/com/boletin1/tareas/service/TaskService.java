package com.boletin1.tareas.service;

import com.boletin1.tareas.model.EstadoTarea;
import com.boletin1.tareas.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

  Page<Task> obtenerTodas(Pageable pageable);

  Page<Task> obtenerPorEstado(EstadoTarea estado, Pageable pageable);

  Task obtenerPorId(Long id);

  Task crear(Task task);

  Task actualizar(Long id, Task datosActualizados);

  void eliminar(Long id);
}
