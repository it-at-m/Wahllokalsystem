package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WahlUndBezirkID implements Serializable {

    @NotNull
    @Size(max = 1024)
    @JsonIgnore
    private String wahlbezirkID;

    @NotNull
    @Size(max = 1024)
    private String wahlID;
}
