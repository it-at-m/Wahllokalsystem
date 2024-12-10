package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import static java.sql.Types.VARCHAR;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stimmabgabevermerke {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    @ToString.Include
    private UUID id;

    @NaturalId
    @NotNull
    private BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer;

    @NotNull
    private long anzahlBlaetter;

    @OneToMany(mappedBy = "wahldaten", orphanRemoval = true, cascade = CascadeType.PERSIST)
    @NotNull
    private Set<Wahldaten> wahldaten = new HashSet<>();

    public void addWahldaten(final Wahldaten wahlDaten) {
        wahlDaten.setStimmabgabevermerke(this);
        wahldaten.add(wahlDaten);
    }
}
