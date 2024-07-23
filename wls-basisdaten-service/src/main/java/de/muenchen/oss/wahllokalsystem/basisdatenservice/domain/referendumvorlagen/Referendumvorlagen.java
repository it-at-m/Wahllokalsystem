package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen;

import static java.sql.Types.VARCHAR;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
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
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Referendumvorlagen {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    @ToString.Include
    private UUID id;

    @NaturalId
    @Embedded
    private BezirkUndWahlID bezirkUndWahlID;

    @NotNull
    private String stimmzettelgebietID;

    @OneToMany(mappedBy = "referendumvorlagen", orphanRemoval = true)
    @NotNull
    private Set<Referendumvorlage> referendumvorlagen = new HashSet<>();

    public void addReferendumvorlage(Referendumvorlage referendumvorlage) {
        referendumvorlage.setReferendumvorlagen(this);
        referendumvorlagen.add(referendumvorlage);
    }
}
