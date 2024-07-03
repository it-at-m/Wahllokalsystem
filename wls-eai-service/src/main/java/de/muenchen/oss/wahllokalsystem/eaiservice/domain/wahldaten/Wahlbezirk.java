package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;
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
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Wahlbezirk extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    private WahlbezirkArtDTO wahlbezirkArt;

    @NotNull
    private String nummer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "stimmzettelgebietID")
    private Stimmzettelgebiet stimmzettelgebiet;

    private long a1;

    private long a2;

    private long a3;
}