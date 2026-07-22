package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.ErfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.TeamBezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class ErfassungTeamStatusModelMapperTest {

  ErfassungTeamStatusModelMapper unitUnderTest =
      Mappers.getMapper(ErfassungTeamStatusModelMapper.class);

  @Nested
  class ToModel {

    @Test
    void should_returnNull_when_givenNull() {
      Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
    }

    @Test
    void should_returnModel_when_givenEntityEnum() {
      val teamStatusToMap = Instancio.create(ErfassungTeamStatus.class);

      val result = unitUnderTest.toModel(teamStatusToMap);

      Assertions.assertThat(result.name()).isEqualTo(teamStatusToMap.name());
    }

    @ParameterizedTest
    @EnumSource(ErfassungTeamStatus.class)
    void should_mapToEnumWithSameName_when_givenEntityEnumValue(
        final ErfassungTeamStatus teamStatusToMap) {
      val result = unitUnderTest.toModel(teamStatusToMap);

      Assertions.assertThat(result.name()).isEqualTo(teamStatusToMap.name());
    }
  }

  @Nested
  class ToEntity {

    @Nested
    class OfWahlbezirkErfassungsteamIDModelAndErfassungTeamStatusModel {

      @Test
      void should_returnNull_when_givenNull() {
        Assertions.assertThat(unitUnderTest.toEntity(null, null)).isNull();
      }

      @Test
      void should_returnEntityWithIdAndStatus_when_givenIdAndStatus() {
        val idToMap = Instancio.create(TeamBezirkUndWahlIDModel.class);
        val statusToMap = Instancio.create(ErfassungTeamStatusModel.class);

        val result = unitUnderTest.toEntity(idToMap, statusToMap);

        val expectedResult =
            new StimmzettelerfassungTeamStatus(
                new TeamBezirkUndWahlID(idToMap.wahlID(), idToMap.wahlbezirkID(), idToMap.teamID()),
                ErfassungTeamStatus.valueOf(statusToMap.name()));
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      @Test
      void should_returnEntityWithNullStatus_when_givenStatusIsNull() {
        val idToMap = Instancio.create(TeamBezirkUndWahlIDModel.class);

        val result = unitUnderTest.toEntity(idToMap, null);

        val expectedResult =
            new StimmzettelerfassungTeamStatus(
                new TeamBezirkUndWahlID(idToMap.wahlID(), idToMap.wahlbezirkID(), idToMap.teamID()),
                null);
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      @Test
      void should_returnEntityWithNullId_when_givenIdIsNull() {
        val statusToMap = Instancio.create(ErfassungTeamStatusModel.class);

        val result = unitUnderTest.toEntity(null, statusToMap);

        val expectedResult =
            new StimmzettelerfassungTeamStatus(
                null, ErfassungTeamStatus.valueOf(statusToMap.name()));
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }

      @ParameterizedTest
      @EnumSource(ErfassungTeamStatusModel.class)
      void should_mapToEnumWithSameName_when_givenModelEnumValue(
          final ErfassungTeamStatusModel statusToMap) {
        val idToMap = Instancio.create(TeamBezirkUndWahlIDModel.class);

        val result = unitUnderTest.toEntity(idToMap, statusToMap);

        Assertions.assertThat(result.getStatus().name()).isEqualTo(statusToMap.name());
      }
    }

    @Nested
    class OfWahlbezirkErfassungsteamIDModel {

      @Test
      void should_returnNull_when_givenNull() {
        Assertions.assertThat(unitUnderTest.toEntity(null)).isNull();
      }

      @Test
      void should_returnEntityId_when_givenModelId() {
        val idToMap = Instancio.create(TeamBezirkUndWahlIDModel.class);

        val result = unitUnderTest.toEntity(idToMap);

        val expectedResult =
            new TeamBezirkUndWahlID(idToMap.wahlID(), idToMap.wahlbezirkID(), idToMap.teamID());
        Assertions.assertThat(result).isEqualTo(expectedResult);
      }
    }
  }
}
