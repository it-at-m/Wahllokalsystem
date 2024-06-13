package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FortsetzungsUhrzeit {

    @Id
    @NotNull
    @Size(max = 255)
    private String wahlbezirkID;

    @NotNull
    private java.time.LocalDateTime fortsetzungsUhrzeit;
}
