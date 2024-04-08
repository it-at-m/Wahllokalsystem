package de.muenchen.wls.common.wls.security.domain;

import de.muenchen.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.wls.common.wls.security.testutils.LoggerExtension;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;
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

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @Nested
    class TestLoggingEvents {
        @Test
        void noLoggEventsOnCorrectUsedConstructor() {
            new BezirkUndWahlID("wahlbezirkID", "wahlID");

            Assertions.assertThat(loggerExtension.getFormattedMessages().isEmpty()).isTrue();
        }

        @Test
        void warnOnWahlbezirkIDIsNull() {
            new BezirkUndWahlID(null, "wahlID");

            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
        }

        @Test
        void warnOnWahlIDIsNull() {
            new BezirkUndWahlID("wahlbezirkID", null);

            Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
        }
    }
}
