package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungService;
import java.util.Collections;
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
class UrnenwahlvorbereitungControllerTest {

    @Mock
    UrnenwahlvorbereitungService service;

    @Mock
    UrnenwahlvorbereitungDTOMapper urnenwahlvorbereitungDTOMapper;

    @InjectMocks
    UrnenwahlvorbereitungController unitUnderTest;

    @Nested
    class GetUrnenwahlVorbereitung {

        @Test
        void gotDataFromService() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedServiceOptionalBody = new UrnenwahlvorbereitungModel(wahlbezirkID, 0, 0, 0, Collections.emptyList());
            val mockedMappedServiceResponseAsDTO = new UrnenwahlvorbereitungDTO(wahlbezirkID, 0, 0, 0, Collections.emptyList());

            Mockito.when(service.getUrnenwahlvorbereitung(wahlbezirkID)).thenReturn(Optional.of(mockedServiceOptionalBody));
            Mockito.when(urnenwahlvorbereitungDTOMapper.toDTO(mockedServiceOptionalBody)).thenReturn(mockedMappedServiceResponseAsDTO);

            val result = unitUnderTest.getUrnenwahlVorbereitung(wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceResponseAsDTO);
        }

        @Test
        void gotNoDataFromService() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(service.getUrnenwahlvorbereitung(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getUrnenwahlVorbereitung(wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

}
