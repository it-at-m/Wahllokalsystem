package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
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
