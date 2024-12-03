package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @EmbeddedId
    private BezirkUndWahlID bezirkUndWahlID;

    @Embedded
    @NotNull
    @AttributeOverrides(
        {
                @AttributeOverride(name = "validierungsstatus", column = @Column(name = "schnellmeldungValidierungsstatus")),
                @AttributeOverride(name = "gedruckt", column = @Column(name = "schnellmeldungGedruckt")),
                @AttributeOverride(name = "uebermittelt", column = @Column(name = "schnellmeldungUebermittelt")),
                @AttributeOverride(name = "sendeuhrzeit", column = @Column(name = "schnellmeldungSendeuhrzeit")),
        }
    )
    private Meldung schnellmeldung;

    @Embedded
    @NotNull
    @AttributeOverrides(
        {
                @AttributeOverride(name = "validierungsstatus", column = @Column(name = "niederschriftValidierungsstatus")),
                @AttributeOverride(name = "gedruckt", column = @Column(name = "niederschriftGedruckt")),
                @AttributeOverride(name = "uebermittelt", column = @Column(name = "niederschriftUebermittelt")),
                @AttributeOverride(name = "sendeuhrzeit", column = @Column(name = "niederschriftSendeuhrzeit")),
        }
    )
    private Meldung niederschrift;
}
