package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Wahltag")
public class Wahltag {

    @Id
    @NotNull
    @Size(max = 1024)
    @ToString.Include
    private String wahltagID;

    @NotNull
    @ToString.Include
    private LocalDate wahltag;

    @Size(max = 1024)
    @ToString.Include
    private String beschreibung;

    @Size(max = 1024)
    @ToString.Include
    private String nummer;
}
