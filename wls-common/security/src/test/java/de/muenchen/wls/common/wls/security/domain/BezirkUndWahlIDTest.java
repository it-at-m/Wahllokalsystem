package de.muenchen.wls.common.wls.security.domain;

import de.muenchen.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BezirkUndWahlIDTest {

    @Test
    void parametersAreSetToCorrectProperty() {
        val wahlbezirkID = "3675";
        val wahlID = "1234";

        val result = new BezirkUndWahlID(wahlbezirkID, wahlID);

        Assertions.assertThat(result.getWahlbezirkID()).isEqualTo(wahlbezirkID);
        Assertions.assertThat(result.getWahlID()).isEqualTo(wahlID);
    }
}
