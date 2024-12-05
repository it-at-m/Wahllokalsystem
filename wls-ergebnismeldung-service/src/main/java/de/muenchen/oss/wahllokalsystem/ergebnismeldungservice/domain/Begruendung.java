package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class Begruendung extends BaseEntity {

    @EmbeddedId
    private BezirkUndWahlIDStapelart bezirkUndWahlIDStapelart;

    @Column(name = "grund1")
    @NotNull
    @Size(max = 1024)
    private String grund1;

    @Column(name = "grund2")
    @Size(max = 1024)
    private String grund2;

    @Column(name = "nachzaehlung")
    @NotNull
    private boolean nachzaehlung;

    @Column(name = "unstimmigkeiten")
    @NotNull
    private boolean unstimmigkeiten;


}

