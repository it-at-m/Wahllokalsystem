package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status;

import static org.instancio.Select.field;

import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StimmzettelerfassungStatusTest {

  @Nested
  class IsStimmzettelerfassungAbgeschlossen {

    public static Stream<Arguments> createTestCases() {
      return Stream.of(
          Arguments.of(ErfassungStatus.STE_BEARBEITUNG, false),
          Arguments.of(ErfassungStatus.STE_ABGESCHLOSSEN, true),
          Arguments.of(ErfassungStatus.BE_ABGESCHLOSSEN, true));
    }

    @ParameterizedTest(name = "status={0}; expectedResult={1} ")
    @MethodSource("createTestCases")
    void should_returnBoolean_when_statusIsGiven(
        ErfassungStatus statusArgument, boolean expectedResult) {
      val unitUnderTest =
          Instancio.of(StimmzettelerfassungStatus.class)
              .set(field(StimmzettelerfassungStatus::getStatus), statusArgument)
              .create();

      Assertions.assertThat(unitUnderTest.isStimmzettelerfassungAbgeschlossen())
          .isEqualTo(expectedResult);
    }

    @Test
    void should_fail_when_notAllValuesOfEnumAreCoveredByTestCaseArguments() {
      val erfassungStatusEnumValueCoveredByTestcases =
          createTestCases()
              .map(argument -> (ErfassungStatus) (argument.get()[0]))
              .distinct()
              .toArray(ErfassungStatus[]::new);
      Assertions.assertThat(ErfassungStatus.values())
          .containsExactlyInAnyOrder(erfassungStatusEnumValueCoveredByTestcases);
    }
  }
}
