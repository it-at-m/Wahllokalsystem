package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.Waehleranzahl;
import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.WaehleranzahlRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
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
public class WaehleranzahlServiceTest {

    @Mock
    WaehleranzahlRepository waehleranzahlRepository;
    @Mock
    WaehleranzahlModelMapper waehleranzahlModelMapper;
    @Mock
    WaehleranzahlValidator waehleranzahlValidator;
    @Mock
    WaehleranzahlClient waehleranzahlClient;
    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WaehleranzahlService unitUnderTest;

    @Nested
    class GetWahlbeteiligung {

        @Test
        void should_returnRepoData_when_RepoDataFound() {
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID("wahlID01", "wahlbezirkID01");

            val mockedRepoResponse = new Waehleranzahl();
            val mockedMappedRepoResponse = new WaehleranzahlModel(bezirkUndWahlID, 99L, LocalDateTime.now());
            Mockito.doNothing().when(waehleranzahlValidator).validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);

            Mockito.when(waehleranzahlRepository.findById(bezirkUndWahlID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(waehleranzahlModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponse);

            Assertions.assertThat(unitUnderTest.getWahlbeteiligung(bezirkUndWahlID).get()).isSameAs(mockedMappedRepoResponse);
        }

        @Test
        void should_returnEmptyResult_when_RepoDataNotFound() {
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID("wahlID01", "wahlbezirkID01");

            Mockito.when(waehleranzahlRepository.findById(bezirkUndWahlID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWahlbeteiligung(bezirkUndWahlID);
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class PostWahlbeteiligung {

        @Test
        void should_notThrowExceptionAndSaveDataInRepo_when_ValidModelIsGiven() {

            val waehleranzahlSetModel = WaehleranzahlModel.builder().build();
            val mockedKonfigurationEntity = new Waehleranzahl();

            Mockito.doNothing().when(waehleranzahlValidator).validWaehleranzahlSetModel(waehleranzahlSetModel);
            Mockito.when(waehleranzahlModelMapper.toEntity(waehleranzahlSetModel)).thenReturn(mockedKonfigurationEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postWahlbeteiligung(waehleranzahlSetModel));

            Mockito.verify(waehleranzahlRepository).save(mockedKonfigurationEntity);
        }

        @Test
        void should_throwUnhandledExceptionFromValidation_when_validationFails() {
            val waehleranzahlSetModel = WaehleranzahlModel.builder().build();

            val mockedValidatorException = new IllegalArgumentException("WRONG!!!");

            Mockito.doThrow(mockedValidatorException).when(waehleranzahlValidator).validWaehleranzahlSetModel(waehleranzahlSetModel);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahlbeteiligung(waehleranzahlSetModel)).isSameAs(mockedValidatorException);
        }
    }
}
