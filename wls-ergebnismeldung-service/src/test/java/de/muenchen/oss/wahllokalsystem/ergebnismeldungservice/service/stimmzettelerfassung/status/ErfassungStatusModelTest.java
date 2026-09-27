package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ErfassungStatusModelTest {

  @ParameterizedTest
  @MethodSource("erfassungStatusWithExpectedCompletionStates")
  void should_returnExpectedCompletionStates_when_checkingErfassungStatus(
      final ErfassungStatusModel erfassungStatus,
      final boolean expectedStimmzettelerfassungAbgeschlossen,
      final boolean expectedBeschlussfassungAbgeschlossen) {
    assertThat(erfassungStatus.isStimmzettelerfassungAbgeschlossen())
        .isEqualTo(expectedStimmzettelerfassungAbgeschlossen);
    assertThat(erfassungStatus.isBeschlussfassungAbgeschlossen())
        .isEqualTo(expectedBeschlussfassungAbgeschlossen);
  }

  static Stream<Arguments> erfassungStatusWithExpectedCompletionStates() {
    return Stream.of(
        Arguments.of(ErfassungStatusModel.STE_BEARBEITUNG, false, false),
        Arguments.of(ErfassungStatusModel.STE_ABGESCHLOSSEN, true, false),
        Arguments.of(ErfassungStatusModel.BE_ABGESCHLOSSEN, true, true));
  }
}
