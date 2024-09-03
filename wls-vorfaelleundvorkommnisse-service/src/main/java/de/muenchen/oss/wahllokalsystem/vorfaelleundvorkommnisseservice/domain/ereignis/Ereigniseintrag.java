package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * This class represents Ereigniseintraege.
 * <p>
 * Only oid and reference will be stored in the database.
 * The entity's content will be loaded according to the reference variable.
 * </p>
 */
@Embeddable
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ereigniseintrag {

    @Column(name="beschreibung")
    @Size(max=1024)
    private String beschreibung;

    @Column(name="uhrzeit")
    private java.time.LocalDateTime uhrzeit;

    @Column(name="ereignisart")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Ereignisart ereignisart;
}
