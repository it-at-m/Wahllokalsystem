package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ausdruck {

    @Valid
    @NotNull
    @EmbeddedId
    private WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart;

    @Lob
    private String content;

    @NotNull
    @Column(name = "erstellt_am")
    private Instant erstelltAm;
}
