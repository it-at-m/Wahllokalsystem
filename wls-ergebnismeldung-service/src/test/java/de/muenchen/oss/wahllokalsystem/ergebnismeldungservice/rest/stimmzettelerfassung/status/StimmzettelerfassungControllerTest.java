package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.status;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.ErfassungStatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.StimmzettelerfassungService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
public class StimmzettelerfassungControllerTest {

  @Mock StimmzettelerfassungService stimmzettelerfassungService;

  @Mock ErfassungStatusDTOMapper erfassungStatusDTOMapper;

  @InjectMocks StimmzettelerfassungController unitUnderTest;

  @Nested
  class SaveStimmzettelerfassungStatus {
    @Test
    void should_callServiceWithModel_when_calledWithData() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val erfassungStatusModel = ErfassungStatusModel.STE_ABGESCHLOSSEN;
      val erfassungStatusDTO = ErfassungStatusDTO.STE_ABGESCHLOSSEN;

      Mockito.when(erfassungStatusDTOMapper.toModel(erfassungStatusDTO))
          .thenReturn(erfassungStatusModel);

      unitUnderTest.saveStimmzettelerfassungStatus(
          wahlID, wahlbezirkID, new StimmzettelerfassungStatusDTO(erfassungStatusDTO));

      Mockito.verify(stimmzettelerfassungService)
          .saveStimmzettelerfassungStatus(
              eq(new BezirkUndWahlID(wahlID, wahlbezirkID)), eq(erfassungStatusModel));
    }
  }

  @Nested
  class GetStimmzettelerfassungStatus {
    @Test
    void should_returnDTOWithHttpStatusOk_when_serviceReturnedData() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val mockedModel = ErfassungStatusModel.STE_ABGESCHLOSSEN;
      val mockedDto = ErfassungStatusDTO.STE_ABGESCHLOSSEN;

      Mockito.when(
              stimmzettelerfassungService.getStimmzettelerfassungStatus(
                  new BezirkUndWahlID(wahlbezirkID, wahlID)))
          .thenReturn(Optional.of(mockedModel));
      Mockito.when(erfassungStatusDTOMapper.toDTO(mockedModel)).thenReturn(mockedDto);

      val result = unitUnderTest.getStimmzettelerfassungStatus(wahlbezirkID, wahlID);

      Assertions.assertThat(result.getBody()).isEqualTo(mockedDto);
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_returnEmptyWithHttpStatusNoContent_when_serviceReturnsNoData() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              stimmzettelerfassungService.getStimmzettelerfassungStatus(
                  new BezirkUndWahlID(wahlbezirkID, wahlID)))
          .thenReturn(Optional.empty());

      val result = unitUnderTest.getStimmzettelerfassungStatus(wahlbezirkID, wahlID);

      Assertions.assertThat(result.getBody()).isNull();
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
  }
}
