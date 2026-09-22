package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatStimmenAnzahl;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.MBWStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.WahlvorschlagStimmzettelAnzahl;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collections;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MBWStimmzettelServiceTest {

  @Mock StimmzettelValidator stimmzettelValidator;

  @Mock StimmzettelModelMapper stimmzettelModelMapper;

  @Mock MBWStimmzettelRepository mbwStimmzettelRepository;

  @InjectMocks MBWStimmzettelService unitUnderTest;

  @Nested
  class GetCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected {

    @Test
    void should_returnMappedCollection_when_repoReturnedData() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              mbwStimmzettelRepository.getStapelA(
                  bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(Collections.emptyList());

      val result =
          unitUnderTest.getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
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
              mbwStimmzettelRepository.getStapelA(
                  bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(mockedRepoResponse);

      Mockito.when(stimmzettelModelMapper.toModel(any(WahlvorschlagStimmzettelAnzahl.class)))
          .thenReturn(Instancio.create(WahlvorschlagStimmzettelAnzahlModel.class));

      val result =
          unitUnderTest.getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
              bezirkUndWahlID);

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
              mbwStimmzettelRepository.getStapelB(
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
              mbwStimmzettelRepository.getStapelB(
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
              mbwStimmzettelRepository.getStapelBC(
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
              mbwStimmzettelRepository.getStapelBC(
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
              mbwStimmzettelRepository.getStapelD(
                  bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID()))
          .thenReturn(mockedRepoResponse);

      val result = unitUnderTest.getCountUngueltige(bezirkUndWahlID);
      Assertions.assertThat(result).isEqualTo(mockedRepoResponse);
      Mockito.verify(stimmzettelValidator).validOrThrow(bezirkUndWahlID);
    }
  }
}
