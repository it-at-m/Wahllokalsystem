package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record KonfigurationSetModel(@NotNull @Size(max = 255) String schluessel,
                                    @Size(max = 1024) String wert,
                                    @Size(max = 1024) String beschreibung,
                                    @Size(max = 1024) String standardwert) {
}
