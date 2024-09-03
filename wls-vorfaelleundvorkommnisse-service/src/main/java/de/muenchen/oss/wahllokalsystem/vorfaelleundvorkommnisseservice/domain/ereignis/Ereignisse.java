package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ereignisse {

    @Id
    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    private boolean keineVorfaelle = false;

    private boolean keineVorkommnisse = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Ereigniseintrag", joinColumns = @JoinColumn(name = "fk_wahlbezirkID"))
    private java.util.List<Ereigniseintrag> ereigniseintrag = new java.util.ArrayList<>();

}
