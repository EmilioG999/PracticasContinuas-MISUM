package com.boletin1.tareas.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    int status, String message, LocalDateTime timestamp, Map<String, String> errores) {

  public ErrorResponse(int status, String message, LocalDateTime timestamp) {
    this(status, message, timestamp, null);
  }
}
