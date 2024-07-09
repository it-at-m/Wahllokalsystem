package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import static java.sql.Types.VARCHAR;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Wahltag {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    @EqualsExclude
    private UUID id;

    @NaturalId
    @NotNull
    @ToString.Include
    private String wahltagID;

    @NotNull
    @ToString.Include
    private LocalDate wahltag;

    @NotNull
    @ToString.Include
    private String beschreibung;

    @NotNull
    @ToString.Include
    private String nummer;
}
