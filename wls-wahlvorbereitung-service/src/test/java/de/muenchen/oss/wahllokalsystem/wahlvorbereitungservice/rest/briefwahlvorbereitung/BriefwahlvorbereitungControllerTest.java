package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import static org.mockito.ArgumentMatchers.eq;

import java.util.Collections;
import java.util.Optional;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungService;
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
public class BriefwahlvorbereitungControllerTest {

    @Mock
    BriefwahlvorbereitungService briefwahlvorbereitungService;

    @Mock
    BriefwahlvorbereitungDTOMapper briefwahlvorbereitungDTOMapper;

    @InjectMocks
    BriefwahlvorbereitungController unitUnderTest;

    @Test
    void getDataFromService() {
        val wahlbezirkID = "wahlbezirkID";

        val mockedServiceOptionalBody = new BriefwahlvorbereitungModel(wahlbezirkID, Collections.emptyList());
        val mockedMappedServiceResponseAsDTO = new BriefwahlvorbereitungDTO(wahlbezirkID, Collections.emptyList());

        Mockito.when(briefwahlvorbereitungService.getBriefwahlvorbereitung(wahlbezirkID)).thenReturn(Optional.of(mockedServiceOptionalBody));
        Mockito.when(briefwahlvorbereitungDTOMapper.toDTO(mockedServiceOptionalBody)).thenReturn(mockedMappedServiceResponseAsDTO);

        val result = unitUnderTest.getBriefwahlvorbereitung(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceResponseAsDTO);
    }

    @Test
    void gotNoDataFromService() {
        val wahlbezirkID = "wahlbezirkID";

        Mockito.when(briefwahlvorbereitungService.getBriefwahlvorbereitung(wahlbezirkID)).thenReturn(Optional.empty());

        val result = unitUnderTest.getBriefwahlvorbereitung(wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(result.getBody()).isNull();
    }

    @Test
    void requestIsMappedAndSendToService() {
        val wahlbezirkID = "wahlbezirkID";
        val requestBody = new BriefwahlvorbereitungWriteDTO(Collections.emptyList());

        val mockedMappedRequest = new BriefwahlvorbereitungModel(wahlbezirkID, Collections.emptyList());

        Mockito.when(briefwahlvorbereitungDTOMapper.toModel(eq(wahlbezirkID), eq(requestBody))).thenReturn(mockedMappedRequest);

        Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postBriefwahlvorbereitung(wahlbezirkID, requestBody));
        Mockito.verify(briefwahlvorbereitungService).setBriefwahlvorbereitung(mockedMappedRequest);
    }

}
