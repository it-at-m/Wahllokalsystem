package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltage.WahltageService;
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
class WahltageControllerTest {

    @Mock
    WahltageService wahltageService;

    @Mock
    WahltagDTOMapper wahltageDTOMapper;

    @InjectMocks
    WahltageController unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void should_returnDtoWithHttpStatusOk_when_serviceReturnedData() {
            val mockedServiceResponse = List.of(Mockito.mock(WahltagModel.class), Mockito.mock(WahltagModel.class));
            val mockedServiceResponseAsDTO = List.of(Mockito.mock(WahltagDTO.class), Mockito.mock(WahltagDTO.class));

            Mockito.when(wahltageService.getWahltage()).thenReturn(mockedServiceResponse);
            Mockito.when(wahltageDTOMapper.toDtoList(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getWahltage();

            Assertions.assertThat(result.getBody()).isSameAs(mockedServiceResponseAsDTO);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnEmptyBodyWithHttpStatusNoContent_when_serviceReturnedNull() {
            Mockito.when(wahltageService.getWahltage()).thenReturn(null);
            Mockito.when(wahltageDTOMapper.toDtoList(null)).thenReturn(null);

            val result = unitUnderTest.getWahltage();

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        void should_returnEmptyBodyWithHttpStatusNoContent_when_serviceReturnedEmptyList() {
            Mockito.when(wahltageService.getWahltage()).thenReturn(Collections.emptyList());
            Mockito.when(wahltageDTOMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getWahltage();

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }
}
