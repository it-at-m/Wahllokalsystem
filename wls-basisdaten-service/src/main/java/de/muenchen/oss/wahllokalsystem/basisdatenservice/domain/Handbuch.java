package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class Handbuch {

    @EmbeddedId
    @ToString.Include
    private WahltagIdUndWahlbezirksart wahltagIdUndWahlbezirksart;

    @NotNull
    @Lob
    private byte[] handbuch;
}
