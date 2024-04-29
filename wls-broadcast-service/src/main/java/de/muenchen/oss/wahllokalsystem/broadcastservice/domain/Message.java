package de.muenchen.oss.wahllokalsystem.broadcastservice.domain;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    @JdbcTypeCode(VARCHAR)
    private UUID oid;

    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    @NotNull
    @Size(max = 1024)
    private String nachricht;

    @NotNull
    private LocalDateTime empfangsZeit;

}
