package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDate;
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

    @Nested
    class GetWahltage {

        @Test
        void resultIsAnArrayWithPositiveSize() {
            val result = unitUnderTest.getWahltage(LocalDate.now().minusMonths(3));
            Assertions.assertThat(result).size().isPositive();
        }
    }
}
