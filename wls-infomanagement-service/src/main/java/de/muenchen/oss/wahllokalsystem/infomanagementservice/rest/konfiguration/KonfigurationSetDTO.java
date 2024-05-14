package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import jakarta.validation.constraints.Size;

public record KonfigurationSetDTO(@Size(max = 1024) String wert,
                                  @Size(max = 1024) String beschreibung,
                                  @Size(max = 1024) String standardwert) {
}
