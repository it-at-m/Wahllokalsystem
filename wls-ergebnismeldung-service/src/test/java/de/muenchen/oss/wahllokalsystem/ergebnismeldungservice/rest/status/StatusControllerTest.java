package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
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
class StatusControllerTest {

    @Mock
    StatusService statusService;

    @Mock
    StatusDTOMapper statusDTOMapper;

    @InjectMocks
    StatusController unitUnderTest;

    @Nested
    class GetStatus {

        @Test
        void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");
            val mockedServiceResponse = new StatusModel(bezirkUndWahlID, null, null);
            val mockedServiceResponseAsDTO = new StatusDTO(bezirkUndWahlID, null, null);

            Mockito.when(statusService.getStatus(bezirkUndWahlID)).thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(statusDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getStatus(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Mockito.when(statusService.getStatus(bezirkUndWahlID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getStatus(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class SetStatus {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val statusDTO = new StatusDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), null, null);

            val mockedStatusModel = new StatusModel(new BezirkUndWahlID(wahlID, wahlbezirkID), null, null);
            Mockito.when(statusDTOMapper.toModel(statusDTO)).thenReturn(mockedStatusModel);

            unitUnderTest.setStatus(wahlID, wahlbezirkID, statusDTO);

            Mockito.verify(statusService).setStatus(eq(new BezirkUndWahlID(wahlID, wahlbezirkID)), eq(mockedStatusModel));
        }
    }
}
