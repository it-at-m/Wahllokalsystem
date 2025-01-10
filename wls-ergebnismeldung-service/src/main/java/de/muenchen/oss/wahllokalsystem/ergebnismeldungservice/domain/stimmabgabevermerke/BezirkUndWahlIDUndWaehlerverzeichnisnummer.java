package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import jakarta.persistence.Column;
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

    @Column(name = "wahlbezirkID")
    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    @Column(name = "wahlID")
    @NotNull
    @Size(max = 1024)
    private String wahlID;

    @Column(name = "waehlerverzeichnisNummer")
    @NotNull
    private Long waehlerverzeichnisNummer;
}
