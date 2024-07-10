package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
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
public class Wahlvorschlag extends BaseEntity {

    @NotNull
    @ToString.Include
    private long ordnungszahl;

    @NotNull
    @ToString.Include
    private String kurzname;

    @NotNull
    @ToString.Include
    private boolean erhaeltStimmen;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wahlvorschlagID")
    private java.util.Set<Kandidat> kandidaten;
}
