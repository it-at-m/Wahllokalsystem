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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Indexed;

@Entity
@Indexed
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Wahltag")
public class Wahltag {

    @Id
    @NotNull
    @Size(max = 1024)
    @ToString.Include
    private String wahltagID;

    @Column(name = "wahltag")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    @ToString.Include
    private LocalDate wahltag;

    @Column(name = "beschreibung")
    @Size(max = 1024)
    @ToString.Include
    private String beschreibung;

    @Column(name = "nummer")
    @Size(max = 1024)
    @ToString.Include
    private String nummer;
}
