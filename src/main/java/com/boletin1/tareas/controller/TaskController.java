package com.boletin1.tareas.controller;

import com.boletin1.tareas.model.Task;
import com.boletin1.tareas.service.TaskService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> obtenerTodas() {
        return taskService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Task> crear(@Valid @RequestBody Task task) {
        Task creada = taskService.crear(task);
        return ResponseEntity.created(URI.create("/api/tasks/" + creada.getId())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> actualizar(@PathVariable Long id, @Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.actualizar(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        taskService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
