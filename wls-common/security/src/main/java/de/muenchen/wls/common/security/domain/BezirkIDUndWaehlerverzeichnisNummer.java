package de.muenchen.wls.common.security.domain;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class BezirkIDUndWaehlerverzeichnisNummer implements Serializable {

    @Column(name = "wahlbezirkID")
    @NotNull
    @Size(max = 1000)
    private String wahlbezirkID;

    @Column(name = "waehlerverzeichnisNummer")
    @NotNull
    private Long waehlerverzeichnisNummer;

    public BezirkIDUndWaehlerverzeichnisNummer() {

    }

    public BezirkIDUndWaehlerverzeichnisNummer(String wahlbezirkID, Long waehlerverzeichnisNummer) {
        this.wahlbezirkID = wahlbezirkID;
        this.waehlerverzeichnisNummer = waehlerverzeichnisNummer;
    }

    @NotNull
    public String getWahlbezirkID() {
        return wahlbezirkID;
    }

    public void setWahlbezirkID(@NotNull String wahlbezirkID) {
        this.wahlbezirkID = wahlbezirkID;
    }

    @NotNull
    public Long getWaehlerverzeichnisNummer() {
        return waehlerverzeichnisNummer;
    }

    public void setWaehlerverzeichnisNummer(@NotNull Long waehlerverzeichnisNummer) {
        this.waehlerverzeichnisNummer = waehlerverzeichnisNummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BezirkIDUndWaehlerverzeichnisNummer)) return false;
        BezirkIDUndWaehlerverzeichnisNummer that = (BezirkIDUndWaehlerverzeichnisNummer) o;
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
