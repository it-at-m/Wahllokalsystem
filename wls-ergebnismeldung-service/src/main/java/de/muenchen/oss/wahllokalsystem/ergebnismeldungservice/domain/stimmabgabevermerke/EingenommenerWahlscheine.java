package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EingenommenerWahlscheine {

    @NotNull
    private long anzahl;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Stimmzettelart stimmzettelart;
}
