package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import jakarta.persistence.*;
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
public class Briefwahlvorbereitung {

    @Id
    @NotNull
    @Size(max = 255)
    private String wahlbezirkID;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "BWVorbereitung_Urnen", joinColumns = @JoinColumn(name = "vorbereitung_wahlbezirkID"))
    @NotNull
    @Size(min = 1)
    @Builder.Default
    private java.util.List<Wahlurne> urnenAnzahl = new java.util.ArrayList<>();
}