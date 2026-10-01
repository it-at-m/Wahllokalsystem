package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankDiscardedKandidat;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithInvalidVote;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByVoter;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankKandidatWithSingleVoteByWahlvorschlag;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankNonSelectedWahlvorschlagModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankSelectedWahlvorschlagModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankStimmzettelModel;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.InstancioModels.createBlankValidStimmzettelModel;
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
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithInvalideVoteAndReststimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithMultipleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithOnlyReststimmenKandidaten;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithReststimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithReststimmeAndEinzelstimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithSingleStreichungen;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithStreichungAndEinzelStimme;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.StimmzettelRepositoryTestWahlvorschlagModels.singleWahlvorschlagModelWithStreichungAndReststimme;
import static org.instancio.Select.field;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
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
class MBWStimmzettelRepositoryImplTest {

  private final String wahlID = Instancio.create(String.class);
  private final String wahlbezirkID = Instancio.create(String.class);
  private final String teamA = "A";
  private final String teamB = "B";

  @Autowired MBWStimmzettelRepository unitUnderTest;
  @Autowired StimmzettelRepository stimmzettelRepository;

  @Autowired TransactionTemplate transactionTemplate;

  private AtomicInteger stimmzettelkennungSequenz = new AtomicInteger(1);
  private AtomicInteger wahlvorschlagIDCounter = new AtomicInteger(1);

  @BeforeEach
  public void setup() {
    stimmzettelkennungSequenz = new AtomicInteger(1);
    wahlvorschlagIDCounter = new AtomicInteger(1);
  }

  @AfterEach
  public void teardown() {
    stimmzettelRepository.deleteAll();
  }

  @Nested
  class GetStapelA {

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

      val result = unitUnderTest.getStapelA(wahlID, wahlbezirkID);

      val expectedResult =
          List.of(
              new WahlvorschlagStimmzettelAnzahl("wv1", 3L),
              new WahlvorschlagStimmzettelAnzahl("wv2", 2L));
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);

      Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(stimmzettelToFind.size());
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

      val result = unitUnderTest.getStapelA(wahlID, wahlbezirkID);

      val expectedResult = List.of(new WahlvorschlagStimmzettelAnzahl("onlyReststimmen", 1L));
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);

      Assertions.assertThat(stimmzettelRepository.count())
          .isEqualTo(stimmzettelToFind.size() + nonMatchingStimmzettel.size());
    }

    @Test
    void should_returnEmptyList_when_noDataWasFound() {
      val result = unitUnderTest.getStapelA(wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEmpty();
    }
  }

  @Nested
  class GetStapelBGroupedByWahlvorschlag {

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

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wv1", 3L),
                new WahlvorschlagStimmzettelAnzahl("wv2", 2L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);

        Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(stimmzettelToFind.size());
      }

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasEinzelstimmen();

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wvEinzelstimme", 1L),
                new WahlvorschlagStimmzettelAnzahl("wvInvalidVote+Reststimme", 1L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Nested
    class OnlyOneWahlvorschlagThatHasReststimmenAndOneOtherKennzeichen {

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

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wv1", 2L),
                new WahlvorschlagStimmzettelAnzahl("wv2", 3L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);

        Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(stimmzettelToFind.size());
      }

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasReststimmenAndOneOtherKennzeichen();

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

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
    class OnlyOneWahlvorschlagThatHasOnlyReststimmenAndStimmzettelInvalidVotes {

      @Test
      void should_countByWahlvorschlag_when_foundWahlvorschlaege() {
        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 2)))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 2)))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k12", 1)))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv2"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k21", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k22", 3)))
                            .create()))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> stimmzettelRepository.saveAll(stimmzettelToFind));
        Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(stimmzettelToFind.size());

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new WahlvorschlagStimmzettelAnzahl("wv1", 3L),
                new WahlvorschlagStimmzettelAnzahl("wv2", 1L));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasOnlyReststimmenAndStimmzettelInvalidVotes();

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

        val expectedResult = List.of(new WahlvorschlagStimmzettelAnzahl("onlyReststimmen", 1L));
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

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

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
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasStreichungen();

        val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);

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

      val result = unitUnderTest.getStapelBGroupedByWahlvorschlag(wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEmpty();

      Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(nonMatchingStimmzettel.size());
    }
  }

  @Nested
  class GetStapelBGroupedByKandidat {

    @Nested
    class OnlyOneWahlvorschlagThatHasEinzelstimmen {

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasEinzelstimmen();

        val result = unitUnderTest.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new KandidatStimmenAnzahl("wvEinzelstimme", "k1", 1),
                new KandidatStimmenAnzahl("wvInvalidVote+Reststimme", "k1", 0),
                new KandidatStimmenAnzahl("wvInvalidVote+Reststimme", "k2", 1));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Nested
    class OnlyOneWahlvorschlagThatHasReststimmenAndOneOtherKennzeichen {

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasReststimmenAndOneOtherKennzeichen();

        val result = unitUnderTest.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new KandidatStimmenAnzahl("wvReststimme+Einzelstimme", "k1", 1),
                new KandidatStimmenAnzahl("wvReststimme+Einzelstimme", "k2", 2),
                new KandidatStimmenAnzahl("wvStreichung+Reststimme", "k1", 0),
                new KandidatStimmenAnzahl("wvStreichung+Reststimme", "k2", 1));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Nested
    class OnlyOneWahlvorschlagThatHasOnlyReststimmenAndStimmzettelInvalidVotes {

      @Test
      void should_countByWahlvorschlag_when_foundWahlvorschlaege() {
        val stimmzettelToFind = new LinkedList<Stimmzettel>();

        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 2)))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 2)))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv1"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k12", 1)))
                            .create()))
                .create());
        stimmzettelToFind.add(
            Instancio.of(
                    createBlankValidStimmzettelModel(
                        wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
                .set(field(Stimmzettel::getInvalideVotes), 1)
                .set(
                    field(Stimmzettel::getWahlvorschlaege),
                    List.of(
                        Instancio.of(createBlankSelectedWahlvorschlagModel("wv2"))
                            .set(
                                field(Wahlvorschlag::getKandidaten),
                                List.of(
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k21", 1),
                                    createBlankKandidatWithSingleVoteByWahlvorschlag("k22", 3)))
                            .create()))
                .create());

        transactionTemplate.executeWithoutResult(
            status -> stimmzettelRepository.saveAll(stimmzettelToFind));
        Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(stimmzettelToFind.size());

        val result = unitUnderTest.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new KandidatStimmenAnzahl("wv1", "k11", 5),
                new KandidatStimmenAnzahl("wv1", "k12", 1),
                new KandidatStimmenAnzahl("wv2", "k21", 1),
                new KandidatStimmenAnzahl("wv2", "k22", 1));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasOnlyReststimmenAndStimmzettelInvalidVotes();

        val result = unitUnderTest.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);

        val expectedResult = List.of(new KandidatStimmenAnzahl("onlyReststimmen", "k2", 2));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Nested
    class OnlyOneWahlvorschlagThatHasStreichungen {

      @Test
      void should_findMatchingStimmzettel_when_matchingAndNonMatchingStimmzettelAreGiven() {
        prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasStreichungen();

        val result = unitUnderTest.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);

        val expectedResult =
            List.of(
                new KandidatStimmenAnzahl("wvStreichung+Reststimme", "k1", 0),
                new KandidatStimmenAnzahl("wvStreichung+Reststimme", "k2", 1),
                new KandidatStimmenAnzahl("wvStreichung+Einzelstimme", "k1", 0L),
                new KandidatStimmenAnzahl("wvStreichung+Einzelstimme", "k2", 1L),
                new KandidatStimmenAnzahl("wvStreichung", "k2", 0),
                new KandidatStimmenAnzahl("wvMultipleStreichung", "k2", 0));
        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }

    @Test
    void should_countCorrectly_when_stimmzettelAreGiven() {
      val stimmzettelToFind = new LinkedList<Stimmzettel>();

      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .set(
                  field(Stimmzettel::getWahlvorschlaege),
                  List.of(
                      Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                          .set(
                              field(Wahlvorschlag::getKandidaten),
                              List.of(
                                  createBlankKandidatWithSingleVoteByVoter("k11", 1, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k11", 2, 1),
                                  createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 3),
                                  createBlankKandidatWithSingleVoteByVoter("k12", 1, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k12", 2, 2),
                                  createBlankKandidatWithSingleVoteByVoter("k13", 1, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k13", 2, 2),
                                  createBlankKandidatWithSingleVoteByVoter("k14", 1, 3),
                                  createBlankKandidatWithInvalidVote("k15", 1),
                                  createBlankDiscardedKandidat("k16", 1)))
                          .create()))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .set(
                  field(Stimmzettel::getWahlvorschlaege),
                  List.of(
                      Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                          .set(
                              field(Wahlvorschlag::getKandidaten),
                              List.of(
                                  createBlankKandidatWithSingleVoteByVoter("k21", 1, 2),
                                  createBlankKandidatWithSingleVoteByVoter("k21", 2, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k22", 1, 3),
                                  createBlankKandidatWithSingleVoteByWahlvorschlag("k23", 1),
                                  createBlankKandidatWithSingleVoteByVoter("k23", 2, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k23", 3, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k24", 1, 3)))
                          .create()))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .set(
                  field(Stimmzettel::getWahlvorschlaege),
                  List.of(
                      Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                          .set(
                              field(Wahlvorschlag::getKandidaten),
                              List.of(
                                  createBlankKandidatWithSingleVoteByVoter("k11", 1, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k11", 2, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k12", 1, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k14", 1, 2),
                                  createBlankKandidatWithInvalidVote("k14", 2),
                                  createBlankKandidatWithInvalidVote("k15", 1),
                                  createBlankDiscardedKandidat("k16", 1)))
                          .create()))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .set(
                  field(Stimmzettel::getWahlvorschlaege),
                  List.of(
                      Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                          .set(
                              field(Wahlvorschlag::getKandidaten),
                              List.of(
                                  createBlankKandidatWithSingleVoteByVoter("k21", 2, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k22", 3, 3),
                                  createBlankKandidatWithSingleVoteByWahlvorschlag("k23", 1),
                                  createBlankKandidatWithSingleVoteByVoter("k23", 2, 2),
                                  createBlankKandidatWithSingleVoteByVoter("k24", 2, 3)))
                          .create()))
              .create());
      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamB, stimmzettelkennungSequenz.getAndIncrement()))
              .set(
                  field(Stimmzettel::getWahlvorschlaege),
                  List.of(
                      Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv1"))
                          .set(
                              field(Wahlvorschlag::getKandidaten),
                              List.of(
                                  createBlankKandidatWithSingleVoteByVoter("k11", 1, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k11", 2, 1),
                                  createBlankKandidatWithSingleVoteByWahlvorschlag("k11", 3),
                                  createBlankKandidatWithSingleVoteByVoter("k12", 1, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k12", 2, 2),
                                  createBlankKandidatWithSingleVoteByWahlvorschlag("k13", 1),
                                  createBlankKandidatWithSingleVoteByVoter("k14", 1, 3),
                                  createBlankDiscardedKandidat("k14", 2),
                                  createBlankDiscardedKandidat("k14", 3),
                                  createBlankKandidatWithInvalidVote("k15", 1),
                                  createBlankDiscardedKandidat("k16", 1)))
                          .create()))
              .create());

      stimmzettelToFind.add(
          Instancio.of(
                  createBlankValidStimmzettelModel(
                      wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
              .set(
                  field(Stimmzettel::getWahlvorschlaege),
                  List.of(
                      Instancio.of(createBlankNonSelectedWahlvorschlagModel("wv2"))
                          .set(
                              field(Wahlvorschlag::getKandidaten),
                              List.of(
                                  createBlankKandidatWithSingleVoteByVoter("k21", 1, 2),
                                  createBlankKandidatWithSingleVoteByVoter("k21", 2, 1),
                                  createBlankKandidatWithSingleVoteByVoter("k22", 1, 3),
                                  createBlankKandidatWithSingleVoteByWahlvorschlag("k23", 1),
                                  createBlankKandidatWithSingleVoteByVoter("k23", 2, 2),
                                  createBlankKandidatWithSingleVoteByVoter("k24", 1, 3)))
                          .create()))
              .create());

      transactionTemplate.executeWithoutResult(
          status -> stimmzettelRepository.saveAll(stimmzettelToFind));

      val expectedResult =
          List.of(
              new KandidatStimmenAnzahl("wv1", "k11", 8),
              new KandidatStimmenAnzahl("wv1", "k12", 7),
              new KandidatStimmenAnzahl("wv1", "k13", 4),
              new KandidatStimmenAnzahl("wv1", "k14", 8),
              new KandidatStimmenAnzahl("wv1", "k15", 0),
              new KandidatStimmenAnzahl("wv1", "k16", 0),
              new KandidatStimmenAnzahl("wv2", "k21", 7),
              new KandidatStimmenAnzahl("wv2", "k22", 9),
              new KandidatStimmenAnzahl("wv2", "k23", 9),
              new KandidatStimmenAnzahl("wv2", "k24", 9));

      val result = unitUnderTest.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    @Test
    void should_returnEmptyList_when_noStimmzettelIsMatching() {
      prepareRepoWithStapelBDataNotToFind();

      val result = unitUnderTest.getStapelBGroupedByKandidat(wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEmpty();
    }
  }

  @Nested
  class GetStapelD {

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
              createBlankStimmzettelModel(
                  wahlID,
                  wahlbezirkID,
                  teamA,
                  stimmzettelkennungSequenz.getAndIncrement(),
                  StimmzettelGueltigkeit.LEER)));
      stimmzettelToCount.add(
          Instancio.create(
              createBlankStimmzettelModel(
                  wahlID,
                  wahlbezirkID,
                  teamA,
                  stimmzettelkennungSequenz.getAndIncrement(),
                  StimmzettelGueltigkeit.BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG)));
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
                  invalidStimmzettelModelWithSingleEinzelstimme,
                  StimmzettelGueltigkeit.INVALID,
                  StimmzettelGueltigkeit.BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG,
                  StimmzettelGueltigkeit.LEER));

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(stimmzettelToCount);
            stimmzettelRepository.saveAll(nonMatchingStimmzettel);
          });

      val result = unitUnderTest.getStapelD(wahlID, wahlbezirkID);

      Assertions.assertThat(result).isEqualTo(stimmzettelToCount.size());

      Assertions.assertThat(stimmzettelRepository.count())
          .isEqualTo(stimmzettelToCount.size() + nonMatchingStimmzettel.size());
    }

    @Test
    void should_return0_when_noMatchingStimmzettelAreGiven() {
      val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();

      Arrays.stream(StimmzettelGueltigkeit.values())
          .forEach(
              gueltigkeit -> {
                if (!StimmzettelGueltigkeit.INVALID.equals(gueltigkeit)
                    && !StimmzettelGueltigkeit.LEER.equals(gueltigkeit)
                    && !StimmzettelGueltigkeit.BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG.equals(
                        gueltigkeit)) {
                  nonMatchingStimmzettel.add(
                      Instancio.of(
                              createBlankStimmzettelModel(
                                  wahlID,
                                  wahlbezirkID,
                                  teamA,
                                  stimmzettelkennungSequenz.getAndIncrement(),
                                  gueltigkeit))
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

      val result = unitUnderTest.getStapelD(wahlID, wahlbezirkID);
      Assertions.assertThat(result).isEqualTo(0L);

      Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(nonMatchingStimmzettel.size());
    }
  }

  @Nested
  class GetStapelC {

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

      val result = unitUnderTest.getStapelC(wahlID, wahlbezirkID);

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

      Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(stimmzettelToCount.size());
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
      stimmzettelToFind.add(Instancio.create(stimmzettelWithTwoListenkreuzen));
      stimmzettelToFind.add(Instancio.create(stimmzettelWithTwoChangedWahlvorschlaegen));

      val nonMatchingStimmzettel = new LinkedList<Stimmzettel>();
      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithExactlyOneListenkreuzModel));

      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithEinzelstimmeModel));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithEinzelstimmeModel));

      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithStreichungModel));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithStreichungModel));

      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithMultipleStreichungenModel));
      nonMatchingStimmzettel.addAll(
          createNonValidVariants(stimmzettelWithMultipleStreichungenModel));

      nonMatchingStimmzettel.add(Instancio.create(stimmzettelWithReststimmeAndEinzelstimmeModel));
      nonMatchingStimmzettel.addAll(
          createNonValidVariants(stimmzettelWithReststimmeAndEinzelstimmeModel));

      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithTwoListenkreuzen));
      nonMatchingStimmzettel.addAll(createNonValidVariants(stimmzettelWithTwoListenkreuzen));

      transactionTemplate.executeWithoutResult(
          status -> {
            stimmzettelRepository.saveAll(stimmzettelToFind);
            stimmzettelRepository.saveAll(nonMatchingStimmzettel);
          });

      val result = unitUnderTest.getStapelC(wahlID, wahlbezirkID);

      val expectedResult = getExpectedKandidatenStimmenAnzahl(stimmzettelToFind);
      Assertions.assertThat(result)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);

      Assertions.assertThat(stimmzettelRepository.count())
          .isEqualTo(stimmzettelToFind.size() + nonMatchingStimmzettel.size());
    }

    @Test
    void should_returnEmptyList_when_noDataWasFound() {
      val result = unitUnderTest.getStapelC(wahlID, wahlbezirkID);
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
      Model<Stimmzettel> stimmzettelModel, StimmzettelGueltigkeit... gueltigkeitToExclude) {
    return Arrays.stream(StimmzettelGueltigkeit.values())
        .filter(gueltigkeit -> !Arrays.asList(gueltigkeitToExclude).contains(gueltigkeit))
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

  private void prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasEinzelstimmen() {
    val stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel =
        Instancio.of(
                createBlankValidStimmzettelModel(
                    wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
            .setModel(
                field(Stimmzettel::getWahlvorschlaege), singleWahlvorschlagModelWithEinzelstimme)
            .toModel();
    val stimmzettelWithSingleWahlvorschlagAndInvalidVote =
        Instancio.of(
                createBlankValidStimmzettelModel(
                    wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
            .setModel(
                field(Stimmzettel::getWahlvorschlaege),
                singleWahlvorschlagModelWithInvalideVoteAndReststimme)
            .toModel();

    val stimmzettelToFind = new LinkedList<Stimmzettel>();
    stimmzettelToFind.add(Instancio.create(stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel));
    stimmzettelToFind.add(Instancio.create(stimmzettelWithSingleWahlvorschlagAndInvalidVote));

    val nonMatchingStimmzettel =
        new LinkedList<>(
            createNonValidVariants(stimmzettelWithSingleWahlvorschlagAndEinzelstimmeModel));
    nonMatchingStimmzettel.addAll(
        createNonValidVariants(stimmzettelWithSingleWahlvorschlagAndInvalidVote));

    transactionTemplate.executeWithoutResult(
        status -> {
          stimmzettelRepository.saveAll(stimmzettelToFind);
          stimmzettelRepository.saveAll(nonMatchingStimmzettel);
        });

    Assertions.assertThat(stimmzettelRepository.count())
        .isEqualTo(stimmzettelToFind.size() + nonMatchingStimmzettel.size());
  }

  private void
      prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasReststimmenAndOneOtherKennzeichen() {
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

    Assertions.assertThat(stimmzettelRepository.count())
        .isEqualTo(stimmzettelToFind.size() + nonMatchingStimmzettel.size());
  }

  private void prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasStreichungen() {
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

    Assertions.assertThat(stimmzettelRepository.count())
        .isEqualTo(stimmzettelToFind.size() + nonMatchingStimmzettel.size());
  }

  private void
      prepareRepoWithStapelBDataToFind_OnlyOneWahlvorschlagThatHasOnlyReststimmenAndStimmzettelInvalidVotes() {
    val validStimmzettelWithInvalidVotesAndWithOneWahlvorschlagWithOnlyReststimmen =
        Instancio.of(
                createValidStimmzettelModelWithSingleWahlvorschlagWithOnlyReststimmen(
                    wahlID, wahlbezirkID, teamA, stimmzettelkennungSequenz.getAndIncrement()))
            .set(field(Stimmzettel::getInvalideVotes), 1)
            .toModel();

    val stimmzettelToFind = new LinkedList<Stimmzettel>();
    stimmzettelToFind.add(
        Instancio.create(
            validStimmzettelWithInvalidVotesAndWithOneWahlvorschlagWithOnlyReststimmen));

    val nonMatchingStimmzettel =
        new LinkedList<>(
            createStimmzettelWithNonMatchingStimmzettelIDs(
                validStimmzettelWithInvalidVotesAndWithOneWahlvorschlagWithOnlyReststimmen));

    transactionTemplate.executeWithoutResult(
        status -> {
          stimmzettelRepository.saveAll(stimmzettelToFind);
          stimmzettelRepository.saveAll(nonMatchingStimmzettel);
        });

    Assertions.assertThat(stimmzettelRepository.count())
        .isEqualTo(stimmzettelToFind.size() + nonMatchingStimmzettel.size());
  }

  private void prepareRepoWithStapelBDataNotToFind() {
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

    Assertions.assertThat(stimmzettelRepository.count()).isEqualTo(nonMatchingStimmzettel.size());
  }
}
