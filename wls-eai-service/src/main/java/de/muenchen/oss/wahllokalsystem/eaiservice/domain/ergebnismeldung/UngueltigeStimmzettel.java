package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UngueltigeStimmzettel {

    private String stimmenart;

    private Long anzahl;

    @NotNull
    private String wahlvorschlagID;

}
