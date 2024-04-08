package de.muenchen.wls.common.wls.security.domain;

import de.muenchen.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.wls.common.wls.security.testutils.LoggerExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;

@Nested
class BezirkIDUndWaehlerverzeichnisNummerTest {

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @Test
    void noLoggEventsOnCorrectUsedConstructor() {
        new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 35L);

        Assertions.assertThat(loggerExtension.getFormattedMessages().isEmpty()).isTrue();
    }

    @Test
    void warnOnWahlbezirkIDIsNull() {
        new BezirkIDUndWaehlerverzeichnisNummer(null, 35L);

        Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
    }

    @Test
    void warnOnWaehlerverzeichnisNummerIsNull() {
        new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", null);

        Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
    }
}
