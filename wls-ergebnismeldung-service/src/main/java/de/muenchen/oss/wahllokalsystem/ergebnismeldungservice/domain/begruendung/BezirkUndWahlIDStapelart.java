package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BezirkUndWahlIDStapelart implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    @NotNull
    @Size(max = 1024)
    private String wahlID;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Stapelart stapelart;
}
