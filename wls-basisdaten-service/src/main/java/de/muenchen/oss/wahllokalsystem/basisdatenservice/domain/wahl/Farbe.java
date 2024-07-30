package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Farbe {

    @NotNull
    @Min(0)
    @Max(255)
    private long r;

    @NotNull
    @Min(0)
    @Max(255)
    private long g;

    @NotNull
    @Min(0)
    @Max(255)
    private long b;
}
