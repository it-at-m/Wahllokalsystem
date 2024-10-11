package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class Wahlvorstandsmitglied {

    @Id
    @NotNull
    @Size(max = 1024)
    private String identifikator;

    @NotNull
    @Size(max = 255)
    private String familienname;

    @NotNull
    @Size(max=255)
    private String vorname;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Funktion funktion;

    @Size(max=255)
    private String funktionsName;

    @NotNull
    private boolean anwesend;

    @ManyToOne
    @JoinColumn(name = "wahlvorstand_wahlbezirkID")
    @EqualsAndHashCode.Exclude
    private Wahlvorstand wahlvorstand;
}