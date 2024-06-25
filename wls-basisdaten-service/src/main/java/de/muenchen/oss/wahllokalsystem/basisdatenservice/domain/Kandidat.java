package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Kandidat {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    //    @Id
    @NaturalId
    @NotNull
    @ToString.Include
    private String identifikator;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "wahlvorschlagID")
    @EqualsAndHashCode.Exclude
    private Wahlvorschlag wahlvorschlag;

    @NotNull
    @ToString.Include
    private String name;

    @NotNull
    @ToString.Include
    private long listenposition;

    @NotNull
    @ToString.Include
    private boolean direktkandidat;

    @NotNull
    @ToString.Include
    private long tabellenSpalteInNiederschrift;

    @NotNull
    @ToString.Include
    private boolean einzelbewerber;
}
