package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DummyClientImplTest {

    private final DummyClientImpl unitUnderTest = new DummyClientImpl();

    @Nested
    class GetAWerte {

        @Test
        void should_returnListOfAWerteModelWithTwoEntries_when_calledWithWahlbezirkID() {
            val result = unitUnderTest.getAWerte("wahlbezirkID");
            Assertions.assertThat(result).hasSize(1);
        }
    }
}
