package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class WahlvorschlaegeListe extends BaseEntity {

    @NotNull
    @ToString.Include
    LocalDate wahltag;

    @NotNull
    @ToString.Include
    String wahlID;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wahlvorschlaegelisteid")
    Set<Wahlvorschlaege> wahlvorschlaegeliste;
}
