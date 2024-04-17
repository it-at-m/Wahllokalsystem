package de.muenchen.wls.common.security;

import de.muenchen.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BezirkIDUndWahlIDTest {

    @Test
    void returnsHash() {
        val unitUnderTest = new BezirkUndWahlID();
        unitUnderTest.setWahlID("1");
        unitUnderTest.setWahlbezirkID("2");
        Assertions.assertThat(unitUnderTest.hashCode()).isEqualTo(2530);
    }

    @Test
    void returnsString() {
        val unitUnderTest = new BezirkUndWahlID();
        unitUnderTest.setWahlID("1");
        unitUnderTest.setWahlbezirkID("2");
        Assertions.assertThat(unitUnderTest.toString()).isEqualTo("de.muenchen.wls.common.security.domain.BezirkUndWahlID{" + "wahlID='" + 1 + '\'' +", wahlbezirkID='" + 2 + '\'' +'}');
    }

    @Test
    void equals() {
        val unitUnderTest = new BezirkUndWahlID();
        unitUnderTest.setWahlID("1");
        unitUnderTest.setWahlbezirkID("2");
        val falseObject = new BezirkUndWahlID();
        falseObject.setWahlID("2");
        falseObject.setWahlbezirkID("3");
        Assertions.assertThat(unitUnderTest.equals(falseObject)).isFalse();
    }
}
