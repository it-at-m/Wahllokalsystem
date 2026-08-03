package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
class StimmzettelRepositoryTest {

  @Autowired StimmzettelRepository unitUnderTest;

  @Autowired TransactionTemplate transactionTemplate;

  @AfterEach
  public void teardown() {
    unitUnderTest.deleteAll();
  }

  @Nested
  class FindByIdWahlbezirkIDAndIdWahlIDAndIdTeamID {

    @Test
    void should_returnEmptyList_when_noDataIsStored() {
      val wahlbezirkID = Instancio.create(String.class);
      val wahlID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      Assertions.assertThat(
              unitUnderTest.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                  wahlbezirkID, wahlID, teamID))
          .isEmpty();
    }

    @Test
    void should_returnEmptyList_when_noDataIsMatching() {
      val wahlbezirkID = Instancio.create(String.class);
      val wahlID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      transactionTemplate.executeWithoutResult(
          status -> {
            unitUnderTest.save(createStimmzettelEntity(wahlID + "sth", wahlbezirkID, teamID, 1));
            unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID + "sth", teamID, 1));
            unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID + "sth", 1));
          });

      Assertions.assertThat(
              unitUnderTest.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                  wahlbezirkID, wahlID, teamID))
          .isEmpty();
    }

    @Test
    void should_returnListWithEntities_when_matchingDataExists() {
      val wahlbezirkID = Instancio.create(String.class);
      val wahlID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val entitiesToFind =
          transactionTemplate.execute(
              status -> {
                unitUnderTest.save(
                    createStimmzettelEntity(wahlID + "sth", wahlbezirkID, teamID, 1));
                val entity1ToFind =
                    unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 1));
                val entity2ToFind =
                    unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 2));
                val entity3ToFind =
                    unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 3));
                unitUnderTest.save(
                    createStimmzettelEntity(wahlID, wahlbezirkID + "sth", teamID, 1));
                unitUnderTest.save(
                    createStimmzettelEntity(wahlID, wahlbezirkID, teamID + "sth", 1));

                return List.of(entity1ToFind, entity2ToFind, entity3ToFind);
              });

      val result =
          unitUnderTest.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(wahlbezirkID, wahlID, teamID);
      Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(entitiesToFind);
    }
  }

  @Nested
  class DeleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID {

    @Test
    void should_deleteOnlyMatchingItems_when_dataExists() {
      val wahlbezirkID = Instancio.create(String.class);
      val wahlID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val entitiesToFind =
          transactionTemplate.execute(
              status -> {
                val entity1ToFind =
                    unitUnderTest.save(
                        createStimmzettelEntity(wahlID + "sth", wahlbezirkID, teamID, 1));
                unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 1));
                unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 2));
                unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 3));
                val entity2ToFind =
                    unitUnderTest.save(
                        createStimmzettelEntity(wahlID, wahlbezirkID + "sth", teamID, 1));
                val entity3ToFind =
                    unitUnderTest.save(
                        createStimmzettelEntity(wahlID, wahlbezirkID, teamID + "sth", 1));

                return List.of(entity1ToFind, entity2ToFind, entity3ToFind);
              });

      transactionTemplate.executeWithoutResult(
          status ->
              unitUnderTest.deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                  wahlbezirkID, wahlID, teamID));

      val remainingItems = unitUnderTest.findAll();
      Assertions.assertThat(remainingItems).containsExactlyInAnyOrderElementsOf(entitiesToFind);
    }
  }

  @Nested
  class CountByIdWahlbezirkIDAndIdWahlID {

    @Test
    void should_returnZero_when_noDataExists() {
      val wahlbezirkID = Instancio.create(String.class);
      val wahlID = Instancio.create(String.class);

      val result = unitUnderTest.countByIdWahlbezirkIDAndIdWahlID(wahlbezirkID, wahlID);

      Assertions.assertThat(result).isEqualTo(0);
    }

    @Test
    void should_returnZero_when_noMatchingDataExists() {
      val wahlbezirkID = Instancio.create(String.class);
      val wahlID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      transactionTemplate.executeWithoutResult(
          status -> {
            unitUnderTest.save(createStimmzettelEntity(wahlID + "sth", wahlbezirkID, teamID, 1));
            unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID + "sth", teamID, 1));
          });

      val result = unitUnderTest.countByIdWahlbezirkIDAndIdWahlID(wahlbezirkID, wahlID);

      Assertions.assertThat(result).isEqualTo(0);
    }

    @Test
    void should_returnCount_when_matchingDataExists() {

      val wahlbezirkID = Instancio.create(String.class);
      val wahlID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      transactionTemplate.executeWithoutResult(
          status -> {
            unitUnderTest.save(createStimmzettelEntity(wahlID + "sth", wahlbezirkID, teamID, 1));
            unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 1)); // Match
            unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 2)); // Match
            unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID, teamID, 3)); // Match
            unitUnderTest.save(createStimmzettelEntity(wahlID, wahlbezirkID + "sth", teamID, 1));
            unitUnderTest.save(
                createStimmzettelEntity(wahlID, wahlbezirkID, teamID + "sth", 1)); // Match
          });

      val result = unitUnderTest.countByIdWahlbezirkIDAndIdWahlID(wahlbezirkID, wahlID);

      Assertions.assertThat(result).isEqualTo(4);
    }
  }

   private DSEStimmzettel createStimmzettelEntity(
      final String wahlID,
      final String wahlbezirkID,
       final String teamID,
       final int stimmzettelKennung) {
     return Instancio.of(DSEStimmzettel.class)
         .set(
             field(DSEStimmzettel::getId),
             new StimmzettelID(wahlbezirkID, wahlID, teamID, stimmzettelKennung))
         .create();
  }
}
