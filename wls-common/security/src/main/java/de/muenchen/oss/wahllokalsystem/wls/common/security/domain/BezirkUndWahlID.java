package de.muenchen.oss.wahllokalsystem.wls.common.security.domain;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BezirkUndWahlID implements Serializable {

    private String wahlID;
    private String wahlbezirkID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BezirkUndWahlID that)) return false;
        return Objects.equals(wahlID, that.wahlID) &&
                Objects.equals(wahlbezirkID, that.wahlbezirkID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wahlID, wahlbezirkID);
    }

    @Override
    public String toString() {
        return "de.muenchen.wls.common.security.domain.BezirkUndWahlID{" +
                "wahlID='" + wahlID + '\'' +
                ", wahlbezirkID='" + wahlbezirkID + '\'' +
                '}';
    }
}
