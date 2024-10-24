package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wahlvorstandsmitglied {

    @Id
    @NotNull
    @Size(max=1024)
    private String identifikator;

    @NotNull
    @Size(max=255)
    private String familienname;

    @NotNull
    @Size(max=255)
    private String vorname;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Funktion funktion;

    @Size(max=255)
    private String funktionsname;

    @NotNull
    private boolean anwesend;

    @ManyToOne
    @JoinColumn(name="wahlvorstand_wahlbezirkID")
    @EqualsAndHashCode.Exclude
    private Wahlvorstand wahlvorstand;
}
