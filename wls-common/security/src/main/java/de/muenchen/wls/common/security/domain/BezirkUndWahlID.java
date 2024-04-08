package de.muenchen.wls.common.security.domain;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class BezirkUndWahlID implements Serializable {

    @Column(name = "wahlID")
    @NotNull
    @Size(max = 1000)
    private String wahlID;

    @Column(name = "wahlbezirkID")
    @NotNull
    @Size(max = 1000)
    private String wahlbezirkID;

    public BezirkUndWahlID() {

    }

    public BezirkUndWahlID(String wahlbezirkID, String wahlID) {
        this.wahlbezirkID = wahlbezirkID;
        this.wahlID = wahlID;
    }

    public String getWahlID() {
        return wahlID;
    }

    public void setWahlID(String wahlID) {
        this.wahlID = wahlID;
    }

    public String getWahlbezirkID() {
        return wahlbezirkID;
    }

    public void setWahlbezirkID(String wahlbezirkID) {
        this.wahlbezirkID = wahlbezirkID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BezirkUndWahlID)) return false;
        BezirkUndWahlID that = (BezirkUndWahlID) o;
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
