package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@Getter
@Setter
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

    private java.time.LocalDateTime uhrzeit;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Ereignisart ereignisart;
}
