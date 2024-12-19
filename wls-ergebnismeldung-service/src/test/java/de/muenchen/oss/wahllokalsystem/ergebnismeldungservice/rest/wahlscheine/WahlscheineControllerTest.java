package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineService;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class WahlscheineControllerTest {

    @Mock
    WahlscheineService wahlscheineService;

    @Mock
    WahlscheineDTOMapper wahlscheineDTOMapper;

    @InjectMocks
    WahlscheineController unitUnderTest;

    @Nested
    class GetWahlscheine {

        @Test
        void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val mockedServiceResponse = new WahlscheineModel(bezirkUndWahlID, null);
            val mockedServiceResponseAsDTO = new WahlscheineDTO(bezirkUndWahlID, null);

            Mockito.when(wahlscheineService.getWahlscheine(bezirkUndWahlID)).thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(wahlscheineDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getWahlscheine(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Mockito.when(wahlscheineService.getWahlscheine(bezirkUndWahlID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getWahlscheine(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class PostWahlscheine {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val wahlscheineDTO = new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), null);

            val mockedWahlscheineModel = new WahlscheineModel(new BezirkUndWahlID(wahlID, wahlbezirkID), null);
            Mockito.when(wahlscheineDTOMapper.toModel(wahlscheineDTO)).thenReturn(mockedWahlscheineModel);

            unitUnderTest.postWahlscheine(wahlID, wahlbezirkID, wahlscheineDTO);

            Mockito.verify(wahlscheineService).setWahlscheine(eq(new BezirkUndWahlID(wahlID, wahlbezirkID)), eq(mockedWahlscheineModel));
        }
    }
}
