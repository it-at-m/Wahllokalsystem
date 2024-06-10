package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten.WahlbriefdatenModel;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten.WahlbriefdatenService;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class WahlbriefdatenControllerTest {

    @Mock
    WahlbriefdatenService wahlbriefdatenService;

    @Mock
    WahlbriefdatenDTOMapper wahlbriefdatenDTOMapper;

    @InjectMocks
    WahlbriefdatenController unitUnderTest;

    @Nested
    class GetWahlbriefdaten {

        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedServiceResponse = new WahlbriefdatenModel(wahlbezirkID, null, null, null, null, null);
            val mockedServiceResponseAsDTO = WahlbriefdatenDTO.builder().build();
            Mockito.when(wahlbriefdatenService.getWahlbriefdaten(wahlbezirkID)).thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(wahlbriefdatenDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getWahlbriefdaten(wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(wahlbriefdatenService.getWahlbriefdaten(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWahlbriefdaten(wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

    @Nested
    class PostWahlbriefdaten {

        @Test
        void requestIsMappedAndSend() {
            val requestBody = new WahlbriefdatenWriteDTO(null, null, null, null, null);
            val wahlbezirkID = "wahlbezirkID";

            val mockedServiceRequest = WahlbriefdatenModel.builder().build();

            Mockito.when(wahlbriefdatenDTOMapper.toModel(eq(wahlbezirkID), eq(requestBody))).thenReturn(mockedServiceRequest);
            Mockito.doNothing().when(wahlbriefdatenService).setWahlbriefdaten(mockedServiceRequest);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postWahlbriefdaten(wahlbezirkID, requestBody));
        }
    }

}
