package de.muenchen.oss.wahllokalsystem.monitoringservice.domain.wahleranzahl;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Embeddable
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor

public class Waehleranzahl {

    @EmbeddedId
    @NotNull
    private BezirkUndWahlID bezirkUndWahlID;

    @NotNull
    private long anzahlWaehler;

    @NotNull
    private java.time.LocalDateTime uhrzeit;

}


