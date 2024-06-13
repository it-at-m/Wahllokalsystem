package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitService;
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
public class FortsetzungsUhrzeitControllerTest {

    @Mock
    FortsetzungsUhrzeitService fortsetzungsUhrzeitService;

    @Mock
    FortsetzungsUhrzeitDTOMapper fortsetzungsUhrzeitDTOMapper;

    @InjectMocks
    FortsetzungsUhrzeitController unitUnderTest;

    @Test
    void getDataFromService() {
        val wahlbezirkID = "wahlbezirkID";
        val fortsetzungsUhrzeit = LocalDateTime.now();

        val mockedServiceOptionalBody = new FortsetzungsUhrzeitModel(wahlbezirkID, fortsetzungsUhrzeit);
        val mockedMappedServiceResponseAsDTO = new FortsetzungsUhrzeitDTO(wahlbezirkID, fortsetzungsUhrzeit);

        Mockito.when(fortsetzungsUhrzeitService.getFortsetzungsUhrzeit(wahlbezirkID)).thenReturn(Optional.of(mockedServiceOptionalBody));
        Mockito.when(fortsetzungsUhrzeitDTOMapper.toDTO(mockedServiceOptionalBody)).thenReturn(mockedMappedServiceResponseAsDTO);

        val result = unitUnderTest.getFortsetzungsUhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceResponseAsDTO);
    }

    @Test
    void gotNoDataFromService() {
        val wahlbezirkID = "wahlbezirkID";

        Mockito.when(fortsetzungsUhrzeitService.getFortsetzungsUhrzeit(wahlbezirkID)).thenReturn(Optional.empty());

        val result = unitUnderTest.getFortsetzungsUhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(result.getBody()).isNull();
    }

    @Test
    void requestIsMappedAndSendToService() {
        val wahlbezirkID = "wahlbezirkID";
        val fortsetzungsUhrzeit = LocalDateTime.now();
        val requestBody = new FortsetzungsUhrzeitWriteDTO(fortsetzungsUhrzeit);

        val mockedMappedRequest = new FortsetzungsUhrzeitModel(wahlbezirkID, fortsetzungsUhrzeit);

        Mockito.when(fortsetzungsUhrzeitDTOMapper.toModel(eq(wahlbezirkID), eq(requestBody))).thenReturn(mockedMappedRequest);

        Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postFortsetzungsUhrzeit(wahlbezirkID, requestBody));
        Mockito.verify(fortsetzungsUhrzeitService).setFortsetzungsUhrzeit(mockedMappedRequest);
    }

}
