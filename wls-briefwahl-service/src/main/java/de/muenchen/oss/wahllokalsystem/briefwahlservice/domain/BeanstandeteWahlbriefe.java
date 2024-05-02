package de.muenchen.oss.wahllokalsystem.briefwahlservice.domain;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapKeyColumn;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import lombok.Data;

@Entity
@Data
//TODO daraus kann man eine Tabelle machen: Key wird weiter spalte und grund wird eine Spalte mit eigenem De/Serializer
public class BeanstandeteWahlbriefe {

    @EmbeddedId
    private BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "Zurueckweisegruende", joinColumns = {
            @JoinColumn(name = "bw_wahlbezirkid", referencedColumnName = "wahlbezirkID"),
            @JoinColumn(name = "bw_waehlerverzeichnisnummer", referencedColumnName = "waehlerverzeichnisNummer")
    }
    )
    @Lob
    @Column(name = "zurueckweisegruende")
    @MapKeyColumn(name = "wahlID")
    @Enumerated(EnumType.STRING)
    private java.util.Map<String, Zurueckweisungsgrund[]> beanstandeteWahlbriefe = new HashMap<>();
}
