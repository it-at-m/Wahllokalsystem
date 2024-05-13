package de.muenchen.oss.wahllokalsystem.briefwahlservice.exception;

import jakarta.validation.constraints.NotNull;

public record ExceptionDataWrapper(@NotNull String code, @NotNull String message) {
}
