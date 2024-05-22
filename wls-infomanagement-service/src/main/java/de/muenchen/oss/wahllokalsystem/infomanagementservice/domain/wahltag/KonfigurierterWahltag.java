package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class KonfigurierterWahltag {

    @NotNull
    private java.time.LocalDate wahltag;

    @Id
    @NotNull
    @Size(max = 255)
    private String wahltagID;

    private boolean active;

    @NotNull
    @Size(max = 255)
    private String nummer;
}
