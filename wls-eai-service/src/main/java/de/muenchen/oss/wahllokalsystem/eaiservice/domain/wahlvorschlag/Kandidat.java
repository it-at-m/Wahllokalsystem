package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Kandidat extends BaseEntity {

    @NotNull
    @ToString.Include
    String identifikator;

    @NotNull
    @ToString.Include
    String name;

    @NotNull
    @ToString.Include
    long listenposition;

    @NotNull
    @ToString.Include
    boolean direktkandidat;

    @NotNull
    @ToString.Include
    long tabellenSpalteInNiederschrift;

    @NotNull
    @ToString.Include
    boolean einzelbewerber;
}
