package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AusdruckControllerTest {

    @Mock
    AusdruckService ausdruckService;

    @Mock
    AusdruckReadDTOMapper ausdruckReadDTOMapper;

    @Mock
    AusdruckWriteDTOMapper ausdruckWriteDTOMapper;

    @InjectMocks
    AusdruckController unitUnderTest;

    @Nested
    class GetAusdruck {

        @Test
        void should_returnTextWithHttpStatusOk_when_serviceReturnsData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsArt = Meldungsart.V1;
            val meldungsArtDto = MeldungsartDTO.V1;
            val content = "Testcontent";
            val erstelltAm = Instant.now();

            val id = new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsArt);
            val mockedServiceResponse = new AusdruckModel(id, content, erstelltAm);
            val mockedServiceResponseAsDTO = new AusdruckReadDTO(wahlbezirkID, wahlID, meldungsArtDto, content, erstelltAm);

            Mockito.when(ausdruckService.getAusdruck(id)).thenReturn(Optional.of(mockedServiceResponse));
            Mockito.when(ausdruckReadDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getAusdruck(wahlID, wahlbezirkID, meldungsArt);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO.content());
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo("text/html; charset=utf-8");
        }

        @Test
        void should_returnNullBodyWithHttpStatusNotFound_when_serviceReturnsEmtyOptional() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsArt = Meldungsart.V1;

            Mockito.when(ausdruckService.getAusdruck(any())).thenReturn(Optional.empty());

            val result = unitUnderTest.getAusdruck(wahlID, wahlbezirkID, meldungsArt);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    class GetAllAusdrucke {

        @Test
        void should_returnHttpStatusOkAndData_when_dataIsFound() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val content = "Testcontent";
            val erstelltAm = Instant.now();

            val mockedAusdruckeModel1 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V1), content, erstelltAm);
            val mockedAusdruckeModel2 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V3), content, erstelltAm);
            val mockedServiceModel = List.of(mockedAusdruckeModel1, mockedAusdruckeModel2);

            val mockedAusdruckReadDTO1 = new AusdruckReadDTO(wahlbezirkID, wahlID, MeldungsartDTO.V1, content, erstelltAm);
            val mockedAusdruckReadDTO2 = new AusdruckReadDTO(wahlbezirkID, wahlID, MeldungsartDTO.V3, content, erstelltAm);
            val mockedMappedServiceDTO = List.of(mockedAusdruckReadDTO1, mockedAusdruckReadDTO2);

            Mockito.when(ausdruckService.getAllAusdrucke(wahlID, wahlbezirkID)).thenReturn(mockedServiceModel);
            Mockito.when(ausdruckReadDTOMapper.toDTO(mockedAusdruckeModel1)).thenReturn(mockedAusdruckReadDTO1);
            Mockito.when(ausdruckReadDTOMapper.toDTO(mockedAusdruckeModel2)).thenReturn(mockedAusdruckReadDTO2);

            val result = unitUnderTest.getAllAusdrucke(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceDTO);
            Assertions.assertThat(result.getBody()).hasSize(mockedServiceModel.size());
        }
    }

    @Nested
    class PostAusdruck {

        private MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS);

        @BeforeEach
        void setup() {
            var clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
            var mockedInstant = Instant.now(clock);
            mockedStatic.when(Instant::now).thenReturn(mockedInstant);
        }

        @AfterEach
        void tearDown() {
            mockedStatic.close();
        }

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsArt = Meldungsart.V1;
            val content = "Testcontent";
            val erstelltAm = Instant.now();
            val id = new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsArt);
            val ausdruckWriteDTO = new AusdruckWriteDTO(content);

            val mockedAusdruckModel = new AusdruckModel(id, content, erstelltAm);
            Mockito.when(ausdruckWriteDTOMapper.toModel(ausdruckWriteDTO, id)).thenReturn(mockedAusdruckModel);

            unitUnderTest.postAusdruck(wahlID, wahlbezirkID, meldungsArt, ausdruckWriteDTO);

            Mockito.verify(ausdruckService).saveAusdruck(mockedAusdruckModel);
        }
    }
}
