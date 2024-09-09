package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ereignis {

    @Id
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
