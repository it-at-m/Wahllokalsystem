package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class StapelartDTOMapperTest {

  private final StapelartDTOMapper unitUnderTest = Mappers.getMapper(StapelartDTOMapper.class);

  @Nested
  class ToModel {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertNull(unitUnderTest.toModel(null));
    }

    @Test
    void should_returnStapelartModel_when_givenStapelartDto() {
      val stapelartDTO = StapelartDTO.BTW_A;

      val result = unitUnderTest.toModel(stapelartDTO);

      val expectedResult = StapelartModel.BTW_A;

      Assertions.assertEquals(expectedResult, result);
    }
  }

  @Nested
  class ToDTO {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertNull(unitUnderTest.toDTO(null));
    }

    @Test
    void should_returnStapelartDto_when_givenStapelartModel() {
      val stapelartModel = StapelartModel.BTW_A;

      val result = unitUnderTest.toDTO(stapelartModel);

      val expectedResult = StapelartDTO.BTW_A;

      Assertions.assertEquals(expectedResult, result);
    }
  }
}
