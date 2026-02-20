package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahltermindaten;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AsyncProgressTest {

  AsyncProgress unitUnderTest = new AsyncProgress();

  @Nested
  class IncWahlvorschlaegeFinished {

    @Test
    void should_increaseNumberOfFinishedWahlvorschlaege_when_callingGet() {
      val numberOfFinisheBeforeCall = unitUnderTest.getWahlvorschlageFinished();
      unitUnderTest.incWahlvorschlaegeFinished();
      val numberOfFinishedAfterCall = unitUnderTest.getWahlvorschlageFinished();

      Assertions.assertThat(numberOfFinishedAfterCall).isEqualTo(numberOfFinisheBeforeCall + 1);
    }
  }

  @Nested
  class IncReferendumVorlagenFinished {

    @Test
    void should_increaseNumberOfFinishedReferendumvorlagen_when_callingGet() {
      val numberOfFinisheBeforeCall = unitUnderTest.getReferendumVorlagenFinished();
      unitUnderTest.incReferendumVorlagenFinished();
      val numberOfFinishedAfterCall = unitUnderTest.getReferendumVorlagenFinished();

      Assertions.assertThat(numberOfFinishedAfterCall).isEqualTo(numberOfFinisheBeforeCall + 1);
    }
  }

  @Nested
  class Reset {}
}
