package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Wahlbezirk {

    @Id
    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    @NotNull
    private LocalDate wahltag;

    @NotNull
    @Size(max = 255)
    private String nummer;

    @Enumerated(EnumType.STRING)
    @NotNull
    private WahlbezirkArt wahlbezirkart;

    @NotNull
    @Size(max = 255)
    private String wahlnummer;

    @NotNull
    @Size(max = 255)
    private String wahlID;
}
