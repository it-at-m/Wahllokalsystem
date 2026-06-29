package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {MicroServiceApplication.class})
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class BedenklicheStimmzettelRepositoryTest {

  @Autowired BedenklicheStimmzettelRepository repository;

  @AfterEach
  void teardown() {
    repository.deleteAll();
  }

  @Nested
  class CountInvalidBedenklicheStimmzettelForWahlbezirkIDAndWahlID {

    @Test
    void should_returnAnzahl_when_ungueltigeBedenklicheStimmzettelExistsForWahlbezirkIDAndWahlID() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlbezirkID2 = "wahlbezirkID2";
      val wahlID = "wahlID";
      val bedenklicherStimmzettel1 = new BedenklicherStimmzettel();
      bedenklicherStimmzettel1.setCompositeId(
          new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 0));
      bedenklicherStimmzettel1.setValidity(Validity.INVALID);
      val bedenklicherStimmzettel2 = new BedenklicherStimmzettel();
      bedenklicherStimmzettel2.setCompositeId(
          new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 1));
      bedenklicherStimmzettel2.setValidity(Validity.VALID);
      val bedenklicherStimmzettel3 = new BedenklicherStimmzettel();
      bedenklicherStimmzettel3.setCompositeId(
          new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 2));
      bedenklicherStimmzettel3.setValidity(Validity.INVALID);
      val bedenklicherStimmzettel4 = new BedenklicherStimmzettel();
      bedenklicherStimmzettel4.setCompositeId(
          new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID2, 0));
      bedenklicherStimmzettel4.setValidity(Validity.INVALID);
      val bedenklicheStimmzettelErfassung1 =
          new BedenklicheStimmzettelErfassung(
              new BezirkUndWahlID(wahlID, wahlbezirkID),
              List.of(
                  bedenklicherStimmzettel1, bedenklicherStimmzettel2, bedenklicherStimmzettel3));
      val bedenklicheStimmzettelErfassung2 =
          new BedenklicheStimmzettelErfassung(
              new BezirkUndWahlID(wahlID, wahlbezirkID2), List.of(bedenklicherStimmzettel4));

      repository.saveAll(
          List.of(bedenklicheStimmzettelErfassung1, bedenklicheStimmzettelErfassung2));

      val result =
          repository.countInvalidBedenklicheStimmzettelForWahlbezirkIDAndWahlID(
              wahlbezirkID, wahlID);
      Assertions.assertThat(result).isEqualTo(2L);
    }

    @Test
    void
        should_returnZero_when_bedenklicheStimmzettelExistsForWahlbezirkIDAndWahlIDButNotUngueltig() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val bedenklicherStimmzettel = new BedenklicherStimmzettel();
      bedenklicherStimmzettel.setCompositeId(new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 0));
      bedenklicherStimmzettel.setValidity(Validity.VALID);
      val bedenklicheStimmzettelErfassung =
          new BedenklicheStimmzettelErfassung(
              new BezirkUndWahlID(wahlID, wahlbezirkID), List.of(bedenklicherStimmzettel));

      repository.save(bedenklicheStimmzettelErfassung);

      val result =
          repository.countInvalidBedenklicheStimmzettelForWahlbezirkIDAndWahlID(
              wahlbezirkID, wahlID);
      Assertions.assertThat(result).isEqualTo(0L);
    }

    @Test
    void should_returnZero_when_noBedenklicheStimmzettelExists() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val bedenklicheStimmzettelErfassung =
          new BedenklicheStimmzettelErfassung(new BezirkUndWahlID(wahlID, wahlbezirkID), List.of());

      repository.save(bedenklicheStimmzettelErfassung);

      val result =
          repository.countInvalidBedenklicheStimmzettelForWahlbezirkIDAndWahlID(
              wahlbezirkID, wahlID);
      Assertions.assertThat(result).isEqualTo(0L);
    }

    @Test
    void should_returnZero_when_ungueltigeBedenklicheStimmzettelExistsButNotForWahlbezirkID() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val bedenklicherStimmzettel = new BedenklicherStimmzettel();
      bedenklicherStimmzettel.setCompositeId(new BezirkIdWahlIdOrderIndex(wahlID, wahlbezirkID, 0));
      bedenklicherStimmzettel.setValidity(Validity.INVALID);
      val bedenklicheStimmzettelErfassung =
          new BedenklicheStimmzettelErfassung(
              new BezirkUndWahlID(wahlID, wahlbezirkID), List.of(bedenklicherStimmzettel));

      repository.save(bedenklicheStimmzettelErfassung);

      val result =
          repository.countInvalidBedenklicheStimmzettelForWahlbezirkIDAndWahlID(
              wahlbezirkID + "sth", wahlID);
      Assertions.assertThat(result).isEqualTo(0L);
    }
  }
}
