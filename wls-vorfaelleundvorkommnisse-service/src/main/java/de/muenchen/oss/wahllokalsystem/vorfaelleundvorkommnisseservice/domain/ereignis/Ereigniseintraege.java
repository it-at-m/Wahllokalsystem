package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

/**
 * This class represents Ereigniseintraege.
 * <p>
 * Only oid and reference will be stored in the database.
 * The entity's content will be loaded according to the reference variable.
 * </p>
 */
@Embeddable
@Getter // von mir
@Setter // von mir
@NoArgsConstructor  // von mir
@AllArgsConstructor // von mir
public class Ereigniseintraege {

    @Column(name="beschreibung")
    @Size(max=1024)
    private String beschreibung;

    @Column(name="uhrzeit")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private java.time.LocalDateTime uhrzeit;

    @Column(name="ereignisart")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Ereignisart ereignisart;

    // todo: wann und wo werden diese funktionen verwendet?
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ereigniseintraege)) return false;
        Ereigniseintraege ereignis = (Ereigniseintraege) o;
        return Objects.equals(beschreibung, ereignis.beschreibung) &&
                Objects.equals(uhrzeit, ereignis.uhrzeit) &&
                ereignisart == ereignis.ereignisart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beschreibung, uhrzeit, ereignisart);
    }

    @Override
    public String toString() {
        return "Ereigniseintraege_{" +
                "beschreibung='" + beschreibung + '\'' +
                ", uhrzeit=" + uhrzeit +
                ", ereignisart=" + ereignisart +
                '}';
    }


}
