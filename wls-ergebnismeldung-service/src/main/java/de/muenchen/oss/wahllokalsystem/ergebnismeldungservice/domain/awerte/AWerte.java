package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AWerte {

    @EmbeddedId
    @NotNull
    private BezirkUndWahlID bezirkUndWahlID;

    @NotNull
    private long a1;

    private Long a2;

}
