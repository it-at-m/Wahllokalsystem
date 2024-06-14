package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Wahlvorstandsmitglied extends BaseEntity {

    @NotNull
    @ToString.Include
    private String vorname;

    @NotNull
    @ToString.Include
    private String nachname;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    private WahlvorstandFunktion funktion;

    @ToString.Include
    private boolean anwesend;

    @ToString.Include
    private LocalDateTime anwesenheitUpdatedOn;
}
