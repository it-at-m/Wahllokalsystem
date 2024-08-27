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
@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ereigniseintrag {

    @Column
    @Size(max=1024)
    @Id
    private String eintragID;

    @Column(name="beschreibung")
    @Size(max=1024)
    private String beschreibung;

    @Column(name="uhrzeit")
    private java.time.LocalDateTime uhrzeit;

    @Column(name="ereignisart")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Ereignisart ereignisart;

    @ManyToOne
    @JoinColumn(name = "fk_wahlbezirkID")
    @EqualsAndHashCode.Exclude
    private Ereignisse fk_wahlbezirkID;

}
