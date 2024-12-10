package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import static java.sql.Types.VARCHAR;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Stimmzettel {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "vermerkID")
    @EqualsAndHashCode.Exclude
    private Vermerk vermerk;

    @NotNull
    @ToString.Include
    private long anzahl;

    @NotNull
    private Stimmzettelart stimmzettelart;
}
