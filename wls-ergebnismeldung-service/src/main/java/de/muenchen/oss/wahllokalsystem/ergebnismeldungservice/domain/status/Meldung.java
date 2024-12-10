package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Embeddable
@Data
public class Meldung {

    @Enumerated(EnumType.STRING)
    @NotNull
    private Validierungsstatus validierungsstatus;

    @NotNull
    private boolean gedruckt;

    private Boolean uebermittelt;

    private LocalDateTime sendeuhrzeit;
}
