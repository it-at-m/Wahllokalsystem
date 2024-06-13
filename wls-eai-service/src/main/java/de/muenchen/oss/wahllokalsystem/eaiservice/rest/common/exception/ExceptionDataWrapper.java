package de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception;

import jakarta.validation.constraints.NotNull;

public record ExceptionDataWrapper(@NotNull String code, @NotNull String message) {
}
