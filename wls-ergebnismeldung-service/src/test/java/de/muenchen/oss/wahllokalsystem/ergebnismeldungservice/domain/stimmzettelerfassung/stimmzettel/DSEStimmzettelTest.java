package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DSEStimmzettelTest {

  @Nested
  class AddBeschlussvormerkung {

    @Test
    void should_addToListAndSetThisAsStimmzettel_when_parameterIsGiven() {
      val stimmzettel = Instancio.create(DSEStimmzettel.class);
      val beschlussvormerkungToAdd = Instancio.create(DSEBeschlussvormerkung.class);

      Assertions.assertThat(beschlussvormerkungToAdd)
          .isNotIn(stimmzettel.getBeschlussvormerkungen());
      Assertions.assertThat(beschlussvormerkungToAdd.getStimmzettel()).isNotEqualTo(stimmzettel);

      stimmzettel.addBeschlussvormerkung(beschlussvormerkungToAdd);

      Assertions.assertThat(beschlussvormerkungToAdd).isIn(stimmzettel.getBeschlussvormerkungen());
      Assertions.assertThat(beschlussvormerkungToAdd.getStimmzettel()).isEqualTo(stimmzettel);
    }
  }

  @Nested
  class AddWahlvorschlag {

    @Test
    void should_addToListAndSetThisAsStimmzettel_when_parameterIsGiven() {
      val stimmzettel = Instancio.create(DSEStimmzettel.class);
      val wahlvorschlagToAdd = Instancio.create(DSEWahlvorschlag.class);

      Assertions.assertThat(wahlvorschlagToAdd).isNotIn(stimmzettel.getWahlvorschlaege());
      Assertions.assertThat(wahlvorschlagToAdd.getStimmzettel()).isNotEqualTo(stimmzettel);

      stimmzettel.addWahlvorschlag(wahlvorschlagToAdd);

      Assertions.assertThat(wahlvorschlagToAdd).isIn(stimmzettel.getWahlvorschlaege());
      Assertions.assertThat(wahlvorschlagToAdd.getStimmzettel()).isEqualTo(stimmzettel);
    }
  }
}
