package com.boletin1.tareas.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios puros (sin contexto de Spring) que comprueban las reglas de negocio
 * declaradas como anotaciones de Bean Validation sobre la entidad Task.
 */
class TaskValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void noSePuedeCrearUnaTareaConFechaLimitePasada() {
        Task task =
                new Task(
                        null,
                        "Entregar boletin",
                        "Descripcion",
                        EstadoTarea.PENDIENTE,
                        PrioridadTarea.ALTA,
                        LocalDate.now().minusDays(1));

        Set<ConstraintViolation<Task>> violaciones = validator.validate(task);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("fechaLimite"));
    }

    @Test
    void unaFechaLimiteDeHoyOFuturaEsValida() {
        Task task =
                new Task(
                        null,
                        "Entregar boletin",
                        "Descripcion",
                        EstadoTarea.PENDIENTE,
                        PrioridadTarea.ALTA,
                        LocalDate.now());

        Set<ConstraintViolation<Task>> violaciones = validator.validate(task);

        assertThat(violaciones).noneMatch(v -> v.getPropertyPath().toString().equals("fechaLimite"));
    }

    @Test
    void noSePuedeCrearUnaTareaConTituloVacio() {
        Task task =
                new Task(
                        null,
                        "   ",
                        "Descripcion",
                        EstadoTarea.PENDIENTE,
                        PrioridadTarea.MEDIA,
                        LocalDate.now().plusDays(3));

        Set<ConstraintViolation<Task>> violaciones = validator.validate(task);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("titulo"));
    }
}
