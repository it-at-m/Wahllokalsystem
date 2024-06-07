package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration;

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
public class Konfiguration {

    @Id
    @NotNull
    @Size(max = 255)
    private String schluessel;

    @Size(max = 1024)
    private String wert;

    @Size(max = 1024)
    private String beschreibung;

    @Size(max = 1024)
    private String standardwert;
}
