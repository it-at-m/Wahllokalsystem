package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stimmzettelumschlaege {

    @EmbeddedId
    private BezirkUndWahlID bezirkUndWahlID;

    private LocalDateTime urneneroeffnungsUhrzeit;

    @NotNull
    private long anzahlWaehler;

    private Long anzahlWaehler2;
}
