package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stimmabgabevermerke {

    @EmbeddedId
    @NotNull
    private BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer;

    @NotNull
    private long anzahlBlaetter;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumns(
            {
                    @JoinColumn(name = "wahlbezirkID", referencedColumnName = "wahlbezirkID"),
                    @JoinColumn(name = "waehlerverzeichnisNummer", referencedColumnName = "waehlerverzeichnisNummer")
            }
    )
    @NotNull
    @EqualsAndHashCode.Exclude
    @Size(min = 1)
    private Set<Wahldaten> wahldaten = new LinkedHashSet<>();
}
