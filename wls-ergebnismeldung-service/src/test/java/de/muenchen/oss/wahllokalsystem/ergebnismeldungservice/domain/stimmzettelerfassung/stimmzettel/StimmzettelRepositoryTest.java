package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyKandidatModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyValidStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createNonMatchingStimmzettelIDModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createStimmzettelWithOnlyOneWahlvorschlagSelectedModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithMultipleStreichungenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithOnlyReststimmenKandidatenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithSingleStreichungenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithStreichungAndEinzelStimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithStreichungAndReststimmeModel;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
  @Autowired private StimmzettelRepository stimmzettelRepository;

  private AtomicInteger stimmzettelkennungSequenz = new AtomicInteger(1);
  private AtomicInteger wahlvorschlagIDCounter = new AtomicInteger(1);

  @BeforeEach
  public void setup() {
    stimmzettelkennungSequenz = new AtomicInteger(1);
    wahlvorschlagIDCounter = new AtomicInteger(1);
  }

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

  private Stimmzettel createStimmzettelEntity(
      final String wahlID,
      final String wahlbezirkID,
      final String teamID,
      final int stimmzettelKennung) {
    return Instancio.of(
            InstancioModels.createDSESTimmzettelModel(
                wahlID, wahlbezirkID, teamID, stimmzettelKennung))
        .create();
  }

  @Nested
  class GetWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen {

    @Test
    void should_findTheMatchingStimmzettel_when_called() {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamA = "A";
      val teamB = "B";

      // TODO Struktur anpassen
      // 2 Testsuits: 1x verify summing; 1x verify matching
      // add non Matching: 2 Wahlvorschlaege, aber nur einer mit selected
      // check non Matching: 2 Wahlvorschläge, beide selected
      val wahlvorschlagID1 = UUID.randomUUID().toString();
      val findOnlyWahlvorschlag1IsSelected_A1 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamA, 1, wahlvorschlagID1));
      val findOnlyWahlvorschlag1IsSelected_A2 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamA, 2, wahlvorschlagID1));
      val findOnlyWahlvorschlag1IsSelected_A3 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamA, 3, wahlvorschlagID1));
      val findOnlyWahlvorschlag1IsSelected_B1 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamB, 1, wahlvorschlagID1));
      val findOnlyWahlvorschlag1IsSelected_B2 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamB, 2, wahlvorschlagID1));

      val wahlvorschlagID2 = UUID.randomUUID().toString();
      val findOnlyWahlvorschlag2IsSelected_A4 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamA, 4, wahlvorschlagID2));
      val findOnlyWahlvorschlag2IsSelected_A5 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamA, 5, wahlvorschlagID2));
      val findOnlyWahlvorschlag2IsSelected_B3 =
          Instancio.create(
              createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                  wahlID, wahlbezirkID, teamB, 3, wahlvorschlagID2));

      val nonMatching1 =
          Instancio.of(
                  createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                      wahlID, wahlbezirkID, teamA, 1, wahlvorschlagID1))
              .set(
                  field(Stimmzettel::getId),
                  Instancio.create(createNonMatchingStimmzettelIDModel(wahlID, wahlbezirkID, 1)))
              .create();
      val nonMatching2 =
          Instancio.of(
                  createStimmzettelWithOnlyOneWahlvorschlagSelectedModel(
                      wahlID, wahlbezirkID, teamA, 1, wahlvorschlagID2))
              .set(
                  field(Stimmzettel::getId),
                  Instancio.create(createNonMatchingStimmzettelIDModel(wahlID, wahlbezirkID, 1)))
              .create();

      transactionTemplate.executeWithoutResult(
          status -> {
            unitUnderTest.saveAll(
                List.of(
                    findOnlyWahlvorschlag1IsSelected_A1,
                    findOnlyWahlvorschlag1IsSelected_A2,
                    findOnlyWahlvorschlag1IsSelected_A3,
                    findOnlyWahlvorschlag1IsSelected_B1,
                    findOnlyWahlvorschlag1IsSelected_B2,
                    findOnlyWahlvorschlag2IsSelected_A4,
                    findOnlyWahlvorschlag2IsSelected_A5,
                    findOnlyWahlvorschlag2IsSelected_B3,
                    nonMatching1,
                    nonMatching2));
          });

      val result =
          unitUnderTest
              .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
                  wahlID, wahlbezirkID);

      val expectedResult =
          List.of(
              new WahlvorschlagStimmzettelAnzahl(wahlvorschlagID1, 5L),
              new WahlvorschlagStimmzettelAnzahl(wahlvorschlagID2, 3L));
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }
  }

  @Nested
  class GetWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen {
    private final String wahlID = Instancio.create(String.class);
    private final String wahlbezirkID = Instancio.create(String.class);
    private final String teamA = "A";
    private final String teamB = "B";

    @Nested
    class OnlyOneWahlvorschlagThatHasEinzelstimmen {

      @Test
      void should_countByWahlvorschlag_when_foundWahlvorschlaege() {
        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        // 1 Wahlvorschlag with 1 Kandidat and votes by voter
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv1"))
                            .set(field(Wahlvorschlag::isSelected), true) // does not matter
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 3)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv2"))
                            .set(field(Wahlvorschlag::isSelected), true) // does not matter
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 3)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv2"))
                            .set(field(Wahlvorschlag::isSelected), false) // does not matter
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 3)
                                        .create()))
                            .create()))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> {
              stimmzettelRepository.saveAll(stimmzettelToFind);
            });

        val result =
            unitUnderTest
                .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                    wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wv1", 3L),
                new WahlvorschlagStimmzettelAnzahl("wv2", 2L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {

        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        // 1 Wahlvorschlag with 1 Kandidat and votes by voter
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wvToFind"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create()))
                            .create()))
                .create());

        val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
        // wrong wahlID
        nonMatchingStimmzettel.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID + "sth",
                        wahlbezirkID,
                        teamA,
                        stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wvWrongWahl"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create()))
                            .create()))
                .create());
        // wrong wahlbezirkID
        nonMatchingStimmzettel.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID,
                        wahlbezirkID + "sth",
                        teamA,
                        stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wvWrongWahlbezirkID"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create()))
                            .create()))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> {
              stimmzettelRepository.saveAll(stimmzettelToFind);
              stimmzettelRepository.saveAll(nonMatchingStimmzettel);
            });

        val result =
            unitUnderTest
                .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                    wahlID, wahlbezirkID);

        val expectedResult = List.of(new WahlvorschlagStimmzettelAnzahl("wvToFind", 1L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Nested
    class OnlyOneWahlvorschlagThatHasReststimmen {

      @Test
      void should_countByWahlvorschlag_when_foundWahlvorschlaege() {
        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv1"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create(),
                                    Instancio.of(createEmptyKandidatModel("k2", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv2"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create(),
                                    Instancio.of(createEmptyKandidatModel("k2", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv2"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create(),
                                    Instancio.of(createEmptyKandidatModel("k2", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv2"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create(),
                                    Instancio.of(createEmptyKandidatModel("k2", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wv1"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create(),
                                    Instancio.of(createEmptyKandidatModel("k3", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> {
              stimmzettelRepository.saveAll(stimmzettelToFind);
            });

        val result =
            unitUnderTest
                .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                    wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wv1", 2L),
                new WahlvorschlagStimmzettelAnzahl("wv2", 3L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        // votes by wahlvorschlag + votes by voter
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wvReststimme+Einzelstimme"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::getVotesByVoter), 1)
                                        .create(),
                                    Instancio.of(createEmptyKandidatModel("k2", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());
        // votes by wahlvorschlag + discarded
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wvReststimme+Streichung"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k1", 1))
                                        .set(field(Kandidat::isDiscarded), true)
                                        .create(),
                                    Instancio.of(createEmptyKandidatModel("k2", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());

        val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
        // only votes by wahlvorschlag
        nonMatchingStimmzettel.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createEmptyWahlvorschlag("wvReststimme+Streichung"))
                            .set(field(Wahlvorschlag::isSelected), true)
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    Instancio.of(createEmptyKandidatModel("k2", 1))
                                        .set(field(Kandidat::getVotesByWahlvorschlag), 1)
                                        .create()))
                            .create()))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> {
              stimmzettelRepository.saveAll(stimmzettelToFind);
              stimmzettelRepository.saveAll(nonMatchingStimmzettel);
            });

        val result =
            unitUnderTest
                .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                    wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wvReststimme+Einzelstimme", 1L),
                new WahlvorschlagStimmzettelAnzahl("wvReststimme+Streichung", 1L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Nested
    class OnlyOneWahlvorschlagThatHasStreichungen {

      @Test
      void should_countByWahlvorschlag_when_foundWahlvorschlaege() {
        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel("wv1"))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> {
              stimmzettelRepository.saveAll(stimmzettelToFind);
            });

        val result =
            unitUnderTest
                .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                    wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wv1", 2L),
                new WahlvorschlagStimmzettelAnzahl("wv2", 3L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        val wvStreichungAndReststimmeModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithStreichungAndReststimmeModel)
                .toModel();

        val wvStreichungAndEinzelstimmeModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithStreichungAndEinzelStimmeModel)
                .toModel();
        // only streichung; normally wahlvorstand has to decide that these one is invalid
        val wvStreichungModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithSingleStreichungenModel)
                .toModel();

        // only multiple streichung; normally wahlvorstand has to decide that these one is invalid
        val wvMultipleStreichungModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithMultipleStreichungenModel)
                .toModel();
        val stimmzettelSingleWvOnlyReststimmenModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithOnlyReststimmenKandidatenModel)
                .toModel();

        val stimmzettelToFind = new LinkedList<Stimmzettel>();
        stimmzettelToFind.add(Instancio.create(wvStreichungAndReststimmeModel));
        stimmzettelToFind.add(Instancio.create(wvStreichungAndEinzelstimmeModel));
        stimmzettelToFind.add(Instancio.create(wvStreichungModel));
        stimmzettelToFind.add(Instancio.create(wvMultipleStreichungModel));

        val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
        nonMatchingStimmzettel.add(Instancio.create(stimmzettelSingleWvOnlyReststimmenModel));

        nonMatchingStimmzettel.add(
            createStimmzettelWithGueltigkeitInvalid(wvStreichungAndReststimmeModel));
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(wvStreichungAndReststimmeModel));

        nonMatchingStimmzettel.add(
            createStimmzettelWithGueltigkeitInvalid(wvStreichungAndEinzelstimmeModel));
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(wvStreichungAndEinzelstimmeModel));

        nonMatchingStimmzettel.add(createStimmzettelWithGueltigkeitInvalid(wvStreichungModel));
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(wvStreichungModel));

        nonMatchingStimmzettel.add(
            createStimmzettelWithGueltigkeitInvalid(wvMultipleStreichungModel));
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(wvMultipleStreichungModel));

        transactionTemplate.executeWithoutResult(
            status -> {
              stimmzettelRepository.saveAll(stimmzettelToFind);
              stimmzettelRepository.saveAll(nonMatchingStimmzettel);
            });

        val result =
            unitUnderTest
                .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                    wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wvStreichung+Reststimme", 1L),
                new WahlvorschlagStimmzettelAnzahl("wvStreichung+Einzelstimme", 1L),
                new WahlvorschlagStimmzettelAnzahl("wvStreichung", 1L),
                new WahlvorschlagStimmzettelAnzahl("wvMultipleStreichung", 1L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Test
    void should_returnEmptyList_when_noStimmzettelIsMatching() {
      val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();

      // 2 Wahlvorschlaege mit Daten
      // 1 leerer Wahlvorschlag
      // 1 Wahlvorschlag aber keine Kandidaten
      // 1 Wahlvorschlag, aber nur ungültige Stimmen
      // 1 Wahlvorschlag, aber nur gestrichen
      // ungültigert Stimmzettel mit 1 Wahlvorschlag mit einer Einzelstimme

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(nonMatchingStimmzettel);
          });

      val result =
          unitUnderTest
              .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                  wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEmpty();
    }

    private List<Stimmzettel> createStimmzettelWithNonMatchingStimmzettelIDs(
        Model<Stimmzettel> stimmzettelModel) {
      val wrongWahlbezirkID =
          Instancio.of(stimmzettelModel)
              .set(
                  field(Stimmzettel::getId),
                  new StimmzettelID(
                      wahlbezirkID + "sth",
                      wahlID,
                      teamA,
                      stimmzettelkennungSequenz.getAndIncrement()))
              .create();
      wrongWahlbezirkID
          .getWahlvorschlaege()
          .forEach(
              wahlvorschlag ->
                  wahlvorschlag.setWahlvorschlagID(
                      "wvInvalidWahlbezirkID" + wahlvorschlagIDCounter.getAndIncrement()));
      val wrongWahlID =
          Instancio.of(stimmzettelModel)
              .set(
                  field(Stimmzettel::getId),
                  new StimmzettelID(
                      wahlbezirkID,
                      wahlID + "sth",
                      teamA,
                      stimmzettelkennungSequenz.getAndIncrement()))
              .create();
      wrongWahlID
          .getWahlvorschlaege()
          .forEach(
              wahlvorschlag ->
                  wahlvorschlag.setWahlvorschlagID(
                      "wvInvalidWahlID" + wahlvorschlagIDCounter.getAndIncrement()));

      return List.of(wrongWahlbezirkID, wrongWahlID);
    }

    private Stimmzettel createStimmzettelWithGueltigkeitInvalid(
        Model<Stimmzettel> stimmzettelModel) {
      val stimmzettelInvalid =
          Instancio.of(stimmzettelModel)
              .set(
                  field(Stimmzettel::getId),
                  new StimmzettelID(
                      wahlbezirkID, wahlID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .set(field(Stimmzettel::getGueltigkeit), StimmzettelGueltigkeit.INVALID)
              .create();
      stimmzettelInvalid
          .getWahlvorschlaege()
          .forEach(
              wahlvorschlag ->
                  wahlvorschlag.setWahlvorschlagID(
                      "wvStimmzettelInvalid" + wahlvorschlagIDCounter.getAndIncrement()));

      return stimmzettelInvalid;
    }
  }
}
