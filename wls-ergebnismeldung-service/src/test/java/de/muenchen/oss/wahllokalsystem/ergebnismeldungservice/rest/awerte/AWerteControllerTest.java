package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AWerteControllerTest {

    @Mock
    AWerteService awerteService;

    @Mock
    AWerteDTOMapper awerteDTOMapper;

    @InjectMocks
    AWerteController unitUnderTest;

    @Nested
    class GetAWerte {

        @Test
        void should_returnHttp200AndData_when_dataIsFound() {
            val wahlbezirkID = "wahlbezirkID01";
            val mockedServiceModel = List.of(new AWerteModel(null, 0, null));
            val mockedMappedServiceDTO = List.of(new AWerteDTO(new BezirkUndWahlID(null, null), 0, null));

            Mockito.when(awerteService.getAWerte(wahlbezirkID)).thenReturn(mockedServiceModel);
            Mockito.when(awerteDTOMapper.fromListOfAWerteModelToListOfAWerteDTO(mockedServiceModel)).thenReturn(mockedMappedServiceDTO);

            val result = unitUnderTest.getAWerte(wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isSameAs(mockedMappedServiceDTO);
        }

        @Test
        void should_returnHttp204AndBodyIsNull_when_serviceReturnsNull() {
            val wahlbezirkID = "wahlbezirkID01";
            val mockedServiceModel = List.of(new AWerteModel(null, 0, null));

            Mockito.when(awerteService.getAWerte(wahlbezirkID)).thenReturn(mockedServiceModel);
            Mockito.when(awerteDTOMapper.fromListOfAWerteModelToListOfAWerteDTO(mockedServiceModel)).thenReturn(null);

            val result = unitUnderTest.getAWerte(wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

    @Nested
    class InitialiseAWerte {

        @Test
        void should_notThrowException_when_serviceIsCalled() {
            val requestBody = List.of("wahlbezirkID1", "wahlbezirkID2", "wahlbezirkID3");

            unitUnderTest.initialiseAWerte(requestBody);

            Mockito.verify(awerteService).initialiseAWerte(requestBody);
        }
    }
}
