package de.muenchen.oss.wahllokalsystem.wls.common.exception.util;

import jakarta.validation.constraints.NotNull;

public record ExceptionDataWrapper(@NotNull String code, @NotNull String message) {
}
