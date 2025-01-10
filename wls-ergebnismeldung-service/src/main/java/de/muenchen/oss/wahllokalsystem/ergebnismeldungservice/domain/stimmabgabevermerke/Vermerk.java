package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import static java.sql.Types.VARCHAR;
import jakarta.persistence.CascadeType;
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
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Vermerk {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "wahldatenID")
    @EqualsAndHashCode.Exclude
    private Wahldaten wahldaten;

    @NotNull
    @ToString.Include
    private long blattnummer;

    @OneToMany(mappedBy = "vermerk", orphanRemoval = true, cascade = CascadeType.PERSIST)
    @NotNull
    private Set<Stimmzettel> stimmzetteln = new LinkedHashSet<>();

    public void addStimmzettel(final Stimmzettel stimmzettel) {
        stimmzettel.setVermerk(this);
        stimmzetteln.add(stimmzettel);
    }
}
