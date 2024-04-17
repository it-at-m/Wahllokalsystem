package de.muenchen.wls.common.security;

import de.muenchen.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BezirkIDUndWaehlerverzeichnisNummerTest {

    @Test
    void returnsHash() {
        val unitUnderTest = new BezirkIDUndWaehlerverzeichnisNummer();
        unitUnderTest.setWaehlerverzeichnisNummer(Long.valueOf("1"));
        unitUnderTest.setWahlbezirkID("2");
        Assertions.assertThat(unitUnderTest.hashCode()).isEqualTo(2512);
    }

    @Test
    void returnsString() {
        val unitUnderTest = new BezirkIDUndWaehlerverzeichnisNummer();
        unitUnderTest.setWaehlerverzeichnisNummer(Long.valueOf("1"));
        unitUnderTest.setWahlbezirkID("2");
        Assertions.assertThat(unitUnderTest.toString()).isEqualTo("BezirkIDUndWaehlerverzeichnisNummer{" + "wahlbezirkID='" + 2 + '\'' +", waehlerverzeichnisNummer=" + 1 +'}');
    }

    @Test
    void equals() {
        val unitUnderTest = new BezirkIDUndWaehlerverzeichnisNummer();
        unitUnderTest.setWaehlerverzeichnisNummer(Long.valueOf("1"));
        unitUnderTest.setWahlbezirkID("2");
        val falseObject = new BezirkIDUndWaehlerverzeichnisNummer();
        falseObject.setWaehlerverzeichnisNummer(Long.valueOf("2"));
        falseObject.setWahlbezirkID("3");
        Assertions.assertThat(unitUnderTest.equals(falseObject)).isFalse();
    }
}
