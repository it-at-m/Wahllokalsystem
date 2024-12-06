package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.AbstractStatusMonitoringSender;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    StatusRepository statusRepository;

    @Mock
    StatusModelMapper statusModelMapper;

    @Mock
    StatusValidator statusValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    List<AbstractStatusMonitoringSender> monitoringSenders;

    @InjectMocks
    StatusService unitUnderTest;

    @Nested
    class GetStatus {

        @Test
        void should_submitFachlichWlsExceptionForParameter_when_callingValidator() {
            val id = new BezirkUndWahlID();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_STATUS_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            unitUnderTest.getStatus(id);

            Mockito.verify(statusValidator).validBezirkUndWahlIdOrThrow(eq(id), eq(mockedWlsException));
        }

        @Test
        void should_returnStatusModel_when_statusIsFoundFromRepo() {
            val id = new BezirkUndWahlID();

            val mockedEntity = new Status();
            val mockedMappedEntityAsModel = new StatusModel(id, new MeldungModel(ValidierungsstatusModel.VALIDE, false, false, LocalDateTime.now()),
                    new MeldungModel(ValidierungsstatusModel.INVALIDE, true, true, LocalDateTime.now()));

            Mockito.when(statusRepository.findById(id)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(statusModelMapper.toModel(mockedEntity)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getStatus(id);

            Assertions.assertThat(result).isEqualTo(Optional.of(mockedMappedEntityAsModel));
        }

        @Test
        void should_returnEmptyOptional_when_statusIsNotFoundFromRepo() {
            val id = new BezirkUndWahlID();

            Mockito.when(statusRepository.findById(id)).thenReturn(Optional.empty());

            val result = unitUnderTest.getStatus(id);

            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class SetStatus {

        @Test
        void should_submitFachlichWlsExceptionForParameter_when_callingValidator() {
            val id = new BezirkUndWahlID();
            val statusToSet = createStatusModel(id);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            unitUnderTest.setStatus(id, statusToSet);

            Mockito.verify(statusValidator).validBezirkUndWahlIdOrThrow(id, mockedWlsException);
        }

        @Test
        void should_validateStatusModel_when_called() {
            val id = new BezirkUndWahlID();
            val statusToSet = createStatusModel(id);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            unitUnderTest.setStatus(id, statusToSet);

            Mockito.verify(statusValidator).validStatusOrThrow(statusToSet);
        }

        @Test
        void should_callSendersWithStatusAndStatusFromRepo_when_called() {
            val id = new BezirkUndWahlID();
            val statusToSet = createStatusModel(id);

            val mockedStatusEntity = new Status();
            val mockedStatusEntityAsModel = Mockito.mock(StatusModel.class);
            val mockedSender = Mockito.mock(AbstractStatusMonitoringSender.class);
            val mockIterator = Mockito.mock(Iterator.class);

            Mockito.doCallRealMethod().when(monitoringSenders).forEach(Mockito.any());
            Mockito.when(monitoringSenders.iterator()).thenReturn(mockIterator);
            Mockito.when(mockIterator.hasNext()).thenReturn(true, false);
            Mockito.when(mockIterator.next()).thenReturn(mockedSender);
            Mockito.when(statusRepository.findById(id)).thenReturn(Optional.of(mockedStatusEntity));
            Mockito.when(statusModelMapper.toModel(mockedStatusEntity)).thenReturn(mockedStatusEntityAsModel);

            unitUnderTest.setStatus(id, statusToSet);

            Mockito.verify(mockedSender).submitStatus(eq(id), eq(statusToSet), eq(mockedStatusEntityAsModel));
        }

        @Test
        void should_saveMappedStatusModel_when_called() {
            val id = new BezirkUndWahlID();
            val statusToSet = createStatusModel(id);

            val mockedModelAsEntity = Mockito.mock(Status.class);

            Mockito.when(statusModelMapper.toEntity(statusToSet)).thenReturn(mockedModelAsEntity);

            unitUnderTest.setStatus(id, statusToSet);

            Mockito.verify(statusRepository).save(mockedModelAsEntity);
        }

        @Test
        void should_throwTechnischeWlsException_when_savingFailed() {
            val id = new BezirkUndWahlID();
            val statusToSet = createStatusModel(id);

            val mockedModelAsEntity = Mockito.mock(Status.class);
            val mockedRepositorySaveException = new RuntimeException("saving failed");
            val mockedExceptionFactoryWlsException = TechnischeWlsException.withCode("").buildWithMessage("save exception");

            Mockito.when(statusModelMapper.toEntity(statusToSet)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedRepositorySaveException).when(statusRepository).save(mockedModelAsEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.STATUS_UNSAVEABLE)).thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setStatus(id, statusToSet)).isSameAs(mockedExceptionFactoryWlsException);
        }

        private StatusModel createStatusModel(final BezirkUndWahlID id) {
            return new StatusModel(id, null, null);
        }
    }

}
