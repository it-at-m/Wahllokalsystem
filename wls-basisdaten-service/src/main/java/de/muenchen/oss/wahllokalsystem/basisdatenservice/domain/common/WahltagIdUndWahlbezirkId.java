package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WahltagIdUndWahlbezirkId implements Serializable {

  @NotNull @Size(max = 1024) private String wahltagID;

  @NotNull @Size(max = 1024) private String wahlbezirkID;
}
