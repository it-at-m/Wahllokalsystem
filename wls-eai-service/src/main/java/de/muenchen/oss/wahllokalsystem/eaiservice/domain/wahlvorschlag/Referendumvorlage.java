package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class Referendumvorlage extends BaseEntity {

    @NotNull
    private String wahlvorschlagID;

    @NotNull
    @ToString.Include
    private long ordnungszahl;

    @NotNull
    @ToString.Include
    private String kurzname;

    @NotNull
    @ToString.Include
    private String frage;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "referendumvorlageid")
    private Set<Referendumoption> referendumoptionen;

}
