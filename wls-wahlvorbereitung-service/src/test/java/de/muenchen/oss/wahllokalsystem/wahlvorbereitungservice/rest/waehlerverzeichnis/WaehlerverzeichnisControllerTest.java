package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisModel;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
class WaehlerverzeichnisControllerTest {

    @Mock
    WaehlerverzeichnisDTOMapper waehlerverzeichnisDTOMapper;

    @Mock
    WaehlerverzeichnisService waehlerverzeichnisService;

    @InjectMocks
    WaehlerverzeichnisController unitUnderTest;

    @Nested
    class PostWaehlerverzeichnis {

        @Test
        void dtoIsMappedAndSendToService() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 13L;
            val requestDTO = new WaehlerverzeichnisWriteDTO(true, true, true, true);

            val mockedDTOMappedToModel = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false,
                    false, false, false);
            Mockito.when(
                    waehlerverzeichnisDTOMapper.toModel(eq(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)), eq(requestDTO)))
                    .thenReturn(mockedDTOMappedToModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postWaehlerverzeichnis(wahlbezirkID, waehlerverzeichnisNummer, requestDTO));

            Mockito.verify(waehlerverzeichnisService).setWaehlerverzeichnis(mockedDTOMappedToModel);
        }
    }

    @Nested
    class GetWaehlerverzeichnis {

        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 13L;

            val mockedServiceResponse = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false,
                    false, false, false);
            val mockedServiceResponseAsDTO = new WaehlerverzeichnisDTO(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false,
                    false, false, false);

            Mockito.when(waehlerverzeichnisService.getWaehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)))
                    .thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(waehlerverzeichnisDTOMapper.toDto(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getWaehlerverzeichnis(wahlbezirkID, waehlerverzeichnisNummer);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isSameAs(mockedServiceResponseAsDTO);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 13L;

            Mockito.when(waehlerverzeichnisService.getWaehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)))
                    .thenReturn(Optional.empty());

            val result = unitUnderTest.getWaehlerverzeichnis(wahlbezirkID, waehlerverzeichnisNummer);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

}
