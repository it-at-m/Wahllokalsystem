package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Kopfdaten {

    @EmbeddedId
    @NotNull
    @ToString.Include
    private BezirkUndWahlID bezirkUndWahlID;

    @NotNull
    @ToString.Include
    private String gemeinde;

    @Enumerated(EnumType.STRING)
    @NotNull
    @ToString.Include
    private Stimmzettelgebietsart stimmzettelgebietsart;

    @NotNull
    @ToString.Include
    private String stimmzettelgebietsnummer;

    @NotNull
    @ToString.Include
    private String stimmzettelgebietsname;

    @NotNull
    @ToString.Include
    private String wahlname;

    @NotNull
    @ToString.Include
    private String wahlbezirknummer;
}
