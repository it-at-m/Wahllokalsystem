package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class Stimmzettelgebiet extends BaseEntity {

    @ToString.Include
    private String nummer;

    @ToString.Include
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Stimmzettelgebietsart stimmzettelgebietart;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wahlID")
    private Wahl wahl;

}