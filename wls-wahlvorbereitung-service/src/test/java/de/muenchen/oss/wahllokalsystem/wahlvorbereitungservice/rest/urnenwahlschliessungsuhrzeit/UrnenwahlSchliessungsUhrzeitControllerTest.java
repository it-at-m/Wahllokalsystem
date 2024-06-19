package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitService;
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
public class UrnenwahlSchliessungsUhrzeitControllerTest {

    @Mock
    UrnenwahlSchliessungsUhrzeitService urnenwahlSchliessungsUhrzeitService;

    @Mock
    UrnenwahlSchliessungsUhrzeitDTOMapper urnenwahlSchliessungsUhrzeitDTOMapper;

    @InjectMocks
    UrnenwahlSchliessungsUhrzeitController unitUnderTest;

    @Test
    void getDataFromService() {
        val wahlbezirkID = "wahlbezirkID";
        val urnenwahlSchliessungsUhrzeit = LocalDateTime.now();

        val mockedServiceOptionalBody = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, urnenwahlSchliessungsUhrzeit);
        val mockedMappedServiceResponseAsDTO = new UrnenwahlSchliessungsUhrzeitDTO(wahlbezirkID, urnenwahlSchliessungsUhrzeit);

        Mockito.when(urnenwahlSchliessungsUhrzeitService.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID)).thenReturn(Optional.of(mockedServiceOptionalBody));
        Mockito.when(urnenwahlSchliessungsUhrzeitDTOMapper.toDTO(mockedServiceOptionalBody)).thenReturn(mockedMappedServiceResponseAsDTO);

        val result = unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceResponseAsDTO);
    }

    @Test
    void gotNoDataFromService() {
        val wahlbezirkID = "wahlbezirkID";

        Mockito.when(urnenwahlSchliessungsUhrzeitService.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID)).thenReturn(Optional.empty());

        val result = unitUnderTest.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(result.getBody()).isNull();
    }

    @Test
    void requestIsMappedAndSendToService() {
        val wahlbezirkID = "wahlbezirkID";
        val urnenwahlSchliessungsUhrzeit = LocalDateTime.now();
        val requestBody = new UrnenwahlSchliessungsUhrzeitWriteDTO(urnenwahlSchliessungsUhrzeit);

        val mockedMappedRequest = new UrnenwahlSchliessungsUhrzeitModel(wahlbezirkID, urnenwahlSchliessungsUhrzeit);

        Mockito.when(urnenwahlSchliessungsUhrzeitDTOMapper.toModel(eq(wahlbezirkID), eq(requestBody))).thenReturn(mockedMappedRequest);

        Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postUrnenwahlSchliessungsUhrzeit(wahlbezirkID, requestBody));
        Mockito.verify(urnenwahlSchliessungsUhrzeitService).setUrnenwahlSchliessungsUhrzeit(mockedMappedRequest);
    }

}
