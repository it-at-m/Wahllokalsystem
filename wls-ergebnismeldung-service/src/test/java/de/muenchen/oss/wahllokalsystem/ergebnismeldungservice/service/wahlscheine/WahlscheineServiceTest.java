package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.Wahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
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
class WahlscheineServiceTest {

    @Mock
    WahlscheineRepository wahlscheineRepository;

    @Mock
    WahlscheineModelMapper wahlscheineModelMapper;

    @Mock
    WahlscheineValidator wahlscheineValidator;

    @Mock
    ExceptionFactory exceptionFactory;



    @InjectMocks
    WahlscheineService unitUnderTest;

    @Nested
    class GetWahlscheine {

        @Test
        void should_submitFachlicheWlsExceptionForParameter_when_callingValidator() {
            val id = new BezirkUndWahlID();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            unitUnderTest.getWahlscheine(id);

            Mockito.verify(wahlscheineValidator).validBezirkUndWahlIdOrThrow(eq(id), eq(mockedWlsException));
        }

        @Test
        void should_returnWahlscheineModel_when_wahlscheineIsFoundFromRepo() {
            val id = new BezirkUndWahlID();

            val mockedEntity = new Wahlscheine();
            val mockedMappedEntityAsModel = new WahlscheineModel(id, null);

            Mockito.when(wahlscheineRepository.findById(id)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(wahlscheineModelMapper.toModel(mockedEntity)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getWahlscheine(id);

            Assertions.assertThat(result).isEqualTo(Optional.of(mockedMappedEntityAsModel));
        }

        @Test
        void should_returnEmptyOptional_when_wahlscheineIsNotFoundFromRepo() {
            val id = new BezirkUndWahlID();

            Mockito.when(wahlscheineRepository.findById(id)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWahlscheine(id);

            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class SetWahlscheine {

        @Test
        void should_submitFachlicheWlsExceptionForParameter_when_callingValidator() {
            val id = new BezirkUndWahlID();
            val wahlscheineToSet = createWahlscheineModel(id);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            unitUnderTest.setWahlscheine(id, wahlscheineToSet);

            Mockito.verify(wahlscheineValidator).validBezirkUndWahlIdOrThrow(id, mockedWlsException);
        }

        @Test
        void should_validateWahlscheineModel_when_called() {
            val id = new BezirkUndWahlID();
            val wahlscheineToSet = createWahlscheineModel(id);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            unitUnderTest.setWahlscheine(id, wahlscheineToSet);

            Mockito.verify(wahlscheineValidator).validWahlscheineOrThrow(wahlscheineToSet);
        }

        @Test
        void should_saveMappedWahlscheineModel_when_called() {
            val id = new BezirkUndWahlID();
            val wahlscheineToSet = createWahlscheineModel(id);

            val mockedModelAsEntity = Mockito.mock(Wahlscheine.class);

            Mockito.when(wahlscheineModelMapper.toEntity(wahlscheineToSet)).thenReturn(mockedModelAsEntity);

            unitUnderTest.setWahlscheine(id, wahlscheineToSet);

            Mockito.verify(wahlscheineRepository).save(mockedModelAsEntity);
        }

        @Test
        void should_throwTechnischeWlsException_when_savingFailed() {
            val id = new BezirkUndWahlID();
            val wahlscheineToSet = createWahlscheineModel(id);

            val mockedModelAsEntity = Mockito.mock(Wahlscheine.class);
            val mockedRepositorySaveException = new RuntimeException("saving failed");
            val mockedExceptionFactoryWlsException = TechnischeWlsException.withCode("").buildWithMessage("save exception");

            Mockito.when(wahlscheineModelMapper.toEntity(wahlscheineToSet)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedRepositorySaveException).when(wahlscheineRepository).save(mockedModelAsEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.WAHLSCHEINE_UNSAVEABLE)).thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setWahlscheine(id, wahlscheineToSet)).isSameAs(mockedExceptionFactoryWlsException);
        }

        private WahlscheineModel createWahlscheineModel(final BezirkUndWahlID id) {
            return new WahlscheineModel(id, null);
        }
    }
}
