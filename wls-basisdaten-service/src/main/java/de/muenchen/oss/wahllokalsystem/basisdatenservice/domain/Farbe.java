package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farbe {

    @Column(name = "r")
    @NotNull
    @Min((long) 0.0)
    @Max((long) 255.0)
    private long r;


    @Column(name = "g")
    @NotNull
    @Min((long) 0.0)
    @Max((long) 255.0)
    private long g;


    @Column(name = "b")
    @NotNull
    @Min((long) 0.0)
    @Max((long) 255.0)
    private long b;
}

