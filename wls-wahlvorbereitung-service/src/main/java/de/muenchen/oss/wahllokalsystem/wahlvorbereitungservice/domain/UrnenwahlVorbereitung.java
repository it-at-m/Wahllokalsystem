package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrnenwahlVorbereitung {

    @Id
    @NotNull
    @Size(max = 255)
    private String wahlbezirkID;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "UWVorbereitung_Urnen", joinColumns = @JoinColumn(name = "vorbereitung_wahlbezirkID"))
    @NotNull
    @Size(min = 1)
    @Builder.Default
    private java.util.List<Wahlurne> urnenAnzahl = new java.util.ArrayList<>();

    private long anzahlWahlkabinen;

    private long anzahlWahltische;

    private long anzahlNebenraeume;

}
