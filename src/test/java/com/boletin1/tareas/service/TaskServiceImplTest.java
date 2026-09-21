package com.boletin1.tareas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.boletin1.tareas.exception.TaskNotFoundException;
import com.boletin1.tareas.model.EstadoTarea;
import com.boletin1.tareas.model.PrioridadTarea;
import com.boletin1.tareas.model.Task;
import com.boletin1.tareas.repository.TaskRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitarios de la capa de servicio. El repositorio se mockea con Mockito: no se levanta
 * contexto de Spring ni base de datos, tal y como exige el boletin.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  @Mock private TaskRepository taskRepository;

  private TaskService taskService;

  @BeforeEach
  void setUp() {
    taskService = new TaskServiceImpl(taskRepository);
  }

  private Task tareaDeEjemplo(Long id) {
    return new Task(
        id,
        "Entregar boletin",
        "Descripcion",
        EstadoTarea.PENDIENTE,
        PrioridadTarea.ALTA,
        LocalDate.now().plusDays(5));
  }

  @Test
  void crearGuardaLaTareaConIdNulo() {
    Task nueva = tareaDeEjemplo(null);
    Task guardada = tareaDeEjemplo(1L);
    when(taskRepository.save(any(Task.class))).thenReturn(guardada);

    Task resultado = taskService.crear(nueva);

    assertThat(resultado.getId()).isEqualTo(1L);
    verify(taskRepository, times(1)).save(any(Task.class));
  }

  @Test
  void obtenerPorIdLanzaExcepcionSiNoExiste() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.obtenerPorId(99L))
        .isInstanceOf(TaskNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void actualizarTareaInexistenteLanzaExcepcion() {
    when(taskRepository.findById(42L)).thenReturn(Optional.empty());
    Task datos = tareaDeEjemplo(null);

    assertThatThrownBy(() -> taskService.actualizar(42L, datos))
        .isInstanceOf(TaskNotFoundException.class);

    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  void actualizarModificaLosCamposDeLaTareaExistente() {
    Task existente = tareaDeEjemplo(1L);
    when(taskRepository.findById(1L)).thenReturn(Optional.of(existente));
    when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

    Task datosNuevos =
        new Task(
            null,
            "Titulo actualizado",
            "Nueva descripcion",
            EstadoTarea.COMPLETADA,
            PrioridadTarea.BAJA,
            LocalDate.now().plusDays(10));

    Task resultado = taskService.actualizar(1L, datosNuevos);

    assertThat(resultado.getTitulo()).isEqualTo("Titulo actualizado");
    assertThat(resultado.getEstado()).isEqualTo(EstadoTarea.COMPLETADA);
    assertThat(resultado.getPrioridad()).isEqualTo(PrioridadTarea.BAJA);
  }

  @Test
  void eliminarTareaInexistenteLanzaExcepcionYNoBorraNada() {
    when(taskRepository.existsById(7L)).thenReturn(false);

    assertThatThrownBy(() -> taskService.eliminar(7L)).isInstanceOf(TaskNotFoundException.class);

    verify(taskRepository, never()).deleteById(any());
  }

  @Test
  void eliminarBorraLaTareaCuandoExiste() {
    when(taskRepository.existsById(1L)).thenReturn(true);

    taskService.eliminar(1L);

    verify(taskRepository, times(1)).deleteById(1L);
  }
}
