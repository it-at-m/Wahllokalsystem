package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlenService;
import java.util.Collections;
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
class WahlenControllerTest {

    @Mock
    WahlenService wahlenService;

    @Mock
    WahlDTOMapper wahlDTOMapper;

    @InjectMocks
    WahlenController unitUnderTest;

    @Nested
    class GetWahlen {

        @Test
        void should_returnDtoWithHttpStatusOk_when_serviceReturnedData() {
            val wahltagID = "wahltagID";

            val mockedServiceResponse = List.of(Mockito.mock(WahlModel.class), Mockito.mock(WahlModel.class));
            val mockedServiceResponseAsDTO = List.of(Mockito.mock(WahlDTO.class), Mockito.mock(WahlDTO.class));

            Mockito.when(wahlenService.getWahlen(wahltagID)).thenReturn(mockedServiceResponse);
            Mockito.when(wahlDTOMapper.toDtoList(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getWahlen(wahltagID);

            Assertions.assertThat(result.getBody()).isSameAs(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyBodyWithHttpStatusNoContent_when_serviceReturnedNull() {
            val wahltagID = "wahltagID";

            Mockito.when(wahlenService.getWahlen(wahltagID)).thenReturn(null);
            Mockito.when(wahlDTOMapper.toDtoList(null)).thenReturn(null);

            val result = unitUnderTest.getWahlen(wahltagID);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void should_returnEmptyBodyWithHttpStatusNoContent_when_serviceReturnedEmptyList() {
            val wahltagID = "wahltagID";

            Mockito.when(wahlenService.getWahlen(wahltagID)).thenReturn(Collections.emptyList());
            Mockito.when(wahlDTOMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getWahlen(wahltagID);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class UpdateWahlen {

        @Test
        void should_returnHttpStatusOkAndCallService_when_dtoIsGiven() {
            val wahltagID = "wahltagID";
            val mockedRequestDto = List.of(Mockito.mock(WahlDTO.class));
            val mockedMappedRequest = List.of(Mockito.mock(WahlModel.class));

            Mockito.when(wahlDTOMapper.toModelList(mockedRequestDto)).thenReturn(mockedMappedRequest);

            unitUnderTest.updateWahlen(mockedRequestDto, wahltagID);

            Mockito.verify(wahlenService).updateWahlen(mockedMappedRequest, wahltagID);
        }
    }
}
