package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.Druckzustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahllokalZustandMapperTest {

  private final WahllokalZustandMapper unitUnderTest =
      Mappers.getMapper(WahllokalZustandMapper.class);

  @Nested
  class ToEntity {

    @Nested
    class OfWahllokalZustandDTO {

      @Test
      void should_returnNull_when_nullIsGiven() {
        Assertions.assertThat(unitUnderTest.toEntity((WahllokalZustandDTO) null)).isNull();
      }

      @Test
      void should_returnEntity_when_dtoIsGiven() {
        val result = unitUnderTest.toEntity(createDTO());

        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(createExpectedEntity());
      }

      private WahllokalZustandDTO createDTO() {
        return new WahllokalZustandDTO(
            "df52e11e-381b-488c-8fe8-f03dd3128536",
            "A",
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            Set.of(
                new DruckzustandDTO(
                    "6f0b87cc-89f8-4487-ae8f-050d78b37628",
                    LocalDateTime.parse("2026-06-20T09:15:01.123"),
                    LocalDateTime.parse("2026-06-20T09:15:01.123"),
                    LocalDateTime.parse("2026-06-20T09:15:01.123"),
                    LocalDateTime.parse("2026-06-20T09:15:01.123")),
                new DruckzustandDTO(
                    "2853ba2d-baaa-49ee-93f7-a653d17d6a72",
                    LocalDateTime.parse("2025-06-20T09:15:01.123"),
                    LocalDateTime.parse("2025-06-20T09:15:01.123"),
                    LocalDateTime.parse("2025-06-20T09:15:01.123"),
                    LocalDateTime.parse("2025-06-20T09:15:01.123"))));
      }

      private WahllokalZustand createExpectedEntity() {
        return new WahllokalZustand(
            UUID.fromString("df52e11e-381b-488c-8fe8-f03dd3128536"),
            "A",
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            Set.of(
                new Druckzustand(
                    UUID.fromString("6f0b87cc-89f8-4487-ae8f-050d78b37628"),
                    LocalDateTime.parse("2026-06-20T09:15:01.123"),
                    LocalDateTime.parse("2026-06-20T09:15:01.123"),
                    LocalDateTime.parse("2026-06-20T09:15:01.123"),
                    LocalDateTime.parse("2026-06-20T09:15:01.123")),
                new Druckzustand(
                    UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"),
                    LocalDateTime.parse("2025-06-20T09:15:01.123"),
                    LocalDateTime.parse("2025-06-20T09:15:01.123"),
                    LocalDateTime.parse("2025-06-20T09:15:01.123"),
                    LocalDateTime.parse("2025-06-20T09:15:01.123"))));
      }
    }

    @Nested
    class OfDruckzustandDTO {

      @Test
      void should_returnNull_when_nullIsGiven() {
        Assertions.assertThat(unitUnderTest.toEntity((DruckzustandDTO) null)).isNull();
      }

      @Test
      void should_returnEntity_when_dtoIsGiven() {
        val result = unitUnderTest.toEntity(createDTO());

        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(createExpectedEntity());
      }

      private DruckzustandDTO createDTO() {
        return new DruckzustandDTO(
            "6f0b87cc-89f8-4487-ae8f-050d78b37628",
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"));
      }

      private Druckzustand createExpectedEntity() {
        return new Druckzustand(
            UUID.fromString("6f0b87cc-89f8-4487-ae8f-050d78b37628"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"),
            LocalDateTime.parse("2026-06-20T09:15:01.123"));
      }
    }
  }
}
