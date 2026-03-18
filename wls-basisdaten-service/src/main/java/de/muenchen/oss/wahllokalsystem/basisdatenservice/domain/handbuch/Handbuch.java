package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class Handbuch {

    @EmbeddedId
    @ToString.Include
    private WahltagIdUndWahlbezirksart wahltagIdUndWahlbezirksart;

    @NotNull
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    private byte[] handbuch;
}
