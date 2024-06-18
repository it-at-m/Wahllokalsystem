package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WLSWahlvorschlaege {

    @EmbeddedId
    @JsonUnwrapped
    @NotNull
    private BezirkUndWahlID bezirkUndWahlID;

    @Lob
    @Column(name = "wahlvorschlaegeAsJson")
    @NotNull
    private String wahlvorschlaegeAsJson;
}
