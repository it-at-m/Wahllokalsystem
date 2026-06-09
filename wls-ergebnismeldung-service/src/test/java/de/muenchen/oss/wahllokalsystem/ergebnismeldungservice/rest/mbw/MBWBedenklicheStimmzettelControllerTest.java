package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.BedenklicherStimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.ValidityModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.Collections;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class MBWBedenklicheStimmzettelControllerTest {

  @Mock MBWBedenklicheStimmzettelService bedenklicheStimmzettelService;

  @Mock BedenklicherStimmzettelDTOMapper bedenklicheStimmzettelDTOMapper;

  @InjectMocks MBWBedenklicheStimmzettelController unitUnderTest;

  @Nested
  class GetBedenklicheStimmzettelByOrderIndexAsc {

    @Test
    void should_returnOkWithCollectionOfDTOs_when_modelsGivenByService() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val bedenklicherStimmzettelModel =
          new BedenklicherStimmzettelModel(12, Collections.emptySet(), ValidityModel.INVALID);
      val mockedServiceResponse =
          List.of(bedenklicherStimmzettelModel, bedenklicherStimmzettelModel);

      Mockito.when(
              bedenklicheStimmzettelService.getBedenklicheStimmzettelOrderedByOrderIndexAsc(
                  new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .thenReturn(Optional.of(mockedServiceResponse));

      val mockedMappedModel =
          List.of(new BedenklicherStimmzettelDTO(12, Collections.emptySet(), ValidityDTO.INVALID));
      Mockito.when(bedenklicheStimmzettelDTOMapper.toDTO(mockedServiceResponse))
          .thenReturn(mockedMappedModel);

      val result = unitUnderTest.getBedenklicheStimmzettelByOrderIndexAsc(wahlID, wahlbezirkID);

      Assertions.assertThat(result.getBody()).isEqualTo(mockedMappedModel);
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_returnOkWithEmptyCollection_when_emptyCollectionGivenByService() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      final Collection<BedenklicherStimmzettelModel> mockedServiceResponse =
          Collections.emptyList();

      Mockito.when(
              bedenklicheStimmzettelService.getBedenklicheStimmzettelOrderedByOrderIndexAsc(
                  new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .thenReturn(Optional.of(mockedServiceResponse));

      val result = unitUnderTest.getBedenklicheStimmzettelByOrderIndexAsc(wahlID, wahlbezirkID);

      Assertions.assertThat(result.getBody()).isEqualTo(Collections.emptyList());
      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_returnNoContentEmptyCollection_when_emptyIsGivenByService() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      Mockito.when(
              bedenklicheStimmzettelService.getBedenklicheStimmzettelOrderedByOrderIndexAsc(
                  new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .thenReturn(Optional.empty());

      val result = unitUnderTest.getBedenklicheStimmzettelByOrderIndexAsc(wahlID, wahlbezirkID);

      Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
  }

  @Nested
  class SetBedenklicheStimmzettel {

    @Test
    void should_callService_when_dtoAndPathVarsGiven() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val dto = new BedenklicherStimmzettelDTO(11, Collections.emptySet(), ValidityDTO.VALID);
      val requestBody = List.of(dto, dto);

      val mockedMappedDTO =
          new BedenklicherStimmzettelModel(11, Collections.emptySet(), ValidityModel.VALID);
      Mockito.when(bedenklicheStimmzettelDTOMapper.toModel(dto)).thenReturn(mockedMappedDTO);

      Assertions.assertThatNoException()
          .isThrownBy(
              () -> unitUnderTest.setBedenklicheStimmzettel(wahlID, wahlbezirkID, requestBody));
      Mockito.verify(bedenklicheStimmzettelService)
          .setBedenklicheStimmzettel(
              Mockito.eq(new BezirkUndWahlID(wahlID, wahlbezirkID)),
              Mockito.eq(List.of(mockedMappedDTO, mockedMappedDTO)));
    }
  }
}
