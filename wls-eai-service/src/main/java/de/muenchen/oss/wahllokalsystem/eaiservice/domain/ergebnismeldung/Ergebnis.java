package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ergebnis {

    @NotNull
    private String stimmenart;

    @NotNull
    private long wahlvorschlagsordnungszahl;

    @NotNull
    private long ergebnis;

    @NotNull
    private String wahlvorschlagID;

    @NotNull
    private String kandidatID;

    @CreationTimestamp
    private LocalDateTime erstellungszeit;

}
