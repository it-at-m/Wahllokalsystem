package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createDSESTimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyDiscardedKandidat;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyKandidatWithSingleVoteByVoter;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyKandidatWithSingleVoteByWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptySelectedWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyValidStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createEmptyWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createInvalidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createInvalidStimmzettelModelWith2WahlvorschlaegenEachWithReststimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createInvalidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createInvalidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createInvalidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWith2WahlvorschlaegenEachWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWith2WahlvorschlaegenEachWithReststimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWithSingleWahlvorschlagWithMultipleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWithSingleWahlvorschlagWithOnlyReststimmen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWithSingleWahlvorschlagWithReststimmeAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWithSingleWahlvorschlagWithSingleStreichung;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryStimmzettelTestModels.createValidStimmzettelModelWithSingleWahlvorschlagWithStreichungAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.createSingleWahlvorschlagWithEinzelstimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.createSingleWahlvorschlagWithReststimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.createSingleWahlvorschlagWithStreichungAndEinzelStimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithEinzelstimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithMultipleStreichungenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithOnlyReststimmenKandidatenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithReststimmeAndEinzelstimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithReststimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithSingleStreichungenModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithStreichungAndEinzelStimmeModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestModels.singleWahlvorschlagWithStreichungAndReststimmeModel;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagWithReststimmeModel("wv1"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagWithReststimmeModel("wv2"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagWithReststimmeModel("wv1"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagWithReststimmeModel("wv2"))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  createSingleWahlvorschlagWithReststimmeModel("wv1"))
              .create());

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(stimmzettelToFind);
          });

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
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagWithOnlyReststimmenKandidatenModel)
              .toModel();

      val stimmzettelWithSingleWahlvorschlagWithEinzelstimmeModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagWithEinzelstimmeModel)
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithOnlyStreichungModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagWithSingleStreichungenModel)
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithReststimmeAndStreichungModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagWithStreichungAndReststimmeModel)
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .setModel(
                  field(Stimmzettel::getWahlvorschlaege),
                  singleWahlvorschlagWithReststimmeAndEinzelstimmeModel)
              .toModel();
      val stimmzettelWith2SelectedWahlvorschlaegenModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () ->
                      List.of(
                          Instancio.of(createEmptySelectedWahlvorschlag("wv1"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createEmptyKandidatWithSingleVoteByWahlvorschlag(
                                              "k11", 1)))
                              .create(),
                          Instancio.of(createEmptySelectedWahlvorschlag("wv2"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () ->
                                      List.of(
                                          createEmptyKandidatWithSingleVoteByWahlvorschlag(
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
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(
              stimmzettelWithSingleWahlvorschlagWithOnlyReststimmenModel));
      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(
              stimmzettelWithSingleWahlvorschlagWithOnlyReststimmenModel));

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
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithEinzelstimmeModel("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithEinzelstimmeModel("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithEinzelstimmeModel("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithEinzelstimmeModel("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithEinzelstimmeModel("wv2"))
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
        val stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithEinzelstimmeModel)
                .toModel();

        val stimmzettelToFind = new LinkedList<Stimmzettel>();
        stimmzettelToFind.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel));

        val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(
                stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel));
        nonMatchingStimmzettel.add(
            createStimmzettelWithGueltigkeitInvalid(
                stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel));

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
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel("wv1"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel("wv2"))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    createSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel("wv1"))
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
        val stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithReststimmeAndEinzelstimmeModel)
                .toModel();
        val stimmzettelWithSingleWahlvorschlagWithStreichungAndEinzelstimmeModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege),
                    singleWahlvorschlagWithStreichungAndReststimmeModel)
                .toModel();
        val stimmzettelWithSingleWahlvorschlagWithReststimmeModel =
            Instancio.of(
                    createEmptyValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .setModel(
                    field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagWithReststimmeModel)
                .toModel();

        stimmzettelToFind.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel));
        stimmzettelToFind.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagWithStreichungAndEinzelstimmeModel));

        val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
        nonMatchingStimmzettel.add(
            Instancio.create(stimmzettelWithSingleWahlvorschlagWithReststimmeModel));

        nonMatchingStimmzettel.add(
            createStimmzettelWithGueltigkeitInvalid(
                stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel));
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(
                stimmzettelWithSingleWahlvorschlagWithReststimmeAndEinzelstimmeModel));

        nonMatchingStimmzettel.add(
            createStimmzettelWithGueltigkeitInvalid(
                stimmzettelWithSingleWahlvorschlagWithStreichungAndEinzelstimmeModel));
        nonMatchingStimmzettel.addAll(
            createStimmzettelWithNonMatchingStimmzettelIDs(
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
      val stimmzettelWith2WahlvorschlaegenModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () ->
                      List.of(
                          Instancio.of(createEmptyWahlvorschlag("wv1"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () -> List.of(createEmptyKandidatWithSingleVoteByVoter("k11", 1)))
                              .create(),
                          Instancio.of(createEmptyWahlvorschlag("wv2"))
                              .supply(
                                  field(Wahlvorschlag::getKandidaten),
                                  () -> List.of(createEmptyKandidatWithSingleVoteByVoter("k21", 1)))
                              .create()))
              .toModel();
      val stimmzettelWithoutAnyWahlvorschlaegeModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .toModel();
      val stimmzettelWithSingleWahlvorschlagWithoutAnyKandidatenModel =
          Instancio.of(
                  createEmptyValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .supply(
                  field(Stimmzettel::getWahlvorschlaege),
                  () -> List.of(Instancio.create(createEmptyWahlvorschlag("wv1"))))
              .toModel();

      val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWith2WahlvorschlaegenModel));
      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(stimmzettelWith2WahlvorschlaegenModel));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWith2WahlvorschlaegenModel));

      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithoutAnyWahlvorschlaegeModel));
      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(
              stimmzettelWithoutAnyWahlvorschlaegeModel));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWithoutAnyWahlvorschlaegeModel));

      nonMatchingStimmzettel.add(
          Instancio.create(stimmzettelWithSingleWahlvorschlagWithoutAnyKandidatenModel));
      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(
              stimmzettelWithSingleWahlvorschlagWithoutAnyKandidatenModel));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(
              stimmzettelWithSingleWahlvorschlagWithoutAnyKandidatenModel));

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
  }

  @Nested
  class CountInvalidStimmzettel {

    @Test
    void should_countStimmzettel_when_foundMatchingStimmzettel() {

      val stimmzettelToCount = new LinkedList<Stimmzettel>();

      stimmzettelToCount.add(
          Instancio.create(
              createInvalidStimmzettelModelWithSingleWahlvorschlagWithEinzelstimme(
                  wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement())));
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

      transactionTemplate.executeWithoutResult(
          status -> stimmzettelRepository.saveAll(stimmzettelToCount));

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
  class GetValidKandidatenVotesPerWahlvorschlagWhereAtLeast2WahlvorschlaegeAreSelectedOrAtLeastOneKandidatHasNotOnlyReststimmen {

    @Test
    void should_countByKandidaten_when_foundWahlvorschlaege() {
      val stimmzettelToCount = new LinkedList<Stimmzettel>();

      val stimmzettelModel1WithSameKandidatWith2Nennungen =
              Instancio.of(createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                      .supply(field(Stimmzettel::getWahlvorschlaege), () -> List.of(
                              Instancio.of(createEmptyWahlvorschlag("wv1"))
                                      .supply(field(Wahlvorschlag::getKandidaten), () -> List.of(
                                              createEmptyKandidatWithSingleVoteByVoter("k11", 1),
                                              createEmptyKandidatWithSingleVoteByVoter("k11", 2),
                                              createEmptyKandidatWithSingleVoteByVoter("k11", 3),
                                              createEmptyKandidatWithSingleVoteByWahlvorschlag ("k12", 1),
                                              createEmptyKandidatWithSingleVoteByWahlvorschlag ("k12", 2),
                                              createEmptyDiscardedKandidat ("k12", 3)

                                      ))
                                      .create(),
                              Instancio.of(createEmptyWahlvorschlag("wv2"))
                                      .supply(field(Wahlvorschlag::getKandidaten), () -> List.of(
                                              createEmptyKandidatWithSingleVoteByVoter("k21", 1),
                                              createEmptyKandidatWithSingleVoteByVoter("k21", 2),
                                              createEmptyKandidatWithSingleVoteByWahlvorschlag ("k22", 1),
                                              createEmptyDiscardedKandidat ("k22", 2)
                                      ))
                                      .create()
                      ))
                      .toModel();
      val stimmzettelModel2WithSameKandidatWith2Nennungen =
              Instancio.of(createEmptyValidStimmzettelModel(wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                      .supply(field(Stimmzettel::getWahlvorschlaege), () -> List.of(
                              Instancio.of(createEmptyWahlvorschlag("wv1"))
                                      .supply(field(Wahlvorschlag::getKandidaten), () -> List.of(
                                              createEmptyKandidatWithSingleVoteByVoter("k11", 1),
                                              createEmptyKandidatWithSingleVoteByVoter("k11", 2),
                                              createEmptyKandidatWithSingleVoteByVoter("k11", 3),
                                              createEmptyKandidatWithSingleVoteByVoter("k12", 1),
                                              createEmptyKandidatWithSingleVoteByWahlvorschlag ("k12", 2)
                                      ))
                                      .create(),
                              Instancio.of(createEmptyWahlvorschlag("wv2"))
                                      .supply(field(Wahlvorschlag::getKandidaten), () -> List.of(
                                              createEmptyKandidatWithSingleVoteByVoter("k21", 1),
                                              createEmptyKandidatWithSingleVoteByVoter("k21", 2)
                                      ))
                                      .create()
                      ))
                      .toModel();
      stimmzettelToCount.add(Instancio.create(stimmzettelModel1WithSameKandidatWith2Nennungen));
      stimmzettelToCount.add(Instancio.create(stimmzettelModel2WithSameKandidatWith2Nennungen));

      transactionTemplate.executeWithoutResult(status -> stimmzettelRepository.saveAll(stimmzettelToCount));

      val result = unitUnderTest.getValidKandidatenVotesPerWahlvorschlagWhereAtLeast2WahlvorschlaegeAreSelectedOrAtLeastOneKandidatHasNotOnlyReststimmen(wahlID, wahlbezirkID);

      val expectedResult = List.of(
              new KandidatStimmenAnzahl("wv1", "k11", 6),
              new KandidatStimmenAnzahl("wv1", "k12", 4),
              new KandidatStimmenAnzahl("wv2", "k21", 4),
              new KandidatStimmenAnzahl("wv2", "k22", 1)
      );
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

      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(stimmzettelWithEinzelstimmeModel));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWithEinzelstimmeModel));

      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(stimmzettelWithStreichungModel));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWithStreichungModel));

      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(stimmzettelWithMultipleStreichungenModel));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWithMultipleStreichungenModel));

      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(
              stimmzettelWithReststimmeAndEinzelstimmeModel));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWithReststimmeAndEinzelstimmeModel));

      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(stimmzettelWithTwoListenkreuzen));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWithTwoListenkreuzen));

      nonMatchingStimmzettel.addAll(
          createStimmzettelWithNonMatchingStimmzettelIDs(stimmzettelWithTwoListenkreuzen));
      nonMatchingStimmzettel.add(
          createStimmzettelWithGueltigkeitInvalid(stimmzettelWithTwoChangedWahlvorschlaegen));

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(stimmzettelToFind);
            stimmzettelRepository.saveAll(nonMatchingStimmzettel);
          });

      val result =
          unitUnderTest
              .getValidKandidatenVotesPerWahlvorschlagWhereAtLeast2WahlvorschlaegeAreSelectedOrAtLeastOneKandidatHasNotOnlyReststimmen(
                  wahlID, wahlbezirkID);

      val expectedResult = getExpectedKandidatenStimmenAnzahl(stimmzettelToFind);
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
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

  // TODO umbauen in nonValid: Listen mit Stimmzettel != VALID
  private Stimmzettel createStimmzettelWithGueltigkeitInvalid(Model<Stimmzettel> stimmzettelModel) {
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
