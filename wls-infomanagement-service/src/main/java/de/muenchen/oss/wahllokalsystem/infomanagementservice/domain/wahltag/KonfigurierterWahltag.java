package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag.WahltagStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KonfigurierterWahltag {

    @Column(name = "wahltag")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private java.time.LocalDate wahltag;

    @Id
    @Column(name = "wahltagID")
    @NotNull
    @Size(max = 255)
    private String wahltagID;

    @Column(name = "wahltagStatus")
    @Enumerated(EnumType.STRING)
    @NotNull
    private WahltagStatus wahltagStatus;

    @Column(name = "nummer")
    @NotNull
    @Size(max = 255)
    private String nummer;
}
