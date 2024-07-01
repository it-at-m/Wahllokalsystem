package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static java.sql.Types.VARCHAR;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Embeddable
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Wahlvorschlaege {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    @Embedded
    @NaturalId
    @NotNull
    @ToString.Include
    private BezirkUndWahlID bezirkUndWahlID;

    @NotNull
    @ToString.Include
    private String stimmzettelgebietID;

    @OneToMany(mappedBy = "wahlvorschlaeage", orphanRemoval = true)
    @NotNull
    @Size(min = 1)
    private Set<Wahlvorschlag> wahlvorschlaege = new java.util.LinkedHashSet<>();

    public void addWahlvorschlag(final Wahlvorschlag wahlvorschlag) {
        wahlvorschlag.setWahlvorschlaeage(this);
        wahlvorschlaege.add(wahlvorschlag);
    }
}
