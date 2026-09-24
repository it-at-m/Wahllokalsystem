package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.DokumentartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckReadModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckWriteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.DokumentartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.WahlUndBezirkIDUndDokumentartModel;
import java.time.Instant;
import java.util.List;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AusdruckControllerTest {

  @Mock AusdruckService ausdruckService;

  @Mock AusdruckDTOMapper ausdruckDTOMapper;

  @Mock DokumentartDTOMapper dokumentartDTOMapper;

  @InjectMocks AusdruckController unitUnderTest;

  @Nested
  class GetAusdruck {

    @Test
    void should_returnTextWithHttpStatusOk_when_serviceReturnsData() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dokumentartModel = DokumentartModel.V1;
      val dokumentartDTO = DokumentartDTO.V1;
      val content = "Testcontent";
      val erstelltAm = Instant.now();

      val idModel = new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel);
      val mockedServiceResponse = new AusdruckReadModel(idModel, content, erstelltAm);
      val mockedServiceResponseAsDTO =
          new AusdruckReadDTO(wahlbezirkID, wahlID, dokumentartDTO, content, erstelltAm);

      Mockito.when(ausdruckService.getAusdruck(idModel))
          .thenReturn(Optional.of(mockedServiceResponse));
      Mockito.when(ausdruckDTOMapper.toDTO(mockedServiceResponse))
          .thenReturn(mockedServiceResponseAsDTO);
      Mockito.when(dokumentartDTOMapper.toModel(dokumentartDTO)).thenReturn(dokumentartModel);

      val result = unitUnderTest.getAusdruck(wahlID, wahlbezirkID, dokumentartDTO);

      Assertions.assertThat(result.getBody()).isEqualTo(mockedServiceResponseAsDTO.content());
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      Assertions.assertThat(result.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0))
          .isEqualTo("text/html; charset=utf-8");
    }

    @Test
    void should_returnNullBodyWithHttpStatusNotFound_when_serviceReturnsEmptyOptional() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dokumentartDTO = DokumentartDTO.V1;

      Mockito.when(ausdruckService.getAusdruck(any())).thenReturn(Optional.empty());
      val result = unitUnderTest.getAusdruck(wahlID, wahlbezirkID, dokumentartDTO);

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

      val mockedAusdruckeModel1 =
          new AusdruckReadModel(
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, DokumentartModel.V1),
              content,
              erstelltAm);
      val mockedAusdruckeModel2 =
          new AusdruckReadModel(
              new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, DokumentartModel.V3),
              content,
              erstelltAm);
      val mockedServiceModel = List.of(mockedAusdruckeModel1, mockedAusdruckeModel2);

      val mockedAusdruckReadDTO1 =
          new AusdruckReadDTO(wahlbezirkID, wahlID, DokumentartDTO.V1, content, erstelltAm);
      val mockedAusdruckReadDTO2 =
          new AusdruckReadDTO(wahlbezirkID, wahlID, DokumentartDTO.V3, content, erstelltAm);
      val mockedMappedServiceDTO = List.of(mockedAusdruckReadDTO1, mockedAusdruckReadDTO2);

      Mockito.when(ausdruckService.getAllAusdrucke(wahlID, wahlbezirkID))
          .thenReturn(mockedServiceModel);
      Mockito.when(ausdruckDTOMapper.toDTO(mockedAusdruckeModel1))
          .thenReturn(mockedAusdruckReadDTO1);
      Mockito.when(ausdruckDTOMapper.toDTO(mockedAusdruckeModel2))
          .thenReturn(mockedAusdruckReadDTO2);

      val result = unitUnderTest.getAllAusdrucke(wahlID, wahlbezirkID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedServiceDTO);
      Assertions.assertThat(result.getBody()).hasSize(mockedServiceModel.size());
    }
  }

  @Nested
  class PostAusdruck {

    @Test
    void should_callServiceWithAusdruckWriteModel_when_calledWithData() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dokumentartModel = DokumentartModel.V1;

      val content = "Testcontent";
      val idModel = new WahlUndBezirkIDUndDokumentartModel(wahlbezirkID, wahlID, dokumentartModel);
      val ausdruckWriteDTO = new AusdruckWriteDTO(content);

      val mockedAusdruckModel = new AusdruckWriteModel(idModel, content);
      Mockito.when(ausdruckDTOMapper.toModel(ausdruckWriteDTO, idModel))
          .thenReturn(mockedAusdruckModel);

      unitUnderTest.postAusdruck(wahlID, wahlbezirkID, dokumentartModel, ausdruckWriteDTO);

      Mockito.verify(ausdruckService).saveAusdruck(mockedAusdruckModel);
    }
  }
}
