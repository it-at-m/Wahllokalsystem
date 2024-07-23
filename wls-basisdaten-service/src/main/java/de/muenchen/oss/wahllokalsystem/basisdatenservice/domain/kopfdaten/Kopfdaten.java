package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private BezirkUndWahlID bezirkUndWahlID;

    @NotNull
    @Size(max = 255)
    private String gemeinde;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Stimmzettelgebietsart stimmzettelgebietsart;

    @NotNull
    @Size(max = 255)
    private String stimmzettelgebietsnummer;

    @NotNull
    @Size(max = 255)
    private String stimmzettelgebietsname;

    @NotNull
    @Size(max = 255)
    private String wahlname;

    @NotNull
    @Size(max = 255)
    private String wahlbezirknummer;
}
