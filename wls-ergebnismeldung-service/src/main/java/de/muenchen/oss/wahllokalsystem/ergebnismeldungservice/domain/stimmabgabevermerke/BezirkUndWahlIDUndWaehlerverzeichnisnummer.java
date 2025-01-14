package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BezirkUndWahlIDUndWaehlerverzeichnisnummer implements Serializable {

    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    @NotNull
    @Size(max = 1024)
    private String wahlID;

    @NotNull
    private Long waehlerverzeichnisNummer;
}
