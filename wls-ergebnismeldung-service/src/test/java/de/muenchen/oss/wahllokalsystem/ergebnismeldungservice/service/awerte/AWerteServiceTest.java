package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.LoggerExtension;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AWerteServiceTest {

    @Mock
    AWerteRepository aWerteRepository;

    @Mock
    AWerteValidator aWerteValidator;

    @Mock
    AWerteModelMapper aWerteModelMapper;

    @Mock
    AWerteClient aWerteClient;

    @Mock
    ExceptionFactory exceptionFactory;

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @InjectMocks
    AWerteService unitUnderTest;

    @Nested
    class GetAWerte {

        @Test
        void should_returnEaiData_when_eaiDataFound() {
            val wahlbezirkID = "wahlbezirkID1";
            val aWerteModelListFromClient = createListOfAWerteModels(wahlbezirkID);

            Mockito.when(aWerteClient.getAWerte(wahlbezirkID)).thenReturn(aWerteModelListFromClient);

            val result = unitUnderTest.getAWerte(wahlbezirkID);

            Mockito.verify(aWerteValidator, times(1)).validWahlbezirkIDParamOrThrow(Mockito.any());
            Mockito.verify(aWerteModelMapper, times(1)).fromListOfAWerteModelToListOfAWerteEntity(Mockito.any());

            Assertions.assertThat(result).isEqualTo(aWerteModelListFromClient);
        }

        @Test
        void should_saveEaiDataInRepo_when_eaiDataFound() {
            val wahlbezirkID = "wahlbezirkID1";
            val aWerteModelListFromClient = createListOfAWerteModels(wahlbezirkID);
            val mappedAWerteEntityList = aWerteModelMapper.fromListOfAWerteModelToListOfAWerteEntity(aWerteModelListFromClient);

            Mockito.when(aWerteClient.getAWerte(wahlbezirkID)).thenReturn(aWerteModelListFromClient);

            unitUnderTest.getAWerte(wahlbezirkID);

            Assertions.assertThatCode(() -> unitUnderTest.getAWerte(wahlbezirkID)).doesNotThrowAnyException();
            val savedAWerteEntitiesList = aWerteRepository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID);
            Assertions.assertThat(savedAWerteEntitiesList).isEqualTo(mappedAWerteEntityList);
        }

        @Test
        void should_retrieveOldAWerteFromRepo_when_noEaiDataFound() {
            val wahlbezirkID = "wahlbezirkID1";
            val aWerteEntityList = createListOfAWerteEntities(wahlbezirkID);
            val aWerteModelList = createListOfAWerteModels(wahlbezirkID);

            Mockito.when(aWerteClient.getAWerte(wahlbezirkID)).thenReturn(null);
            Mockito.when(aWerteRepository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID)).thenReturn(aWerteEntityList);
            Mockito.when(aWerteModelMapper.fromListOfAWerteEntityToListOfAWerteModel(aWerteEntityList)).thenReturn(aWerteModelList);

            val result = unitUnderTest.getAWerte(wahlbezirkID);

            Mockito.verify(aWerteRepository).findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(aWerteModelList);
        }

        @Test
        void should_throwTechnischeWlsException_when_noEaiDataAndNoRepositoryDataFound() {
            val wahlbezirkID = "wahlbezirkID1";
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(aWerteClient.getAWerte(wahlbezirkID)).thenReturn(null);
            Mockito.when(aWerteRepository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID)).thenReturn(null);
            Mockito.when(aWerteModelMapper.fromListOfAWerteEntityToListOfAWerteModel(null)).thenReturn(null);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.GETAWERTE_UNSAVEABLE))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getAWerte(wahlbezirkID)).isSameAs(mockedWlsException);
        }
    }

    private List<AWerte> createListOfAWerteEntities(String wahlbezirkID) {
        val aWert1 = new AWerte(new BezirkUndWahlID("wahlID1", wahlbezirkID), 2, 3L);
        val aWert2 = new AWerte(new BezirkUndWahlID("wahlID2", wahlbezirkID), 4, 5L);
        val aWert3 = new AWerte(new BezirkUndWahlID("wahlID3", wahlbezirkID), 5, 6L);
        return List.of(aWert1, aWert2, aWert3);
    }

    private List<AWerteModel> createListOfAWerteModels(String wahlbezirkID) {
        val aWert1 = new AWerteModel(new BezirkUndWahlID("wahlID1", wahlbezirkID), 2, 3L);
        val aWert2 = new AWerteModel(new BezirkUndWahlID("wahlID2", wahlbezirkID), 4, 5L);
        val aWert3 = new AWerteModel(new BezirkUndWahlID("wahlID3", wahlbezirkID), 5, 6L);
        return List.of(aWert1, aWert2, aWert3);
    }

}
