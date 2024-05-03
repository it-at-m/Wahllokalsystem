package de.muenchen.oss.wahllokalsystem.briefwahlservice.domain;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.utils.ZurueckweisungsgrundConverter;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.validation.constraints.NotNull;
import java.sql.Types;
import java.util.HashMap;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Data
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
    @Column(name = "zurueckweisegruende")
    @MapKeyColumn(name = "wahlID")
    @Convert(attributeName = "value", converter = ZurueckweisungsgrundConverter.class)
    @JdbcTypeCode(Types.CLOB)
    private java.util.Map<String, Zurueckweisungsgrund[]> beanstandeteWahlbriefe = new HashMap<>();
}
