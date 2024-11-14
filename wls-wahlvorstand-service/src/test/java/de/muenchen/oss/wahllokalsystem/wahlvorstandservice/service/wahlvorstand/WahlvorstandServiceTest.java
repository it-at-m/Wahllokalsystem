package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
public class WahlvorstandServiceTest {

    @Mock
    WahlvorstandEaiClient wahlvorstandEaiClient;

    @Mock
    KonfigurierterWahltagClient konfigurierterWahltagClient;

    @Mock
    WahlvorstandRepository wahlvorstandRepository;

    @Mock
    WahlvorstandModelMapper wahlvorstandModelMapper;

    @Mock
    WahlvorstandValidator wahlvorstandValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlvorstandService unitUnderTest;

    @Nested
    class GetWahlvorstand {

        @Test
        void should_returnWahlvorstandModel_when_givenValidWahlbezirkID() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedWahlvorstand = TestDataFactory.CreateWahlvorstandEntity.withData();
            val expectedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fromEntity(mockedWahlvorstand);

            Mockito.when(wahlvorstandRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedWahlvorstand));
            Mockito.when(wahlvorstandModelMapper.toModel(mockedWahlvorstand)).thenReturn(expectedWahlvorstandModel);

            val result = unitUnderTest.getWahlvorstand(wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(Optional.of(expectedWahlvorstandModel));

            Mockito.verify(wahlvorstandValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
        }

        @Test
        void should_returnNull_when_noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(wahlvorstandRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWahlvorstand(wahlbezirkID);
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class UpdateWahlvorstand {

        @Test
        void should_updateWahlvorstandAndReturnWahlvorstandModel_when_givenValidWahlbezirkID() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedKonfigurierterWahltagFromClient = TestDataFactory.CreateFromClient.konfigurierterWahltagModel();
            val mockedWahlvorstandModelFromClient = TestDataFactory.CreateFromClient.wahlvorstandModel(wahlbezirkID);
            val expectedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();
            Mockito.when(konfigurierterWahltagClient.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltagFromClient);
            Mockito.when(wahlvorstandEaiClient.getWahlvorstand(wahlbezirkID, mockedKonfigurierterWahltagFromClient.wahltag()))
                    .thenReturn(mockedWahlvorstandModelFromClient);

            val mockedWahlvorstand = TestDataFactory.CreateWahlvorstandEntity.withData();
            Mockito.when(wahlvorstandRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedWahlvorstand));
            Mockito.when(wahlvorstandModelMapper.toEntity(mockedWahlvorstandModelFromClient)).thenReturn(mockedWahlvorstand);

            val result = unitUnderTest.updateWahlvorstand(wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(Optional.of(expectedWahlvorstandModel));

            Mockito.verify(wahlvorstandValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.verify(wahlvorstandRepository).save(mockedWahlvorstand);
        }
    }

    @Nested
    class PostWahlvorstand {

        @Test
        void should_notThrowException_when_newDataIsSaved() {
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();
            val mockedWahlvorstandFromModel = TestDataFactory.CreateWahlvorstandEntity.fromModel(mockedWahlvorstandModel);

            Mockito.when(wahlvorstandModelMapper.toEntity(mockedWahlvorstandModel)).thenReturn(mockedWahlvorstandFromModel);
            Mockito.doNothing().when(wahlvorstandEaiClient).postWahlvorstand(mockedWahlvorstandModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postWahlvorstand(mockedWahlvorstandModel));

            Mockito.verify(wahlvorstandValidator).validWahlvorstandOrThrow(mockedWahlvorstandModel);
            Mockito.verify(wahlvorstandRepository).save(mockedWahlvorstandFromModel);
        }

        @Test
        void should_throwWlsException_when_savingFailed() {
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();
            val mockedWahlvorstandFromModel = TestDataFactory.CreateWahlvorstandEntity.fromModel(mockedWahlvorstandModel);
            val mockedRepoSaveException = new RuntimeException("saving failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahlvorstandModelMapper.toEntity(mockedWahlvorstandModel)).thenReturn(mockedWahlvorstandFromModel);
            Mockito.doThrow(mockedRepoSaveException).when(wahlvorstandRepository).save(mockedWahlvorstandFromModel);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTWAHLVORSTAND_NOT_SAVEABLE)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahlvorstand(mockedWahlvorstandModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_notSaveWahlvorstand_when_givenWahlvorstandWithFallbackData() {
            val mockedWahlvorstandFallbackModel = TestDataFactory.CreateWahlvorstandModel.fallback("wahlbezirkID");

            unitUnderTest.postWahlvorstand(mockedWahlvorstandFallbackModel);
            Mockito.verify(wahlvorstandRepository, Mockito.never()).save(wahlvorstandModelMapper.toEntity(mockedWahlvorstandFallbackModel));
        }
    }
}
