package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "Ungueltigews")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UngueltigeWahlscheine {

    // ========= //
    // Variables //
    // ========= //
    @EmbeddedId
    private WahltagIdUndWahlbezirksart wahltagIdUndWahlbezirksart;

    @Column(name = "ungueltigews")
    @NotNull
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    private byte[] ungueltigeWahlscheine;
}
