package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl;

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
    private String wahlID;

    @Size(max = 255)
    private String name;

    @Min(0)
    private long reihenfolge;

    @Min(0)
    private long waehlerverzeichnisnummer;

    @NotNull
    private LocalDate wahltag;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Wahlart wahlart;

    @Embedded
    private Farbe farbe;

    @Size(max = 255)
    private String nummer;
}