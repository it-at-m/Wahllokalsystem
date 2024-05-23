package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Waehlerverzeichnis {

    @EmbeddedId
    private BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer;

    private Boolean verzeichnisLagVor;

    private Boolean berichtigungVorBeginnDerAbstimmung;

    private Boolean nachtraeglicheBerichtigung;

    private Boolean mitteilungUeberUngueltigeWahlscheineErhalten;
}
