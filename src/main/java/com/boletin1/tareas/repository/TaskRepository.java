package com.boletin1.tareas.repository;

import com.boletin1.tareas.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
