package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit;

import static org.mockito.ArgumentMatchers.eq;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit.EroeffnungsUhrzeitModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit.EroeffnungsUhrzeitService;
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
public class EroeffnungsUhrzeitControllerTest {

    @Mock
    EroeffnungsUhrzeitService eroeffnungsUhrzeitService;

    @Mock
    EroeffnungsUhrzeitDTOMapper eroeffnungsUhrzeitDTOMapper;

    @InjectMocks
    EroeffnungsUhrzeitController unitUnderTest;

    @Test
    void getDataFromService() {
        val wahlbezirkID = "wahlbezirkID";
        val eroeffnungsUhrzeit = LocalDateTime.now();

        val mockedServiceOptionalBody = new EroeffnungsUhrzeitModel(wahlbezirkID, eroeffnungsUhrzeit);
        val mockedMappedServiceResponseAsDTO = new EroeffnungsUhrzeitDTO(wahlbezirkID, eroeffnungsUhrzeit);

        Mockito.when(eroeffnungsUhrzeitService.getEroeffnungsUhrzeit(wahlbezirkID)).thenReturn(Optional.of(mockedServiceOptionalBody));
        Mockito.when(eroeffnungsUhrzeitDTOMapper.toDTO(mockedServiceOptionalBody)).thenReturn(mockedMappedServiceResponseAsDTO);

        val result = unitUnderTest.getEroeffnungsuhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceResponseAsDTO);
    }

    @Test
    void gotNoDataFromService() {
        val wahlbezirkID = "wahlbezirkID";

        Mockito.when(eroeffnungsUhrzeitService.getEroeffnungsUhrzeit(wahlbezirkID)).thenReturn(Optional.empty());

        val result = unitUnderTest.getEroeffnungsuhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(result.getBody()).isNull();
    }

    @Test
    void requestIsMappedAndSendToService() {
        val wahlbezirkID = "wahlbezirkID";
        val eroeffnungsUhrzeit = LocalDateTime.now();
        val requestBody = new EroeffnungsUhrzeitWriteDTO(wahlbezirkID, eroeffnungsUhrzeit);

        val mockedMappedRequest = new EroeffnungsUhrzeitModel(wahlbezirkID, eroeffnungsUhrzeit);

        Mockito.when(eroeffnungsUhrzeitDTOMapper.toModel(eq(wahlbezirkID), eq(requestBody))).thenReturn(mockedMappedRequest);

        Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEroeffnungsuhrzeit(wahlbezirkID, requestBody));
        Mockito.verify(eroeffnungsUhrzeitService).setEroeffnungsUhrzeit(mockedMappedRequest);
    }

}
