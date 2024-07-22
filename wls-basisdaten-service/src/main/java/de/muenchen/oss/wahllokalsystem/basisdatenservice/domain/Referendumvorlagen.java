package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Referendumvorlagen {

    @EmbeddedId
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
