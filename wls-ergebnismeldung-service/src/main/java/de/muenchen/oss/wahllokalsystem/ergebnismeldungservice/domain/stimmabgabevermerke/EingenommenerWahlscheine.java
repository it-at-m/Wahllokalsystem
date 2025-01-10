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
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@ToString(onlyExplicitlyIncluded = true)
public class EingenommenerWahlscheine {

    @NotNull
    @ToString.Include
    private long anzahl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Stimmzettelart stimmzettelart;
}
