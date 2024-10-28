package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AsyncProgressTest {

    AsyncProgress unitUnderTest = new AsyncProgress();

    @Nested
    class IncWahlvorschlaegeFinished {

        @Test
        void should_increaseNumberOfFinishedWahlvorschlaege_when_called() {
            val numberOfFinisheBeforeCall = unitUnderTest.getWahlvorschlageFinished();
            unitUnderTest.incWahlvorschlaegeFinished();
            val numberOfFinishedAfterCall = unitUnderTest.getWahlvorschlageFinished();

            Assertions.assertThat(numberOfFinishedAfterCall).isEqualTo(numberOfFinisheBeforeCall + 1);
        }

        @Test
        void should_keepWahlvorschlaegeActiveTrue_when_finishedIsBelowTotal() {
            unitUnderTest.setWahlvorschlaegeLoadingActive(true);
            unitUnderTest.setWahlvorschlaegeTotal(10);

            unitUnderTest.incWahlvorschlaegeFinished();

            Assertions.assertThat(unitUnderTest.isWahlvorschlaegeLoadingActive()).isTrue();
        }

        @Test
        void should_setWahlvorschlaegeFinishedToFalse_when_totalIsReached() {
            unitUnderTest.setWahlvorschlaegeLoadingActive(true);
            unitUnderTest.setWahlvorschlaegeTotal(1);

            unitUnderTest.incWahlvorschlaegeFinished();

            Assertions.assertThat(unitUnderTest.isWahlvorschlaegeLoadingActive()).isFalse();
        }

        @Test
        void should_setLastFinishTime_when_wahlvorschlaegeAndReferendumvorlagenAreDone() {
            unitUnderTest.setWahlvorschlaegeLoadingActive(true);
            unitUnderTest.setReferendumLoadingActive(false);
            unitUnderTest.setWahlvorschlaegeTotal(1);
            unitUnderTest.setLastFinishTime(null);

            unitUnderTest.incWahlvorschlaegeFinished();

            Assertions.assertThat(unitUnderTest.getLastFinishTime()).isNotNull();
        }
    }

    @Nested
    class IncReferendumVorlagenFinished {

        @Test
        void should_increaseNumberOfFinishedReferendumvorlagen_when_called() {
            val numberOfFinisheBeforeCall = unitUnderTest.getReferendumVorlagenFinished();
            unitUnderTest.incReferendumVorlagenFinished();
            val numberOfFinishedAfterCall = unitUnderTest.getReferendumVorlagenFinished();

            Assertions.assertThat(numberOfFinishedAfterCall).isEqualTo(numberOfFinisheBeforeCall + 1);
        }

        @Test
        void should_keepReferndumvorlagenFinishedActiveTrue_when_finishedIsBelowTotal() {
            unitUnderTest.setReferendumLoadingActive(true);
            unitUnderTest.setReferendumVorlagenTotal(10);

            unitUnderTest.incReferendumVorlagenFinished();

            Assertions.assertThat(unitUnderTest.isReferendumLoadingActive()).isTrue();
        }

        @Test
        void should_setReferndumvorlagenFinishedToFalse_when_totalIsReached() {
            unitUnderTest.setReferendumLoadingActive(true);
            unitUnderTest.setReferendumVorlagenTotal(1);

            unitUnderTest.incReferendumVorlagenFinished();

            Assertions.assertThat(unitUnderTest.isReferendumLoadingActive()).isFalse();
        }

        @Test
        void should_setLastFinishTime_when_wahlvorschlaegeAndReferendumvorlagenAreDone() {
            unitUnderTest.setWahlvorschlaegeLoadingActive(false);
            unitUnderTest.setReferendumLoadingActive(true);
            unitUnderTest.setWahlvorschlaegeTotal(1);
            unitUnderTest.setLastFinishTime(null);

            unitUnderTest.incReferendumVorlagenFinished();

            Assertions.assertThat(unitUnderTest.getLastFinishTime()).isNotNull();
        }
    }

    @Nested
    class Reset {

    }

}
