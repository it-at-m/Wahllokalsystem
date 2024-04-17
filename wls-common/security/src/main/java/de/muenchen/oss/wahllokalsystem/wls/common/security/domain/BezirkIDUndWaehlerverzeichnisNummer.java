package de.muenchen.oss.wahllokalsystem.wls.common.security.domain;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BezirkIDUndWaehlerverzeichnisNummer implements Serializable {

    @NotNull
    private String wahlbezirkID;

    @NotNull
    private Long waehlerverzeichnisNummer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BezirkIDUndWaehlerverzeichnisNummer that)) return false;
        return Objects.equals(wahlbezirkID, that.wahlbezirkID) &&
                Objects.equals(waehlerverzeichnisNummer, that.waehlerverzeichnisNummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wahlbezirkID, waehlerverzeichnisNummer);
    }

    @Override
    public String toString() {
        return "BezirkIDUndWaehlerverzeichnisNummer{" +
                "wahlbezirkID='" + wahlbezirkID + '\'' +
                ", waehlerverzeichnisNummer=" + waehlerverzeichnisNummer +
                '}';
    }
}
