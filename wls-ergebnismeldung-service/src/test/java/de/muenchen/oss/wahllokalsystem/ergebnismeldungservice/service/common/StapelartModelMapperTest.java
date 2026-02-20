package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class StapelartModelMapperTest {

  private final StapelartModelMapper unitUnderTest = Mappers.getMapper(StapelartModelMapper.class);

  @Nested
  class ToModel {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertNull(unitUnderTest.toModel(null));
    }

    @Test
    void should_returnStapelartModel_when_givenStapelart() {
      val stapelart = Stapelart.BTW_A;

      val result = unitUnderTest.toModel(stapelart);

      val expectedResult = StapelartModel.BTW_A;

      Assertions.assertEquals(expectedResult, result);
    }
  }

  @Nested
  class ToEntity {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertNull(unitUnderTest.toEntity(null));
    }

    @Test
    void should_returnStapelartEntity_when_givenStapelartModel() {
      val stapelartModel = StapelartModel.BTW_A;

      val result = unitUnderTest.toEntity(stapelartModel);

      val expectedResult = Stapelart.BTW_A;

      Assertions.assertEquals(expectedResult, result);
    }
  }
}
