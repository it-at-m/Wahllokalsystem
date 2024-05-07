package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record KonfigurationModel(@NotNull @Size(max = 255) String schluessel,
                                 @Size(max = 1024) String wert,
                                 @Size(max = 1024) String beschreibung,
                                 @Size(max = 1024) String standardwert
) {
}
