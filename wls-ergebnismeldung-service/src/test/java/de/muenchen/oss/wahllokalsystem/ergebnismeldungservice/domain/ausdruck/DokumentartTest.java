package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class DokumentartTest {

  @Test
  void should_containAllMeldungsartValues_when_dokumentartDefined() {
    for (final Meldungsart meldungsart : Meldungsart.values()) {
      assertDoesNotThrow(
          () -> Dokumentart.valueOf(meldungsart.name()),
          () -> "Dokumentart is missing value '" + meldungsart.name() + "' from Meldungsart");
    }
  }
}
