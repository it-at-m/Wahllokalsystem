package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.BedenklicherStimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.ValidityModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MBWBedenklicheStimmzettelControllerTest {

  @Mock MBWBedenklicheStimmzettelService bedenklicheStimmzettelService;

  @Mock BedenklicherStimmzettelDTOMapper bedenklicheStimmzettelDTOMapper;

  @InjectMocks MBWBedenklicheStimmzettelController unitUnderTest;

  @Nested
  class GetBedenklicheStimmzettelByOrderIndexAsc {

    @Test
    void should_returnCollectionWithDTOs_when_modelsGivenByService() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val bedenklicherStimmzettelModel =
          new BedenklicherStimmzettelModel(12, Collections.emptySet(), ValidityModel.INVALID);
      val mockedServiceResponse =
          List.of(bedenklicherStimmzettelModel, bedenklicherStimmzettelModel);
      //
      // Mockito.when(bedenklicheStimmzettelService.getBedenklicheStimmzettelOrderedByOrderIndexAsc(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(mockedServiceResponse);

      val mockedMappedModel =
          new BedenklicherStimmzettelDTO(12, Collections.emptySet(), ValidityDTO.INVALID);
      Mockito.when(bedenklicheStimmzettelDTOMapper.toDTO(bedenklicherStimmzettelModel))
          .thenReturn(mockedMappedModel);

      val result = unitUnderTest.getBedenklicheStimmzettelByOrderIndexAsc(wahlID, wahlbezirkID);

      val expectedResult = List.of(mockedMappedModel, mockedMappedModel);
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_returnEmptyCollection_when_emptyCollectionIsGivenByService() {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      //
      // Mockito.when(bedenklicheStimmzettelService.getBedenklicheStimmzettelOrderedByOrderIndexAsc(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(Collections.emptyList());

      val result = unitUnderTest.getBedenklicheStimmzettelByOrderIndexAsc(wahlID, wahlbezirkID);

      //            Assertions.assertThat(result.isEmpty()).isTrue();
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
