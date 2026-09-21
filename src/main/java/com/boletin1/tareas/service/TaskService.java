package com.boletin1.tareas.service;

import com.boletin1.tareas.model.EstadoTarea;
import com.boletin1.tareas.model.Task;
import java.util.List;

public interface TaskService {

  List<Task> obtenerTodas();

  List<Task> obtenerPorEstado(EstadoTarea estado);

  Task obtenerPorId(Long id);

  Task crear(Task task);

  Task actualizar(Long id, Task datosActualizados);

  void eliminar(Long id);
}
