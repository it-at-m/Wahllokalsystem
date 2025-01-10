package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import static java.sql.Types.VARCHAR;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Wahldaten {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    @Embedded
    @NaturalId
    @NotNull
    @ToString.Include
    private BezirkUndWahlIDUndWaehlerverzeichnisnummer bezirkUndWahlIDUndWaehlerverzeichnisnummer;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "stimmabgabevermerkeID")
    @EqualsAndHashCode.Exclude
    private Stimmabgabevermerke stimmabgabevermerke;

    @OneToMany(mappedBy = "wahldaten", orphanRemoval = true, cascade = CascadeType.PERSIST)
    @NotNull
    private Set<Vermerk> vermerke = new LinkedHashSet<>();

    public void addVermerk(final Vermerk vermerk) {
        vermerk.setWahldaten(this);
        vermerke.add(vermerk);
    }

    @OneToMany(mappedBy = "wahldaten", orphanRemoval = true, cascade = CascadeType.PERSIST)
    @NotNull
    @Size(min = 1)
    private Set<EingenommenerWahlschein> eingenommenewahlscheine = new LinkedHashSet<>();

    public void addEingenommenerWahlschein(final EingenommenerWahlschein eingenommenerWahlschein) {
        eingenommenerWahlschein.setWahldaten(this);
        eingenommenewahlscheine.add(eingenommenerWahlschein);
    }
}
