package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EreignisDTO(
    String beschreibung, LocalDateTime uhrzeit, @NotNull EreignisartDTO ereignisart) {}
