package de.muenchen.oss.wahllokalsystem.briefwahlservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Wahlbriefdaten {

    // ========= //
    // Variables //
    // ========= //
    @NotNull
    @Size(max = 1024)
    @Id
    private String wahlbezirkID;

    private Long wahlbriefe;

    private Long verzeichnisseUngueltige;

    private Long nachtraege;

    private Long nachtraeglichUeberbrachte;

    private LocalDateTime zeitNachtraeglichUeberbrachte;
}
