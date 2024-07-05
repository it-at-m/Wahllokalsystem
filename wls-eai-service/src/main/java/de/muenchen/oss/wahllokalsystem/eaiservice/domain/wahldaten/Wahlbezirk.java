package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Wahlbezirk extends BaseEntity {

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    private WahlbezirkArt wahlbezirkArt;

    @NotNull
    @ToString.Include
    private String nummer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "stimmzettelgebietID")
    private Stimmzettelgebiet stimmzettelgebiet;

    @ToString.Include
    private long a1;

    @ToString.Include
    private long a2;

    @ToString.Include
    private long a3;
}
