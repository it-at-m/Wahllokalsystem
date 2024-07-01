package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class Wahltag extends BaseEntity {

    @NotNull
    @ToString.Include
    private LocalDate tag;

    @NotNull
    @ToString.Include
    private String beschreibung;

    @NotNull
    @ToString.Include
    private String nummer;
}