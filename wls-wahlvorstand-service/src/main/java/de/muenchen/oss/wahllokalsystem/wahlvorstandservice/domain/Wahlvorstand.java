package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
public class Wahlvorstand {

    @Id
    @NotNull
    @Size(max = 1024)
    private String wahlbezirkID;

    private LocalDateTime anwesenheitBeginn;

    @NotNull
    @Size(min = 1)
    @OneToMany(mappedBy = "wahlvorstand", orphanRemoval = true)
    private java.util.List<Wahlvorstandsmitglied> wahlvorstandsmitglieder = new java.util.ArrayList<>();
}