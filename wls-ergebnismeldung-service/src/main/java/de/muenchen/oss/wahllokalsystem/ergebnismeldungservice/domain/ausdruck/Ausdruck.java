package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
    @JsonUnwrapped
    private WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart;

    @Lob
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String content;

    @NotNull
    @Column(name = "erstellt_am")
    private Instant erstelltAm;
}
