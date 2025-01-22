package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
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
            val content = "Testcontent";
            val erstelltAm = Instant.now();

            val id = new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsArt);
            val mockedServiceResponse = new AusdruckModel(id, content, erstelltAm);
            val mockedServiceResponseAsDTO = new AusdruckReadDTO(wahlbezirkID, wahlID, meldungsArt, content, erstelltAm);

            when(ausdruckService.getAusdruck(id)).thenReturn(mockedServiceResponse);
            when(ausdruckReadDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getAusdruck(wahlID, wahlbezirkID, meldungsArt);

            Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO.content());
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0)).isEqualTo("text/html; charset=utf-8");
        }

        @Test
        void should_returnNullWithHttpStatusNotFound_when_serviceReturnsNoData() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsArt = Meldungsart.V1;

            when(ausdruckService.getAusdruck(any())).thenReturn(null);
            when(ausdruckReadDTOMapper.toDTO(any())).thenReturn(null);

            val result = unitUnderTest.getAusdruck(wahlID, wahlbezirkID, meldungsArt);

            Assertions.assertThat(result.getBody()).isNull();
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    class PostAusdruck {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            var clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
            var mockedInstant = Instant.now(clock);
            MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS);
            mockedStatic.when(Instant::now).thenReturn(mockedInstant);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsArt = Meldungsart.V1;
            val content = "Testcontent";
            val erstelltAm = Instant.now();
            val id = new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsArt);
            val ausdruckWriteDTO = new AusdruckWriteDTO(content);

            val mockedAusdruckModel = new AusdruckModel(id, content, erstelltAm);
            when(ausdruckWriteDTOMapper.toModel(ausdruckWriteDTO, id, erstelltAm)).thenReturn(mockedAusdruckModel);

            unitUnderTest.postAusdruck(wahlID, wahlbezirkID, meldungsArt, ausdruckWriteDTO);

            Mockito.verify(ausdruckService).saveAusdruck((mockedAusdruckModel));

            mockedStatic.close();
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

            val mockedServiceModel = List.of(new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V1), content, erstelltAm),
                    new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V3), content, erstelltAm));
            val mockedMappedServiceDTO = List.of(new AusdruckReadDTO(wahlbezirkID, wahlID, Meldungsart.V1, content, erstelltAm),
                    new AusdruckReadDTO(wahlbezirkID, wahlID, Meldungsart.V3, content, erstelltAm));

            Mockito.when(ausdruckService.getAllAusdrucke(wahlID, wahlbezirkID)).thenReturn(mockedServiceModel);
            Mockito.when(ausdruckReadDTOMapper.fromListOfAusdruckModelToListOfAusdruckReadDTO(mockedServiceModel)).thenReturn(mockedMappedServiceDTO);

            val result = unitUnderTest.getAllAusdrucke(wahlID, wahlbezirkID);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isSameAs(mockedMappedServiceDTO);
        }
    }

}
