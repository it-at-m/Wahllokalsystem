package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class BezirkUndWahlIDStapelart implements Serializable {

    @Column(name = "wahlbezirkID")
    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    @Column(name = "wahlID")
    @NotNull
    @Size(max = 1024)
    private String wahlID;

    @Column(name = "stapelart")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Stapelart stapelart;
}

