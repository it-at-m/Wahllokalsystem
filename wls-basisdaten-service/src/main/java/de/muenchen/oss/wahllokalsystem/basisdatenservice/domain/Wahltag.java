package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@Table(name = "Wahltag")
@Data
public class Wahltag {

    @Id
    @Column(name = "wahltagID")
    @NotNull
    @Size(max = 1024)
    private String wahltagID;


    @Column(name = "wahltag")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate wahltag;


    @Column(name = "beschreibung")
    @Size(max = 1024)
    private String beschreibung;

    @Column(name = "nummer")
    @Size(max = 1024)
    private String nummer;
    @Id
    private Long id;
}


