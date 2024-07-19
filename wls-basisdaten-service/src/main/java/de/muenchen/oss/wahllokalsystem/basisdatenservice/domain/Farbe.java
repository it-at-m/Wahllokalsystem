package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
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
