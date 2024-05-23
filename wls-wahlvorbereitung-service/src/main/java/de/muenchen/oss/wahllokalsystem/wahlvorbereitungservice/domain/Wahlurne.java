package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wahlurne {

    @NotNull
    @Size(max = 255)
    private String wahlID;

    @NotNull
    private long anzahl;

    private Boolean urneVersiegelt;

}
