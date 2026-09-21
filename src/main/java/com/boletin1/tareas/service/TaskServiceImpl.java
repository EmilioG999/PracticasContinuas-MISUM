package com.boletin1.tareas.service;

import com.boletin1.tareas.exception.TaskNotFoundException;
import com.boletin1.tareas.model.Task;
import com.boletin1.tareas.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> obtenerTodas() {
        return taskRepository.findAll();
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
