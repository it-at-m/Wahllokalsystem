package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankDiscardedKandidat;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByVoter;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankNonSelectedWahlvorschlagModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankSelectedWahlvorschlagModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankValidStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createDSESTimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createInvalidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createInvalidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createInvalidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createInvalidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWith2WahlvorschlaegenEachWithReststimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWithSingleWahlvorschlagWithOnlyReststimmen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWithSingleWahlvorschlagWithReststimmeAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWithSingleWahlvorschlagWithSingleStreichung;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestStimmzettelModels.createValidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.createSingleWahlvorschlagModelWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.createSingleWahlvorschlagModelWithReststimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.createSingleWahlvorschlagModelWithStreichungAndEinzelStimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithMultipleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithOnlyReststimmenKandidaten;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithReststimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithReststimmeAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithSingleInvalideVote;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithSingleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithStreichungAndEinzelStimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithStreichungAndReststimme;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
  private final String wahlID = Instancio.create(String.class);
  private final String wahlbezirkID = Instancio.create(String.class);
  private final String teamA = "A";
  private final String teamB = "B";

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
    void should_countByWahlvorschlag_when_foundWahlvorschlaege() {
      val stimmzettelToFind = new ArrayList<Stimmzettel>();
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagModelWithReststimme("wv1"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagModelWithReststimme("wv2"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagModelWithReststimme("wv1"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagModelWithReststimme("wv2"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagModelWithReststimme("wv1"))
              .create());

      transactionTemplate.executeWithoutResult(
          status -> stimmzettelRepository.saveAll(stimmzettelToFind));

      val result =
          unitUnderTest
              .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
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
      val stimmzettelWithSingleWahlvorschlagWithOnlyReststimmenModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagModelWithOnlyReststimmenKandidaten)
              .toModel();

      val stimmzettelWithSingleWahlvorschlagWithEinzelstimmeModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagModelWithEinzelstimme)
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithOnlyStreichungModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagModelWithSingleStreichungen)
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithReststimmeAndStreichungModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagModelWithStreichungAndReststimme)
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagModelWithReststimmeAndEinzelstimme)
              .toModel();
      val stimmzettelWith2SelectedWahlvorschlaegenModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () ->
                      List.of(
                          Instancio.of(createBlankSelectedWahlvorschlagModel("wv1"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createBlankKandidatWithSingleVoteByWahlvorschlag(
                                              "k11", 1)))
                              .create(),
                          Instancio.of(createBlankSelectedWahlvorschlagModel("wv2"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createBlankKandidatWithSingleVoteByWahlvorschlag(
                                              "k21", 1)))
                              .create()))
              .toModel();

      val stimmzettelToFind = new ArrayList<Stimmzettel>();
      stimmzettelToFind.add(
          Instancio.create(stimmzettelWithSingleWahlvorschlagWithOnlyReststimmenModel));

      val nonMatchingStimmzettel = new ArrayList<Stimmzettel>();
      nonMatchingStimmzettel.add(
          Instancio.create(stimmzettelWithSingleWahlvorschlagWithEinzelstimmeModel));
      nonMatchingStimmzettel.add(
          Instancio.create(stimmzettelWithSingleWahlvorschlagWithOnlyStreichungModel));
      nonMatchingStimmzettel.add(
          Instancio.create(stimmzettelWithSingleWahlvorschlagWithReststimmeAndStreichungModel));
      nonMatchingStimmzettel.add(
          Instancio.create(stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel));
      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWith2SelectedWahlvorschlaegenModel));
      nonMatchingStimmzettel.addAll(
          createNonValidVariants(stimmzettelWithSingleWahlvorschlagWithOnlyReststimmenModel));

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(stimmzettelToFind);
            stimmzettelRepository.saveAll(nonMatchingStimmzettel);
          });

      val result =
          unitUnderTest
              .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
                  wahlID, wahlbezirkID);

      val expectedResult = List.of(new WahlvorschlagStimmzettelAnzahl("onlyReststimmen", 1L));
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    @Test
    void should_returnEmptyList_when_noDataWasFound() {
      val result =
          unitUnderTest
              .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
                  wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEmpty();
    }
  }

  @Nested
  class GetWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen {

    @Nested
    class OnlyOneWahlvorschlagThatHasEinzelstimmen {

      @Test
      void should_countByWahlvorschlag_when_foundWahlvorschlaege() {
        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithEinzelstimme("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithEinzelstimme("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithEinzelstimme("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithEinzelstimme("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithEinzelstimme("wv2"))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> stimmzettelRepository.saveAll(stimmzettelToFind));

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
        val stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithEinzelstimme)
                .toModel();

        val stimmzettelToFind = new LinkedList<Stimmzettel>();
        stimmzettelToFind.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel));

        val nonMatchingStimmzettel =
            new LinkedList<>(
                createNonValidVariants(stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel));
        nonMatchingStimmzettel.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithSingleInvalideVote)
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

        val expectedResult = List.of(new WahlvorschlagStimmzettelAnzahl("wvEinzelstimme", 1L));
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
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithReststimmeAndEinzelstimme("wv1"))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> stimmzettelRepository.saveAll(stimmzettelToFind));

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

        val stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithReststimmeAndEinzelstimme)
                .toModel();
        val stimmzettelWithSingleWahlvorschlagWithStreichungAndEinzelstimmeModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithStreichungAndReststimme)
                .toModel();
        val stimmzettelWithSingleWahlvorschlagWithReststimmeModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagModelWithReststimme)
                .toModel();

        stimmzettelToFind.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel));
        stimmzettelToFind.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagWithStreichungAndEinzelstimmeModel));

        val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
        nonMatchingStimmzettel.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagWithReststimmeModel));
        nonMatchingStimmzettel.addAll(
            createNonValidVariants(
                stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel));
        nonMatchingStimmzettel.addAll(
            createNonValidVariants(
                stimmzettelWithSingleWahlvorschlagWithStreichungAndEinzelstimmeModel));

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
                new WahlvorschlagStimmzettelAnzahl("wvStreichung+Reststimme", 1L));
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
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithStreichungAndEinzelStimme("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithStreichungAndEinzelStimme("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithStreichungAndEinzelStimme("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithStreichungAndEinzelStimme("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagModelWithStreichungAndEinzelStimme("wv1"))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> stimmzettelRepository.saveAll(stimmzettelToFind));

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
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithStreichungAndReststimme)
                .toModel();

        val wvStreichungAndEinzelstimmeModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithStreichungAndEinzelStimme)
                .toModel();
        // only streichung; normally wahlvorstand has to decide that these one is invalid
        val wvStreichungModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithSingleStreichungen)
                .toModel();

        // only multiple streichung; normally wahlvorstand has to decide that these one is invalid
        val wvMultipleStreichungModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithMultipleStreichungen)
                .toModel();
        val stimmzettelSingleWvOnlyReststimmenModel =
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagModelWithOnlyReststimmenKandidaten)
                .toModel();

        val stimmzettelToFind = new LinkedList<Stimmzettel>();
        stimmzettelToFind.add(Instancio.create(wvStreichungAndReststimmeModel));
        stimmzettelToFind.add(Instancio.create(wvStreichungAndEinzelstimmeModel));
        stimmzettelToFind.add(Instancio.create(wvStreichungModel));
        stimmzettelToFind.add(Instancio.create(wvMultipleStreichungModel));

        val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
        nonMatchingStimmzettel.add(Instancio.create(stimmzettelSingleWvOnlyReststimmenModel));
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(wvStreichungAndReststimmeModel));
        nonMatchingStimmzettel.addAll(createNonValidVariants(wvStreichungAndEinzelstimmeModel));
        nonMatchingStimmzettel.addAll(createNonValidVariants(wvStreichungModel));
        nonMatchingStimmzettel.addAll(createNonValidVariants(wvMultipleStreichungModel));

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
      val stimmzettelWith2WahlvorschlaegenModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () ->
                      List.of(
                          Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () -> List.of(createBlankKandidatWithSingleVoteByVoter("k11", 1)))
                              .create(),
                          Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () -> List.of(createBlankKandidatWithSingleVoteByVoter("k21", 1)))
                              .create()))
              .toModel();
      val stimmzettelWithoutAnyWahlvorschlaegeModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithoutAnyKandidatenModel =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () -> List.of(Instancio.create(createBlankNonSelectedWahlvorschlagModel("wv1"))))
              .toModel();

      val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWith2WahlvorschlaegenModel));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWith2WahlvorschlaegenModel));

      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithoutAnyWahlvorschlaegeModel));
      nonMatchingStimmzettel.addAll(
          createNonValidVariants(stimmzettelWithoutAnyWahlvorschlaegeModel));

      nonMatchingStimmzettel.add(
          Instancio.create(stimmzettelWithSingleWahlvorschlagWithoutAnyKandidatenModel));
      nonMatchingStimmzettel.addAll(
          createNonValidVariants(stimmzettelWithSingleWahlvorschlagWithoutAnyKandidatenModel));

      transactionTemplate.executeWithoutResult(
          status -> stimmzettelRepository.saveAll(nonMatchingStimmzettel));

      val result =
          unitUnderTest
              .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                  wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEmpty();
    }
  }

  @Nested
  class CountInvalidStimmzettel {

    @Test
    void should_countStimmzettel_when_foundMatchingStimmzettel() {

      val stimmzettelToCount = new LinkedList<Stimmzettel>();

      val invalidStimmzettelModelWithSingleEinzelstimme =
          Instancio.of(
                  createInvalidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .toModel();

      stimmzettelToCount.add(Instancio.create(invalidStimmzettelModelWithSingleEinzelstimme));
      stimmzettelToCount.add(
          Instancio.create(
              createInvalidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen(
                  wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement())));
      stimmzettelToCount.add(
          Instancio.create(
              createInvalidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme(
                  wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement())));
      stimmzettelToCount.add(
          Instancio.create(
              createInvalidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme(
                  wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement())));

      val nonMatchingStimmzettel =
          new LinkedList<>(
              createStimmzettelWithOtherGueltigkeiten(
                  invalidStimmzettelModelWithSingleEinzelstimme, StimmzettelGueltigkeit.INVALID));

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(stimmzettelToCount);
            stimmzettelRepository.saveAll(nonMatchingStimmzettel);
          });

      val result = unitUnderTest.countInvalidStimmzettel(wahlID, wahlbezirkID);

      Assertions.assertThat(result).isEqualTo(stimmzettelToCount.size());
    }

    @Test
    void should_return0_when_noMatchingStimmzettelAreGiven() {
      val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();

      Arrays.stream(StimmzettelGueltigkeit.values())
          .forEach(
              gueltigkeit -> {
                if (!StimmzettelGueltigkeit.INVALID.equals(gueltigkeit)) {
                  nonMatchingStimmzettel.add(
                      Instancio.of(
                              createDSESTimmzettelModel(
                                  wahlID,
                                  wahlbezirkID,
                                  teamA,
                                  stimmzettelkennungSequenz.getAndIncrement()))
                          .set(field(Stimmzettel::getGueltigkeit), gueltigkeit)
                          .create());
                }
              });

      nonMatchingStimmzettel.add(
          Instancio.create(
              createValidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
                  wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement())));
      nonMatchingStimmzettel.add(
          Instancio.create(
              createValidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen(
                  wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement())));
      nonMatchingStimmzettel.add(
          Instancio.create(
              createValidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme(
                  wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement())));

      transactionTemplate.executeWithoutResult(
          status -> stimmzettelRepository.saveAll(nonMatchingStimmzettel));

      val result = unitUnderTest.countInvalidStimmzettel(wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEqualTo(0L);
    }
  }

  @Nested
  class GetSumValidKandidatenVotesPerWahlvorschlagWhenNotOnlyOneListenkreuzReststimmeAreGiven {

    @Test
    void should_countByKandidaten_when_foundWahlvorschlaege() {
      val stimmzettelToCount = new LinkedList<Stimmzettel>();

      val stimmzettelModel1WithSameKandidatWith2Nennungen =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () ->
                      List.of(
                          Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createBlankKandidatWithSingleVoteByVoter("k11", 1),
                                          createBlankKandidatWithSingleVoteByVoter("k11", 2),
                                          createBlankKandidatWithSingleVoteByVoter("k11", 3),
                                          createBlankKandidatWithSingleVoteByWahlvorschlag(
                                              "k12", 1),
                                          createBlankKandidatWithSingleVoteByWahlvorschlag(
                                              "k12", 2),
                                          createBlankDiscardedKandidat("k12", 3)))
                              .create(),
                          Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createBlankKandidatWithSingleVoteByVoter("k21", 1),
                                          createBlankKandidatWithSingleVoteByVoter("k21", 2),
                                          createBlankKandidatWithSingleVoteByWahlvorschlag(
                                              "k22", 1),
                                          createBlankDiscardedKandidat("k22", 2)))
                              .create()))
              .toModel();
      val stimmzettelModel2WithSameKandidatWith2Nennungen =
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () ->
                      List.of(
                          Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createBlankKandidatWithSingleVoteByVoter("k11", 1),
                                          createBlankKandidatWithSingleVoteByVoter("k11", 2),
                                          createBlankKandidatWithSingleVoteByVoter("k11", 3),
                                          createBlankKandidatWithSingleVoteByVoter("k12", 1),
                                          createBlankKandidatWithSingleVoteByWahlvorschlag(
                                              "k12", 2)))
                              .create(),
                          Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createBlankKandidatWithSingleVoteByVoter("k21", 1),
                                          createBlankKandidatWithSingleVoteByVoter("k21", 2)))
                              .create()))
              .toModel();
      stimmzettelToCount.add(Instancio.create(stimmzettelModel1WithSameKandidatWith2Nennungen));
      stimmzettelToCount.add(Instancio.create(stimmzettelModel2WithSameKandidatWith2Nennungen));

      transactionTemplate.executeWithoutResult(
          status -> stimmzettelRepository.saveAll(stimmzettelToCount));

      val result =
          unitUnderTest
              .getSumValidKandidatenVotesPerWahlvorschlagWhenNotOnlyOneListenkreuzReststimmeAreGiven(
                  wahlID, wahlbezirkID);

      val expectedResult =
          List.of(
              new KandidatStimmenAnzahl("wv1", "k11", 6),
              new KandidatStimmenAnzahl("wv1", "k12", 4),
              new KandidatStimmenAnzahl("wv2", "k21", 4),
              new KandidatStimmenAnzahl("wv2", "k22", 1));
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    @Test
    void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {

      val stimmzettelWithExactlyOneListenkreuzModel =
          createValidStimmzettelModelWithSingleWahlvorschlagWithOnlyReststimmen(
              wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement());
      val stimmzettelWithEinzelstimmeModel =
          createValidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
              wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement());
      val stimmzettelWithStreichungModel =
          createValidStimmzettelModelWithSingleWahlvorschlagWithSingleStreichung(
              wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement());
      val stimmzettelWithMultipleStreichungenModel =
          createValidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen(
              wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement());
      val stimmzettelWithReststimmeAndEinzelstimmeModel =
          createValidStimmzettelModelWithSingleWahlvorschlagWithReststimmeAndEinzelstimme(
              wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement());
      val stimmzettelWithTwoListenkreuzen =
          createValidStimmzettelModelWith2WahlvorschlaegenEachWithReststimme(
              wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement());
      val stimmzettelWithTwoChangedWahlvorschlaegen =
          createValidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme(
              wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement());

      val stimmzettelToFind = new LinkedList<Stimmzettel>();
      stimmzettelToFind.add(Instancio.create(stimmzettelWithEinzelstimmeModel));
      stimmzettelToFind.add(Instancio.create(stimmzettelWithStreichungModel));
      stimmzettelToFind.add(Instancio.create(stimmzettelWithMultipleStreichungenModel));
      stimmzettelToFind.add(Instancio.create(stimmzettelWithReststimmeAndEinzelstimmeModel));
      stimmzettelToFind.add(Instancio.create(stimmzettelWithTwoListenkreuzen));
      stimmzettelToFind.add(Instancio.create(stimmzettelWithTwoChangedWahlvorschlaegen));
      // Changes in Multiple Wahlvorschlaegen - valid

      val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithExactlyOneListenkreuzModel));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithEinzelstimmeModel));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithStreichungModel));
      nonMatchingStimmzettel.addAll(
          createNonValidVariants(stimmzettelWithMultipleStreichungenModel));
      nonMatchingStimmzettel.addAll(
          createNonValidVariants(stimmzettelWithReststimmeAndEinzelstimmeModel));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithTwoListenkreuzen));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithTwoListenkreuzen));

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(stimmzettelToFind);
            stimmzettelRepository.saveAll(nonMatchingStimmzettel);
          });

      val result =
          unitUnderTest
              .getSumValidKandidatenVotesPerWahlvorschlagWhenNotOnlyOneListenkreuzReststimmeAreGiven(
                  wahlID, wahlbezirkID);

      val expectedResult = getExpectedKandidatenStimmenAnzahl(stimmzettelToFind);
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    @Test
    void should_returnEmptyList_when_noDataWasFound() {
      val result =
          unitUnderTest
              .getSumValidKandidatenVotesPerWahlvorschlagWhenNotOnlyOneListenkreuzReststimmeAreGiven(
                  wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEmpty();
    }

    private List<KandidatStimmenAnzahl> getExpectedKandidatenStimmenAnzahl(
        Collection<Stimmzettel> stimmzettelForCounting) {
      val wahlvorschlaegeGroupedByWahlvorschlagID =
          stimmzettelForCounting.stream()
              .map(Stimmzettel::getWahlvorschlaege)
              .flatMap(Collection::stream)
              .collect(Collectors.groupingBy(Wahlvorschlag::getWahlvorschlagID));

      final Map<String, Map<String, Long>> wahlvorschlaegeWithKandidatenVoteCount = new HashMap<>();
      wahlvorschlaegeGroupedByWahlvorschlagID.forEach(
          (String wahlvorschlagID, List<Wahlvorschlag> wahlvorschlaege) -> {
            val kandidatenVotesOfWahlvorschlag =
                wahlvorschlaegeWithKandidatenVoteCount.computeIfAbsent(
                    wahlvorschlagID, (String key) -> new HashMap<>());
            wahlvorschlaege.stream()
                .flatMap(wahlvorschlag -> wahlvorschlag.getKandidaten().stream())
                .forEach(
                    kandidat -> {
                      val kandidatenVotes =
                          kandidatenVotesOfWahlvorschlag.getOrDefault(
                              kandidat.getKandidatID().getKandidatID(), 0L);
                      kandidatenVotesOfWahlvorschlag.put(
                          kandidat.getKandidatID().getKandidatID(),
                          kandidatenVotes
                              + Optional.ofNullable(kandidat.getVotesByWahlvorschlag()).orElse(0)
                              + Optional.ofNullable(kandidat.getVotesByVoter()).orElse(0));
                    });
          });

      return wahlvorschlaegeWithKandidatenVoteCount.entrySet().stream()
          .map(
              entry -> {
                val wahlvorschlagID = entry.getKey();
                return entry.getValue().entrySet().stream()
                    .map(
                        kandidatVotes ->
                            new KandidatStimmenAnzahl(
                                wahlvorschlagID, kandidatVotes.getKey(), kandidatVotes.getValue()))
                    .toList();
              })
          .flatMap(Collection::stream)
          .toList();
    }
  }

  private List<Stimmzettel> createNonValidVariants(Model<Stimmzettel> model) {
    val result = new LinkedList<Stimmzettel>();

    result.addAll(createStimmzettelWithNonMatchingStimmzettelIDs(model));
    result.addAll(createStimmzettelWithOtherGueltigkeiten(model, StimmzettelGueltigkeit.VALID));

    return result;
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

  private List<Stimmzettel> createStimmzettelWithOtherGueltigkeiten(
      Model<Stimmzettel> stimmzettelModel, StimmzettelGueltigkeit gueltigkeitToExclude) {
    return Arrays.stream(StimmzettelGueltigkeit.values())
        .filter(gueltigkeit -> !gueltigkeitToExclude.equals(gueltigkeit))
        .map(
            gueltigkeit -> {
              val stimmzettelInvalid =
                  Instancio.of(stimmzettelModel)
                      .set(
                          field(Stimmzettel::getId),
                          new StimmzettelID(
                              wahlbezirkID,
                              wahlID,
                              teamA,
                              stimmzettelkennungSequenz.getAndIncrement()))
                      .set(field(Stimmzettel::getGueltigkeit), gueltigkeit)
                      .create();
              stimmzettelInvalid
                  .getWahlvorschlaege()
                  .forEach(
                      wahlvorschlag ->
                          wahlvorschlag.setWahlvorschlagID(
                              "wvStimmzettel"
                                  + gueltigkeit.name()
                                  + wahlvorschlagIDCounter.getAndIncrement()));

              return stimmzettelInvalid;
            })
        .toList();
  }
}
