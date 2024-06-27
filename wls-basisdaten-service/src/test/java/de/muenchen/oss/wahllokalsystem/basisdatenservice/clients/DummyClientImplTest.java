package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DummyClientImplTest {

    private final DummyClientImpl unitUnderTest = new DummyClientImpl();

    @Nested
    class GetWahlvorschlaege {

        @Test
        void resultIsANonNullObject() {
            val result = unitUnderTest.getWahlvorschlaege(new BezirkUndWahlID("wahlID", "wahlbezirkID"));

            Assertions.assertThat(result).hasNoNullFieldsOrProperties();
        }
    }
}
