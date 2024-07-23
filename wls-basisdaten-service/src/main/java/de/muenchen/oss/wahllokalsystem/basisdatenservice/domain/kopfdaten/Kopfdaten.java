package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten;

import static java.sql.Types.VARCHAR;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
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
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Kopfdaten {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    @Embedded
    @NaturalId
    @NotNull
    @ToString.Include
    private BezirkUndWahlID bezirkUndWahlID;

    @NotNull
    @ToString.Include
    private String gemeinde;

    @Enumerated(EnumType.STRING)
    @NotNull
    @ToString.Include
    private Stimmzettelgebietsart stimmzettelgebietsart;

    @NotNull
    @ToString.Include
    private String stimmzettelgebietsnummer;

    @NotNull
    @ToString.Include
    private String stimmzettelgebietsname;

    @NotNull
    @ToString.Include
    private String wahlname;

    @NotNull
    @ToString.Include
    private String wahlbezirknummer;
}
