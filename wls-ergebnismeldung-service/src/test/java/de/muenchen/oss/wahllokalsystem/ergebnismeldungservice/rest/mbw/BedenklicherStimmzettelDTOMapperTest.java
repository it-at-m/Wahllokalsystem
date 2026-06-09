package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.BedenklicherStimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.SupplementModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.ValidityModel;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BedenklicherStimmzettelDTOMapperTest {

  BedenklicherStimmzettelDTOMapper unitUnderTest =
      Mappers.getMapper(BedenklicherStimmzettelDTOMapper.class);

  @Nested
  class ToDTO {

    @Test
    void should_createDTO_when_modelIsGiven() {
      val modelToMap =
          List.of(
              new BedenklicherStimmzettelModel(
                  23,
                  Set.of(
                      SupplementModel.TOO_MANY_LISTENKREUZE,
                      SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                  ValidityModel.PARTIAL_VALID),
              new BedenklicherStimmzettelModel(12, Collections.emptySet(), ValidityModel.VALID));

      val result = unitUnderTest.toDTO(modelToMap);

      val expectedResult =
          List.of(
              new BedenklicherStimmzettelDTO(
                  23,
                  Set.of(
                      SupplementDTO.TOO_MANY_LISTENKREUZE,
                      SupplementDTO.TOO_MANY_SINGLE_KANDIDAT_VOTES),
                  ValidityDTO.PARTIAL_VALID),
              new BedenklicherStimmzettelDTO(12, Collections.emptySet(), ValidityDTO.VALID));
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_returnNull_when_nullIsGiven() {
      Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
    }
  }

  @Nested
  class ToModel {

    @Test
    void should_createModel_when_dtoIsGiven() {
      val dtoToMap =
          new BedenklicherStimmzettelDTO(
              23,
              Set.of(
                  SupplementDTO.TOO_MANY_LISTENKREUZE,
                  SupplementDTO.TOO_MANY_SINGLE_KANDIDAT_VOTES),
              ValidityDTO.PARTIAL_VALID);

      val result = unitUnderTest.toModel(dtoToMap);

      val expectedResult =
          new BedenklicherStimmzettelModel(
              dtoToMap.orderIndex(),
              Set.of(
                  SupplementModel.TOO_MANY_LISTENKREUZE,
                  SupplementModel.TOO_MANY_SINGLE_KANDIDAT_VOTES),
              ValidityModel.PARTIAL_VALID);
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void should_returnNull_when_nullIsGiven() {
      Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
    }
  }
}
