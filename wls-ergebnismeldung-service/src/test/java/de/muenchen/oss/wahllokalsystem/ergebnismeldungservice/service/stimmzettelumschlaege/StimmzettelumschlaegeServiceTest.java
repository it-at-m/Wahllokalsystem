package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
class StimmzettelumschlaegeServiceTest {

    @Mock
    StimmzettelumschlaegeRepository stimmzettelumschlaegeRepository;

    @Mock
    StimmzettelumschlaegeModelMapper stimmzettelumschlaegeModelMapper;

    @Mock
    StimmzettelumschlaegeValidator stimmzettelumschlaegeValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    StimmzettelumschlaegeService unitUnderTest;

    @Nested
    class GetStimmzettelumschlaege {

        @Test
        void should_submitFachlicheWlsExceptionForParameter_when_callingValidator() {
            val id = new BezirkUndWahlID();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            unitUnderTest.getStimmzettelumschlaege(id);

            Mockito.verify(stimmzettelumschlaegeValidator).validBezirkUndWahlIdOrThrow(eq(id), eq(mockedWlsException));
        }

        @Test
        void should_returnStimmzettelumschlaegeModel_when_stimmzettelumschlaegeIsFoundFromRepo() {
            val id = new BezirkUndWahlID();

            val mockedEntity = new Stimmzettelumschlaege();
            val mockedMappedEntityAsModel = new StimmzettelumschlaegeModel(id, null, 0, 0);

            Mockito.when(stimmzettelumschlaegeRepository.findById(id)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(stimmzettelumschlaegeModelMapper.toModel(mockedEntity)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getStimmzettelumschlaege(id);

            Assertions.assertThat(result).isEqualTo(Optional.of(mockedMappedEntityAsModel));
        }

        @Test
        void should_returnEmptyOptional_when_stimmzettelumschlaegeIsNotFoundFromRepo() {
            val id = new BezirkUndWahlID();

            Mockito.when(stimmzettelumschlaegeRepository.findById(id)).thenReturn(Optional.empty());

            val result = unitUnderTest.getStimmzettelumschlaege(id);

            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class SetStimmzettelumschlaege {

        @Test
        void should_submitFachlicheWlsExceptionForParameter_when_callingValidator() {
            val id = new BezirkUndWahlID();
            val stimmzettelumschlaegeToSet = createStimmzettelumschlaegeModel(id);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaegeToSet);

            Mockito.verify(stimmzettelumschlaegeValidator).validBezirkUndWahlIdOrThrow(id, mockedWlsException);
        }

        @Test
        void should_validateStimmzettelumschlaegeModel_when_called() {
            val id = new BezirkUndWahlID();
            val stimmzettelumschlaegeToSet = createStimmzettelumschlaegeModel(id);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaegeToSet);

            Mockito.verify(stimmzettelumschlaegeValidator).validStimmzettelumschlaegeOrThrow(stimmzettelumschlaegeToSet);
        }

        @Test
        void should_saveMappedStimmzettelumschlaegeModel_when_called() {
            val id = new BezirkUndWahlID();
            val stimmzettelumschlaegeToSet = createStimmzettelumschlaegeModel(id);

            val mockedModelAsEntity = Mockito.mock(Stimmzettelumschlaege.class);

            Mockito.when(stimmzettelumschlaegeModelMapper.toEntity(stimmzettelumschlaegeToSet)).thenReturn(mockedModelAsEntity);

            unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaegeToSet);

            Mockito.verify(stimmzettelumschlaegeRepository).save(mockedModelAsEntity);
        }

        @Test
        void should_throwTechnischeWlsException_when_savingFailed() {
            val id = new BezirkUndWahlID();
            val stimmzettelumschlaegeToSet = createStimmzettelumschlaegeModel(id);

            val mockedModelAsEntity = Mockito.mock(Stimmzettelumschlaege.class);
            val mockedRepositorySaveException = new RuntimeException("saving failed");
            val mockedExceptionFactoryWlsException = TechnischeWlsException.withCode("").buildWithMessage("save exception");

            Mockito.when(stimmzettelumschlaegeModelMapper.toEntity(stimmzettelumschlaegeToSet)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedRepositorySaveException).when(stimmzettelumschlaegeRepository).save(mockedModelAsEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.STIMMZETTELUMSCHLAEGE_UNSAVEABLE))
                    .thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaegeToSet)).isSameAs(mockedExceptionFactoryWlsException);
        }

        private StimmzettelumschlaegeModel createStimmzettelumschlaegeModel(final BezirkUndWahlID id) {
            return new StimmzettelumschlaegeModel(id, null, 0, 0);
        }
    }
}
