package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeService;
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
class StimmzettelumschlaegeControllerTest {

    @Mock
    StimmzettelumschlaegeService stimmzettelumschlaegeService;

    @Mock
    StimmzettelumschlaegeDTOMapper stimmzettelumschlaegeDTOMapper;

    @InjectMocks
    StimmzettelumschlaegeController unitUnderTest;

    @Nested
    class GetStimmzettelumschlaege {

        @Test
        void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val mockedServiceResponse = new StimmzettelumschlaegeModel(bezirkUndWahlID, null, 0, 0);
            val mockedServiceResponseAsDTO = new StimmzettelumschlaegeDTO(bezirkUndWahlID, null, 0, 0L);

            Mockito.when(stimmzettelumschlaegeService.getStimmzettelumschlaege(bezirkUndWahlID)).thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(stimmzettelumschlaegeDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getStimmzettelumschlaege(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Mockito.when(stimmzettelumschlaegeService.getStimmzettelumschlaege(bezirkUndWahlID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getStimmzettelumschlaege(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class PostStimmzettelumschlaege {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stimmzettelumschlaegeDTO = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), null, 0, 0L);

            val mockedStimmzettelumschlaegeModel = new StimmzettelumschlaegeModel(new BezirkUndWahlID(wahlID, wahlbezirkID), null, 0, 0);
            Mockito.when(stimmzettelumschlaegeDTOMapper.toModel(stimmzettelumschlaegeDTO)).thenReturn(mockedStimmzettelumschlaegeModel);

            unitUnderTest.postStimmzettelumschlaege(wahlID, wahlbezirkID, stimmzettelumschlaegeDTO);

            Mockito.verify(stimmzettelumschlaegeService).setStimmzettelumschlaege(eq(new BezirkUndWahlID(wahlID, wahlbezirkID)),
                    eq(mockedStimmzettelumschlaegeModel));
        }
    }
}
