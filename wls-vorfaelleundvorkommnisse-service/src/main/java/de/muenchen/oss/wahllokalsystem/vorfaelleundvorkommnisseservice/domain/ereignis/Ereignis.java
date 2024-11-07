package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Ereignis {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID id;

    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    @Size(max = 1024)
    private String beschreibung;

    private LocalDateTime uhrzeit;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Ereignisart ereignisart;
}
