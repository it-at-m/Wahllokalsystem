package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Embeddable
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Wahl")
public class Wahl {

    @Id
    @Column(name = "wahlID")
    @NotNull
    @Size(max = 1024)
    private String wahlID;


    @Column(name = "name")
    @NotNull
    @Size(max = 255)
    private String name;


    @Column(name = "reihenfolge")
    @NotNull
    private long reihenfolge;


    @Column(name = "waehlerverzeichnisNummer")
    @NotNull
    @Min((long) 1.0)
    private long waehlerverzeichnisNummer;

    @Column(name = "wahltag")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private java.time.LocalDate wahltag;

    @Column(name = "wahlart")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Wahlart wahlart;

    @Embedded
    private Farbe farbe;

    @Column(name = "nummer")
    @Size(max = 255)
    private String nummer;
}

