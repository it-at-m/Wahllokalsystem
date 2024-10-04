package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
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
public class Wahlvorschlag {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    //    @Id
    @NaturalId
    @NotNull
    @ToString.Include
    private String identifikator;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "wahlvorschlaegeID")
    @EqualsAndHashCode.Exclude
    private Wahlvorschlaege wahlvorschlaeage;

    @NotNull
    @ToString.Include
    private long ordnungszahl;

    @NotNull
    private String kurzname;

    @NotNull
    @ToString.Include
    private boolean erhaeltStimmen;

    @OneToMany(mappedBy = "wahlvorschlag", orphanRemoval = true)
    @NotNull
    private Set<Kandidat> kandidaten = new LinkedHashSet<>();

    public void addKandidat(final Kandidat kandidat) {
        kandidat.setWahlvorschlag(this);
        kandidaten.add(kandidat);
    }
}
