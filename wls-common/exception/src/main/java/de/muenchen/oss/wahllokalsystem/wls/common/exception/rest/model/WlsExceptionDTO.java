package de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record WlsExceptionDTO(@NotNull WlsExceptionCategory category, @NotNull String code, @NotNull String service, @NotNull String message) {

    public WlsExceptionDTO(final WlsExceptionCategory category, final String code, final String service, final String message) {
        this.category = Optional.ofNullable(category).orElseGet(() -> {
            log.warn("Category ist nicht enthalten");
            return WlsExceptionCategory.T;
        });

        this.code = Optional.ofNullable(code).orElseGet(() -> {
            log.warn("Code ist nicht enthalten. Setze allgemeinen Code");
            return ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT;
        });

        this.service = Optional.ofNullable(service).orElseGet(() -> {
            log.warn("Service ist nicht enthalten. Setze allgemeinen Service");
            return ExceptionKonstanten.SERVICE_UNBEKANNT;
        });

        this.message = Optional.ofNullable(message).orElseGet(() -> {
            log.warn("Nachricht ist nicht enthalten. Setze allgemeine Nachricht");
            return ExceptionKonstanten.MESSAGE_UNBEKANNTER_FEHLER;
        });
    }
}
