package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.basisdatenClient;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Embeddable
@NoArgsConstructor
public class Farbe {

    @NotNull
    @Min((long) 0.0)
    @Max((long) 255.0)
    private long r;

    @NotNull
    @Min((long) 0.0)
    @Max((long) 255.0)
    private long g;

    @NotNull
    @Min((long) 0.0)
    @Max((long) 255.0)
    private long b;
}
