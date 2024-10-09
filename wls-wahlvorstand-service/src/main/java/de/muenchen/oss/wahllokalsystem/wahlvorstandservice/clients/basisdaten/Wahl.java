package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Data
@Entity
public class Wahl {

    @Id
    @NotNull
    @Size(max = 1024)
    private String wahlID;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private long reihenfolge;

    @NotNull
    @Min((long) 1.0)
    private long waehlerverzeichnisNummer;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate wahltag;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Wahlart wahlart;

    @Embedded
    private Farbe farbe;
}
