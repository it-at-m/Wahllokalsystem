package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class UnterbrechungsUhrzeitControllerTest {

    @Mock
    UnterbrechungsUhrzeitService unterbrechungsUhrzeitService;

    @Mock
    UnterbrechungsUhrzeitDTOMapper unterbrechungsUhrzeitDTOMapper;

    @InjectMocks
    UnterbrechungsUhrzeitController unitUnderTest;

    @Test
    void getDataFromService() {
        val wahlbezirkID = "wahlbezirkID";
        val unterbrechungsUhrzeit = LocalDateTime.now();

        val mockedServiceOptionalBody = new UnterbrechungsUhrzeitModel(wahlbezirkID, unterbrechungsUhrzeit);
        val mockedMappedServiceResponseAsDTO = new UnterbrechungsUhrzeitDTO(wahlbezirkID, unterbrechungsUhrzeit);

        Mockito.when(unterbrechungsUhrzeitService.getUnterbrechungsUhrzeit(wahlbezirkID)).thenReturn(Optional.of(mockedServiceOptionalBody));
        Mockito.when(unterbrechungsUhrzeitDTOMapper.toDTO(mockedServiceOptionalBody)).thenReturn(mockedMappedServiceResponseAsDTO);

        val result = unitUnderTest.getUnterbrechungsuhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceResponseAsDTO);
    }

    @Test
    void gotNoDataFromService() {
        val wahlbezirkID = "wahlbezirkID";

        Mockito.when(unterbrechungsUhrzeitService.getUnterbrechungsUhrzeit(wahlbezirkID)).thenReturn(Optional.empty());

        val result = unitUnderTest.getUnterbrechungsuhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(result.getBody()).isNull();
    }

    @Test
    void requestIsMappedAndSendToService() {
        val wahlbezirkID = "wahlbezirkID";
        val unterbrechungsUhrzeit = LocalDateTime.now();
        val requestBody = new UnterbrechungsUhrzeitWriteDTO(unterbrechungsUhrzeit);

        val mockedMappedRequest = new UnterbrechungsUhrzeitModel(wahlbezirkID, unterbrechungsUhrzeit);

        Mockito.when(unterbrechungsUhrzeitDTOMapper.toModel(eq(wahlbezirkID), eq(requestBody))).thenReturn(mockedMappedRequest);

        Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postUnterbrechungsuhrzeit(wahlbezirkID, requestBody));
        Mockito.verify(unterbrechungsUhrzeitService).setUnterbrechungsUhrzeit(mockedMappedRequest);
    }

}
