package de.muenchen.oss.wahllokalsystem.wls.common.security.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BezirkIDUndWaehlerverzeichnisNummer implements Serializable {

    @NotNull
    @Size(max = 1000)
    private String wahlbezirkID;

    @NotNull
    @Size(max = 1000)
    private Long waehlerverzeichnisNummer;

}
