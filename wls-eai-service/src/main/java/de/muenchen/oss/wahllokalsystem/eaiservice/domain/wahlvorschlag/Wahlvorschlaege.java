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
public class Wahlvorschlaege extends BaseEntity {

    @NotNull
    String wahlbezirkID;

    @NotNull
    String wahlID;

    @NotNull
    String stimmzettelgebietID;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "wahlvorschlaegeid")
    Set<Wahlvorschlag> wahlvorschlaege;
}
