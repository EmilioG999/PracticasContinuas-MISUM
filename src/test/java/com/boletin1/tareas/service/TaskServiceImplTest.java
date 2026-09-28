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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

  @Test
  void obtenerTodasDevuelveLaPaginaSolicitadaAlRepositorio() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> pagina =
        new PageImpl<>(List.of(tareaDeEjemplo(1L), tareaDeEjemplo(2L)), pageable, 2);
    when(taskRepository.findAll(pageable)).thenReturn(pagina);

    Page<Task> resultado = taskService.obtenerTodas(pageable);

    assertThat(resultado.getTotalElements()).isEqualTo(2);
    assertThat(resultado.getContent()).hasSize(2);
    verify(taskRepository, times(1)).findAll(pageable);
  }

  @Test
  void obtenerPorEstadoDevuelveSoloLaPaginaFiltradaPorEseEstado() {
    Pageable pageable = PageRequest.of(0, 5);
    Page<Task> pagina = new PageImpl<>(List.of(tareaDeEjemplo(3L)), pageable, 1);
    when(taskRepository.findByEstado(EstadoTarea.COMPLETADA, pageable)).thenReturn(pagina);

    Page<Task> resultado = taskService.obtenerPorEstado(EstadoTarea.COMPLETADA, pageable);

    assertThat(resultado.getTotalElements()).isEqualTo(1);
    verify(taskRepository, times(1)).findByEstado(EstadoTarea.COMPLETADA, pageable);
    verify(taskRepository, never()).findAll(any(Pageable.class));
  }

  @Test
  void buscarPorTituloDevuelveLaPaginaConCoincidencias() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> pagina = new PageImpl<>(List.of(tareaDeEjemplo(1L)), pageable, 1);
    when(taskRepository.findByTituloContainingIgnoreCase("boletin", pageable)).thenReturn(pagina);

    Page<Task> resultado = taskService.buscarPorTitulo("boletin", pageable);

    assertThat(resultado.getTotalElements()).isEqualTo(1);
    verify(taskRepository, times(1)).findByTituloContainingIgnoreCase("boletin", pageable);
  }

  @Test
  void buscarPorTituloDevuelvePaginaVaciaSiNoHayCoincidencias() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> paginaVacia = new PageImpl<>(List.of(), pageable, 0);
    when(taskRepository.findByTituloContainingIgnoreCase("inexistente", pageable))
        .thenReturn(paginaVacia);

    Page<Task> resultado = taskService.buscarPorTitulo("inexistente", pageable);

    assertThat(resultado.getContent()).isEmpty();
  }

  @Test
  void buscarPorTituloLanzaExcepcionSiElTituloEstaVacio() {
    Pageable pageable = PageRequest.of(0, 10);

    assertThatThrownBy(() -> taskService.buscarPorTitulo("  ", pageable))
        .isInstanceOf(IllegalArgumentException.class);

    verify(taskRepository, never()).findByTituloContainingIgnoreCase(any(), any());
  }

  @Test
  void obtenerPorPrioridadDevuelveLaPrimeraPaginaSolicitada() {
    Pageable pageable = PageRequest.of(0, 2);
    List<Task> tareas = List.of(tareaDeEjemplo(1L), tareaDeEjemplo(2L));
    Page<Task> pagina = new PageImpl<>(tareas, pageable, 2);
    when(taskRepository.findByPrioridad(PrioridadTarea.ALTA, pageable)).thenReturn(pagina);

    Page<Task> resultado = taskService.obtenerPorPrioridad(PrioridadTarea.ALTA, pageable);

    assertThat(resultado.getNumber()).isZero();
    assertThat(resultado.getSize()).isEqualTo(2);
    assertThat(resultado.getContent()).containsExactlyElementsOf(tareas);
    assertThat(resultado.getTotalElements()).isEqualTo(2);
    verify(taskRepository, times(1)).findByPrioridad(PrioridadTarea.ALTA, pageable);
    verify(taskRepository, never()).findAll(any(Pageable.class));
  }

  @Test
  void obtenerPorPrioridadDevuelveLaSegundaPaginaSolicitada() {
    Pageable pageable = PageRequest.of(1, 2);
    Page<Task> pagina =
        new PageImpl<>(List.of(tareaDeEjemplo(3L), tareaDeEjemplo(4L)), pageable, 4);
    when(taskRepository.findByPrioridad(PrioridadTarea.ALTA, pageable)).thenReturn(pagina);

    Page<Task> resultado = taskService.obtenerPorPrioridad(PrioridadTarea.ALTA, pageable);

    assertThat(resultado.getNumber()).isEqualTo(1);
    verify(taskRepository, times(1)).findByPrioridad(PrioridadTarea.ALTA, pageable);
  }

  @Test
  void obtenerPorPrioridadRespetaElTamanoPersonalizadoDePagina() {
    Pageable pageable = PageRequest.of(0, 5);
    Page<Task> pagina = new PageImpl<>(List.of(tareaDeEjemplo(1L)), pageable, 1);
    when(taskRepository.findByPrioridad(PrioridadTarea.ALTA, pageable)).thenReturn(pagina);

    Page<Task> resultado = taskService.obtenerPorPrioridad(PrioridadTarea.ALTA, pageable);

    assertThat(resultado.getSize()).isEqualTo(5);
    verify(taskRepository, times(1)).findByPrioridad(PrioridadTarea.ALTA, pageable);
  }

  @Test
  void obtenerPorPrioridadMantieneElTotalAunqueLaPaginaTengaMenosElementos() {
    Pageable pageable = PageRequest.of(0, 2);
    Page<Task> pagina =
        new PageImpl<>(List.of(tareaDeEjemplo(1L), tareaDeEjemplo(2L)), pageable, 5);
    when(taskRepository.findByPrioridad(PrioridadTarea.ALTA, pageable)).thenReturn(pagina);

    Page<Task> resultado = taskService.obtenerPorPrioridad(PrioridadTarea.ALTA, pageable);

    assertThat(resultado.getTotalElements()).isEqualTo(5);
    assertThat(resultado.getTotalPages()).isEqualTo(3);
    assertThat(resultado.getContent()).hasSize(2);
    verify(taskRepository, times(1)).findByPrioridad(PrioridadTarea.ALTA, pageable);
  }

  @Test
  void obtenerPorPrioridadDevuelvePaginaVaciaSiNoHayCoincidencias() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Task> paginaVacia = new PageImpl<>(List.of(), pageable, 0);
    when(taskRepository.findByPrioridad(PrioridadTarea.BAJA, pageable)).thenReturn(paginaVacia);

    Page<Task> resultado = taskService.obtenerPorPrioridad(PrioridadTarea.BAJA, pageable);

    assertThat(resultado.getContent()).isEmpty();
    assertThat(resultado.getTotalElements()).isZero();
    verify(taskRepository, times(1)).findByPrioridad(PrioridadTarea.BAJA, pageable);
  }
}
