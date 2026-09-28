package com.boletin1.tareas.repository;

import com.boletin1.tareas.model.EstadoTarea;
import com.boletin1.tareas.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  Page<Task> findByEstado(EstadoTarea estado, Pageable pageable);
}
