package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class Referendumvorlage {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    @ToString.Include
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "referendumvorlagenID")
    @EqualsAndHashCode.Exclude
    private Referendumvorlagen referendumvorlagen;

    @NotNull
    @ToString.Include
    private String wahlvorschlagID;

    @NotNull
    @ToString.Include
    private Long ordnungszahl;

    @NotNull
    @ToString.Include
    private String kurzname;

    @NotNull
    @ToString.Include
    private String frage;

    @ElementCollection
    @CollectionTable(name = "referendumoption", joinColumns = @JoinColumn(name = "referendumvorlageID"))
    @NotNull
    private Set<Referendumoption> referendumoptionen;
}
