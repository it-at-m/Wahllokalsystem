package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

// übernommen aus https://git.muenchen.de/wahlagenda2017/wls-service-vorfaelleundvorkommnisse/-/blob/master/src/main/java/de/muenchen/wls/vorfaelleundvorkommnisse/service/gen/domain/Ereignisse_.java?ref_type=heads

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

    private boolean keineVorfaelle = false;

    private boolean keineVorkommnisse = false;

    @ElementCollection(fetch = FetchType.EAGER)
    private java.util.List<Ereigniseintraege> ereigniseintraege = new java.util.ArrayList<>();     // todo: was soll hier passieren?

}
