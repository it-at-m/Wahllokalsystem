package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Wahl")
public class Wahl {

    @Id
    @NotNull
    @Size(max = 1024)
    @ToString.Include
    private String wahlID;

    @Size(max = 255)
    @ToString.Include
    private String name;

    @NotNull
    @ToString.Include
    private long reihenfolge;

    @NotNull
    @Min((long) 1.0)
    private long waehlerverzeichnisNummer;

    @NotNull
    @ToString.Include
    private LocalDate wahltag;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Wahlart wahlart;

    @Embedded
    private Farbe farbe;

    @Size(max = 255)
    @ToString.Include
    private String nummer;
}
