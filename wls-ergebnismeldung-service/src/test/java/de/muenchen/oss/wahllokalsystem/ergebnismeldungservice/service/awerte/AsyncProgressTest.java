package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AsyncProgressTest {

    AsyncProgress unitUnderTest = new AsyncProgress();

    @Nested
    class IncAWerteFinished {

        @Test
        void should_increaseNumberOfaWerteFinishedAndLoadingIsNotActive_when_biggerOrEqualThanAWerteTotal() {
            val numberOfFinishedBeforeCall = unitUnderTest.getAWerteFinished();
            unitUnderTest.reset(0);
            unitUnderTest.incAWerteFinished();
            val numberOfFinishedAfterCall = unitUnderTest.getAWerteFinished();

            Assertions.assertThat(numberOfFinishedAfterCall).isEqualTo(numberOfFinishedBeforeCall + 1);
            Assertions.assertThat(unitUnderTest.isAWerteLoadingActive()).isFalse();
        }

        @Test
        void should_increaseNumberOfaWerteFinishedAndIsLoadingActive_when_smallerThanAWerteTotal() {
            val numberOfFinishedBeforeCall = unitUnderTest.getAWerteFinished();
            unitUnderTest.reset(5);
            unitUnderTest.incAWerteFinished();
            val numberOfFinishedAfterCall = unitUnderTest.getAWerteFinished();

            Assertions.assertThat(numberOfFinishedAfterCall).isEqualTo(numberOfFinishedBeforeCall + 1);
            Assertions.assertThat(unitUnderTest.isAWerteLoadingActive()).isTrue();
        }
    }
}
