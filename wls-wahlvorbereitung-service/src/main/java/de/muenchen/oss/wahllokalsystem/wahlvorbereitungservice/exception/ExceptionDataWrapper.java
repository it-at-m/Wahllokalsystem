package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception;

import jakarta.validation.constraints.NotNull;

public record ExceptionDataWrapper(@NotNull String code, @NotNull String message) {
}
