package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamBezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamErfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.StimmzettelerfassungService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
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
class TeamStatusServiceTest {

  @Mock TeamErfassungStatusValidator teamErfassungStatusValidator;

  @Mock StimmzettelerfassungTeamStatusRepository stimmzettelerfassungTeamStatusRepository;

  @Mock TeamErfassungStatusModelMapper teamErfassungStatusModelMapper;

  @Mock StimmzettelerfassungService stimmzettelerfassungService;

  @InjectMocks TeamStatusService unitUnderTest;

  @Nested
  class SaveTeamStatus {

    @Test
    void should_saveMappedEntity_when_idAndStatusAreValid() {
      val id = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val statusToSave = Instancio.create(TeamErfassungStatusModel.class);
      val entityToSave = Instancio.create(StimmzettelerfassungTeamStatus.class);

      Mockito.when(teamErfassungStatusModelMapper.toEntity(id, statusToSave))
          .thenReturn(entityToSave);

      unitUnderTest.saveTeamStatus(id, statusToSave);

      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(statusToSave);
      Mockito.verify(teamErfassungStatusModelMapper).toEntity(id, statusToSave);
      Mockito.verify(stimmzettelerfassungTeamStatusRepository).save(entityToSave);
    }

    @Test
    void should_triggerRegisterStimmzettelerfassungBearbeitung_when_teamStatusIsInBearbeitung() {
      val id = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val statusToSave = TeamErfassungStatusModel.IN_BEARBEITUNG;
      val entityToSave = Instancio.create(StimmzettelerfassungTeamStatus.class);

      Mockito.when(teamErfassungStatusModelMapper.toEntity(id, statusToSave))
          .thenReturn(entityToSave);

      unitUnderTest.saveTeamStatus(id, statusToSave);

      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(statusToSave);
      Mockito.verify(teamErfassungStatusModelMapper).toEntity(id, statusToSave);
      Mockito.verify(stimmzettelerfassungTeamStatusRepository).save(entityToSave);
      Mockito.verify(stimmzettelerfassungService)
          .registerStimmzettelerfassungStart(new BezirkUndWahlID(id.wahlID(), id.wahlbezirkID()));
    }

    @Test
    void should_throwException_when_validationOfIdFailed() {
      val id = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val statusToSave = Instancio.create(TeamErfassungStatusModel.class);
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

      Mockito.doThrow(mockedWlsException).when(teamErfassungStatusValidator).isValidOrThrow(id);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveTeamStatus(id, statusToSave))
          .isEqualTo(mockedWlsException);

      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.verifyNoInteractions(teamErfassungStatusModelMapper);
      Mockito.verifyNoInteractions(stimmzettelerfassungTeamStatusRepository);
    }

    @Test
    void should_throwException_when_validationOfStatusFailed() {
      val id = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val statusToSave = Instancio.create(TeamErfassungStatusModel.class);
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

      Mockito.doNothing().when(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.doThrow(mockedWlsException)
          .when(teamErfassungStatusValidator)
          .isValidOrThrow(statusToSave);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveTeamStatus(id, statusToSave))
          .isEqualTo(mockedWlsException);

      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(statusToSave);
      Mockito.verifyNoInteractions(teamErfassungStatusModelMapper);
      Mockito.verifyNoInteractions(stimmzettelerfassungTeamStatusRepository);
    }
  }

  @Nested
  class GetTeamStatus {

    @Test
    void should_returnMappedStatus_when_entityIsFound() {
      val id = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val entityID = Instancio.create(TeamBezirkUndWahlID.class);
      val entityStatus = Instancio.create(TeamErfassungStatus.class);
      val entityFromRepo = new StimmzettelerfassungTeamStatus(entityID, entityStatus);
      val mappedStatus = Instancio.create(TeamErfassungStatusModel.class);

      Mockito.when(teamErfassungStatusModelMapper.toEntity(id)).thenReturn(entityID);
      Mockito.when(stimmzettelerfassungTeamStatusRepository.findById(entityID))
          .thenReturn(Optional.of(entityFromRepo));
      Mockito.when(teamErfassungStatusModelMapper.toModel(entityStatus)).thenReturn(mappedStatus);

      val result = unitUnderTest.getTeamStatus(id);

      Assertions.assertThat(result).contains(mappedStatus);
      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.verify(teamErfassungStatusModelMapper).toEntity(id);
      Mockito.verify(stimmzettelerfassungTeamStatusRepository).findById(entityID);
      Mockito.verify(teamErfassungStatusModelMapper).toModel(entityStatus);
    }

    @Test
    void should_returnEmptyOptional_when_entityIsNotFound() {
      val id = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val entityID = Instancio.create(TeamBezirkUndWahlID.class);

      Mockito.when(teamErfassungStatusModelMapper.toEntity(id)).thenReturn(entityID);
      Mockito.when(stimmzettelerfassungTeamStatusRepository.findById(entityID))
          .thenReturn(Optional.empty());

      val result = unitUnderTest.getTeamStatus(id);

      Assertions.assertThat(result).isEmpty();
      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.verify(teamErfassungStatusModelMapper).toEntity(id);
      Mockito.verify(stimmzettelerfassungTeamStatusRepository).findById(entityID);
      Mockito.verify(teamErfassungStatusModelMapper, Mockito.never())
          .toModel(Mockito.any(TeamErfassungStatus.class));
    }

    @Test
    void should_throwException_when_validationOfIdFailed() {
      val id = Instancio.create(TeamBezirkUndWahlIDModel.class);
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("mocked wls exception");

      Mockito.doThrow(mockedWlsException).when(teamErfassungStatusValidator).isValidOrThrow(id);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getTeamStatus(id))
          .isEqualTo(mockedWlsException);

      Mockito.verify(teamErfassungStatusValidator).isValidOrThrow(id);
      Mockito.verifyNoInteractions(teamErfassungStatusModelMapper);
      Mockito.verifyNoInteractions(stimmzettelerfassungTeamStatusRepository);
    }
  }
}
