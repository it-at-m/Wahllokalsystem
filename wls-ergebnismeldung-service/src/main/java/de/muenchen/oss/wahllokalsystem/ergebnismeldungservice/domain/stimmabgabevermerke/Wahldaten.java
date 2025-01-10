package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Wahldaten {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    @ToString.Include
    private UUID id;

    @Embedded
    @NaturalId
    @NotNull
    @ToString.Include
    private BezirkUndWahlIDUndWaehlerverzeichnisnummer bezirkUndWahlIDUndWaehlerverzeichnisnummer;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "wahldatenID", referencedColumnName = "id")
    @NotNull
    @ToString.Include
    private Set<Vermerk> vermerke = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "EingenommeneWahlscheine", joinColumns = @JoinColumn(name = "wahldatenID", referencedColumnName = "id"))
    @NotNull
    @Size(min = 1)
    @ToString.Include
    private Set<EingenommenerWahlscheine> eingenommenewahlscheine = new LinkedHashSet<>();
}
