package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class Wahlvorstand {

    @Id
    @NotNull
    @Size(max=1024)
    private String wahlbezirkID;

    private LocalDateTime anwesenheitBeginn;

    @NotNull
    @Size(min=1)
    @OneToMany(mappedBy = "wahlvorstand", orphanRemoval = true)
    private List<Wahlvorstandsmitglied> wahlvorstandsmitglieder = new ArrayList<>();
}
