package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatStimmenAnzahl;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.WahlvorschlagStimmzettelAnzahl;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collections;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StimmzettelServiceTest {

  @Mock StimmzettelValidator stimmzettelValidator;

  @Mock StimmzettelModelMapper stimmzettelModelMapper;

  @Mock StimmzettelRepository stimmzettelRepository;

  @InjectMocks StimmzettelService unitUnderTest;

  @Captor ArgumentCaptor<Iterable<Stimmzettel>> saveStimmzettelArgumentCaptor;

  @Nested
  class GetStimmzettel {

    @Test
    void should_returnMappedStimmzettel_when_repoFoundData() {
      val stimmzettelOwner = Instancio.create(TeamBezirkUndWahlIDModel.class);

      val mockedRepoResponse = Instancio.ofList(Stimmzettel.class).size(5).create();
      Mockito.when(
              stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                  stimmzettelOwner.wahlbezirkID(),
                  stimmzettelOwner.wahlID(),
                  stimmzettelOwner.teamID()))
          .thenReturn(mockedRepoResponse);

      Mockito.when(stimmzettelModelMapper.toModel(any(Stimmzettel.class)))
          .thenAnswer(invocation -> Instancio.create(StimmzettelOfTeamModel.class));

      val result = unitUnderTest.getStimmzettel(stimmzettelOwner);
      Assertions.assertThat(result).hasSize(5);
      Mockito.verify(stimmzettelValidator).validOrThrow(stimmzettelOwner);
    }

    @Test
    void should_returnEmptyList_when_repoFoundNoData() {
      val stimmzettelOwner = Instancio.create(TeamBezirkUndWahlIDModel.class);

      Mockito.when(
              stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                  stimmzettelOwner.wahlbezirkID(),
                  stimmzettelOwner.wahlID(),
                  stimmzettelOwner.teamID()))
          .thenReturn(Collections.emptyList());

      val result = unitUnderTest.getStimmzettel(stimmzettelOwner);
      Assertions.assertThat(result).isEmpty();
    }

    @Test
    void should_throwException_when_validationOfOwnerFailed() {
      val stimmzettelOwner = Instancio.create(TeamBezirkUndWahlIDModel.class);

      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
      Mockito.doThrow(mockedWlsException).when(stimmzettelValidator).validOrThrow(stimmzettelOwner);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getStimmzettel(stimmzettelOwner))
          .isEqualTo(mockedWlsException);
    }
  }

  @Nested
  class SaveStimmzettel {

    @Test
    void should_saveMappedStimmzettel_when_stimmzettelAndOwnerIsGiven() {
      val stimmzettelOwner = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val stimmzettelToSave = Instancio.ofList(StimmzettelOfTeamModel.class).size(5).create();

      Mockito.when(stimmzettelModelMapper.toEntity(Mockito.eq(stimmzettelOwner), any()))
          .thenAnswer(invocation -> Instancio.create(Stimmzettel.class));

      unitUnderTest.saveStimmzettel(stimmzettelOwner, stimmzettelToSave);

      Mockito.verify(stimmzettelValidator).validOrThrow(stimmzettelOwner);
      Mockito.verify(stimmzettelValidator).validOrThrow(stimmzettelToSave);
      Mockito.verify(stimmzettelRepository)
          .deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
              stimmzettelOwner.wahlbezirkID(),
              stimmzettelOwner.wahlID(),
              stimmzettelOwner.teamID());
      Mockito.verify(stimmzettelRepository).saveAll(saveStimmzettelArgumentCaptor.capture());
      Assertions.assertThat(saveStimmzettelArgumentCaptor.getValue()).hasSize(5);
    }

    @Test
    void should_throwException_when_validationOfOwnerFailed() {
      val stimmzettelOwner = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val stimmzettelToSave = Instancio.ofList(StimmzettelOfTeamModel.class).size(5).create();

      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
      Mockito.doThrow(mockedWlsException).when(stimmzettelValidator).validOrThrow(stimmzettelOwner);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveStimmzettel(stimmzettelOwner, stimmzettelToSave))
          .isEqualTo(mockedWlsException);
      Mockito.verifyNoInteractions(stimmzettelRepository);
    }

    @Test
    void should_throwException_when_validationOfStimmzettelFailed() {
      val stimmzettelOwner = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val stimmzettelToSave = Instancio.ofList(StimmzettelOfTeamModel.class).size(5).create();

      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
      Mockito.doNothing()
          .when(stimmzettelValidator)
          .validOrThrow(any(TeamBezirkUndWahlIDModel.class));
      Mockito.doThrow(mockedWlsException)
          .when(stimmzettelValidator)
          .validOrThrow(stimmzettelToSave);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveStimmzettel(stimmzettelOwner, stimmzettelToSave))
          .isEqualTo(mockedWlsException);
      Mockito.verifyNoInteractions(stimmzettelRepository);
    }
  }

  @Nested
  class GetAnzahlStimmzettel {

    @Test
    void should_returnCount_when_repoHasMatchingData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      val mockedCountFromRepo = Instancio.gen().ints().get();
      Mockito.when(
              stimmzettelRepository.countByIdWahlbezirkIDAndIdWahlID(
                  bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID()))
          .thenReturn(mockedCountFromRepo);

      val result = unitUnderTest.getAnzahlStimmzettel(bezirkUndWahlID);

      Assertions.assertThat(result).isEqualTo(mockedCountFromRepo);
    }

    @Test
    void should_returnZero_when_repoHasNoMatchingData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              stimmzettelRepository.countByIdWahlbezirkIDAndIdWahlID(
                  bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID()))
          .thenReturn(0);

      val result = unitUnderTest.getAnzahlStimmzettel(bezirkUndWahlID);

      Assertions.assertThat(result).isEqualTo(0);
    }

    @Test
    void should_throwException_when_validationOfParameterFailed() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");
      Mockito.doThrow(mockedWlsException).when(stimmzettelValidator).validOrThrow(bezirkUndWahlID);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getAnzahlStimmzettel(bezirkUndWahlID))
          .isEqualTo(mockedWlsException);
    }
  }

  @Nested
  class GetStimmzettelWithExactlyOneWahlvorschlagSelected {

    @Test
    void should_returnMappedCollection_when_repoReturnedData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              stimmzettelRepository
                  .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
                      bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(Collections.emptyList());

      val result = unitUnderTest.getStimmzettelWithExactlyOneWahlvorschlagSelected(bezirkUndWahlID);

      Assertions.assertThat(result).isEmpty();
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
      Mockito.verifyNoInteractions(stimmzettelModelMapper);
    }

    @Test
    void should_returnEmptyCollection_when_repoReturnedNoData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      val mockedRepoResponse = Instancio.createList(WahlvorschlagStimmzettelAnzahl.class);
      Mockito.when(
              stimmzettelRepository
                  .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneSelectedWahlvorschlagAndNoOtherKennzeichen(
                      bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(mockedRepoResponse);

      Mockito.when(stimmzettelModelMapper.toModel(any(WahlvorschlagStimmzettelAnzahl.class)))
          .thenReturn(Instancio.create(WahlvorschlagStimmzettelAnzahlModel.class));

      val result = unitUnderTest.getStimmzettelWithExactlyOneWahlvorschlagSelected(bezirkUndWahlID);

      Assertions.assertThat(result).hasSize(mockedRepoResponse.size());
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
      Mockito.verify(stimmzettelModelMapper, times(mockedRepoResponse.size()))
          .toModel(any(WahlvorschlagStimmzettelAnzahl.class));
    }
  }

  @Nested
  class CountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges {

    @Test
    void should_returnMappedCollection_when_repoReturnedData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              stimmzettelRepository
                  .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                      bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(Collections.emptyList());

      val result =
          unitUnderTest
              .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                  bezirkUndWahlID);

      Assertions.assertThat(result).isEmpty();
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
      Mockito.verifyNoInteractions(stimmzettelModelMapper);
    }

    @Test
    void should_returnEmptyCollection_when_repoReturnedNoData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      val mockedRepoResponse = Instancio.createList(WahlvorschlagStimmzettelAnzahl.class);
      Mockito.when(
              stimmzettelRepository
                  .getWahlvorschlaegeAndCountWhereStimmzettelHasOnlyOneWahlvorschlagAndAtLeastOneOtherKennzeichen(
                      bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(mockedRepoResponse);

      Mockito.when(stimmzettelModelMapper.toModel(any(WahlvorschlagStimmzettelAnzahl.class)))
          .thenReturn(Instancio.create(WahlvorschlagStimmzettelAnzahlModel.class));

      val result =
          unitUnderTest
              .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                  bezirkUndWahlID);

      Assertions.assertThat(result).hasSize(mockedRepoResponse.size());
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
      Mockito.verify(stimmzettelModelMapper, times(mockedRepoResponse.size()))
          .toModel(any(WahlvorschlagStimmzettelAnzahl.class));
    }
  }

  @Nested
  class GetKandidatVotes {

    @Test
    void should_returnMappedCollection_when_repoReturnedData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              stimmzettelRepository
                  .getSumValidKandidatenVotesPerWahlvorschlagWhenNotOnlyOneListenkreuzReststimmeAreGiven(
                      bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(Collections.emptyList());

      val result = unitUnderTest.getKandidatVotes(bezirkUndWahlID);

      Assertions.assertThat(result).isEmpty();
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
      Mockito.verifyNoInteractions(stimmzettelModelMapper);
    }

    @Test
    void should_returnEmptyCollection_when_repoReturnedNoData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      val mockedRepoResponse = Instancio.createList(KandidatStimmenAnzahl.class);
      Mockito.when(
              stimmzettelRepository
                  .getSumValidKandidatenVotesPerWahlvorschlagWhenNotOnlyOneListenkreuzReststimmeAreGiven(
                      bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(mockedRepoResponse);

      Mockito.when(stimmzettelModelMapper.toModel(any(KandidatStimmenAnzahl.class)))
          .thenReturn(Instancio.create(KandidatStimmenAnzahlModel.class));

      val result = unitUnderTest.getKandidatVotes(bezirkUndWahlID);

      Assertions.assertThat(result).hasSize(mockedRepoResponse.size());
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
      Mockito.verify(stimmzettelModelMapper, times(mockedRepoResponse.size()))
          .toModel(any(KandidatStimmenAnzahl.class));
    }
  }

  @Nested
  class GetCountUngueltige {

    @Test
    void should_returnValueOfRepo_when_called() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      val mockedRepoResponse = Instancio.gen().longs().get();
      Mockito.when(
              stimmzettelRepository.countInvalidStimmzettel(
                  bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(mockedRepoResponse);

      val result = unitUnderTest.getCountUngueltige(bezirkUndWahlID);
      Assertions.assertThat(result).isEqualTo(mockedRepoResponse);
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
    }
  }
}
