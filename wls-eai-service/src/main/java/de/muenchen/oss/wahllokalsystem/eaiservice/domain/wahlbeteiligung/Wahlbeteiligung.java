package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Wahlbeteiligung extends BaseEntity {

    @NotNull
    @ToString.Include
    String wahlID;

    @NotNull
    @ToString.Include
    String wahlbezirkID;

    @NotNull
    @ToString.Include
    long anzahlWaehler;

    @NotNull
    @ToString.Include
    LocalDateTime meldeZeitpunkt;
}
