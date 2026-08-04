package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DSEWahlvorschlagTest {

  @Nested
  class AddKandidat {

    @Test
    void should_addToListAndSetThisAsStimmzettel_when_parameterIsGiven() {
      val wahlvorschlag = Instancio.create(DSEWahlvorschlag.class);
      val kandidatToAdd = Instancio.create(DSEKandidat.class);

      Assertions.assertThat(kandidatToAdd).isNotIn(wahlvorschlag.getKandidaten());
      Assertions.assertThat(kandidatToAdd.getWahlvorschlag()).isNotEqualTo(wahlvorschlag);

      wahlvorschlag.addKandidat(kandidatToAdd);

      Assertions.assertThat(kandidatToAdd).isIn(wahlvorschlag.getKandidaten());
      Assertions.assertThat(kandidatToAdd.getWahlvorschlag()).isEqualTo(wahlvorschlag);
    }
  }
}
