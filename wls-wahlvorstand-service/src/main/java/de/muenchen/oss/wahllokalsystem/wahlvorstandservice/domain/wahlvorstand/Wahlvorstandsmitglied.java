package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wahlvorstandsmitglied {

    @NotNull
    @Size(max = 1024)
    private String identifikator;

    @NotNull
    @Size(max = 255)
    private String familienname;

    @NotNull
    @Size(max = 255)
    private String vorname;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Funktion funktion;

    @Size(max = 255)
    private String funktionsname;

    @NotNull
    private boolean anwesend;
}
