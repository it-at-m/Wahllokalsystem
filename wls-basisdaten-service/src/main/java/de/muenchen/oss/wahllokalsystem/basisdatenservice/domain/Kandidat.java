package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kandidat {

    @Column(name = "identifikator")
    @NotNull
    private String identifikator;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "listenposition")
    @NotNull
    private long listenposition;

    @Column(name = "direktkandidat")
    @NotNull
    private boolean direktkandidat;

    @Column(name = "tabellenSpalteInNiederschrift")
    @NotNull
    private long tabellenSpalteInNiederschrift;

    @Column(name = "einzelbewerber")
    @NotNull
    private boolean einzelbewerber;
}
