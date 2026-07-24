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
            null,
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

    @Nested
    class OfEntityWithLastSeen {

      @Test
      void should_map_wahlbezirkID_teamID_and_timestamp_when_dataIsGiven() {
        val timestamp = LocalDateTime.parse("2026-01-01T10:00:00.000");
        val result =
            unitUnderTest.toEntityWithLastSeen(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"), "teamID", timestamp);

        val expected =
            new WahllokalZustand(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"),
                "teamID",
                timestamp,
                null,
                null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }

      @Test
      void should_map_null_wahlbezirkID_when_wahlbezirkIDIsNull() {
        val timestamp = LocalDateTime.parse("2026-01-01T10:00:00.000");
        val result = unitUnderTest.toEntityWithLastSeen(null, "teamID", timestamp);

        val expected = new WahllokalZustand(null, "teamID", timestamp, null, null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }

      @Test
      void should_map_null_teamID_when_teamIDIsNull() {
        val timestamp = LocalDateTime.parse("2026-01-01T10:00:00.000");
        val result =
            unitUnderTest.toEntityWithLastSeen(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"), null, timestamp);

        val expected =
            new WahllokalZustand(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"),
                null,
                timestamp,
                null,
                null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }

      @Test
      void should_map_null_lastSeen_when_timestampIsNull() {
        val result =
            unitUnderTest.toEntityWithLastSeen(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"), "teamID", null);

        val expected =
            new WahllokalZustand(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"),
                "teamID",
                null,
                null,
                null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }
    }

    @Nested
    class OfEntityWithLetzteAbmeldung {

      @Test
      void should_map_wahlbezirkID_teamID_and_letzteAbmeldung_when_dataIsGiven() {
        val timestamp = LocalDateTime.parse("2026-02-02T11:11:11.111");
        val result =
            unitUnderTest.toEntityWithLetzteAbmeldung(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"), "teamID", timestamp);

        val expected =
            new WahllokalZustand(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"),
                "teamID",
                null,
                timestamp,
                null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }

      @Test
      void should_map_null_wahlbezirkID_when_wahlbezirkIDIsNull() {
        val timestamp = LocalDateTime.parse("2026-02-02T11:11:11.111");
        val result = unitUnderTest.toEntityWithLetzteAbmeldung(null, "teamID", timestamp);

        val expected = new WahllokalZustand(null, "teamID", null, timestamp, null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }

      @Test
      void should_map_null_teamID_when_teamIDIsNull() {
        val timestamp = LocalDateTime.parse("2026-02-02T11:11:11.111");
        val result =
            unitUnderTest.toEntityWithLetzteAbmeldung(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"), null, timestamp);

        val expected =
            new WahllokalZustand(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"),
                null,
                null,
                timestamp,
                null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }

      @Test
      void should_map_null_letzteAbmeldung_when_timestampIsNull() {
        val result =
            unitUnderTest.toEntityWithLetzteAbmeldung(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"), "teamID", null);

        val expected =
            new WahllokalZustand(
                UUID.fromString("2853ba2d-baaa-49ee-93f7-a653d17d6a72"),
                "teamID",
                null,
                null,
                null);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
      }
    }
  }
}
