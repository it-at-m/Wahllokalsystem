package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WahlUndBezirkIDUndMeldungsart implements Serializable {

    @NotBlank
    @Size(max = 1024)
    private String wahlbezirkID;

    @NotBlank
    @Size(max = 1024)
    private String wahlID;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Meldungsart meldungsart;
}
