package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.EingenommenerWahlscheinModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmzettelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.VermerkModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.WahldatenModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

public class StimmabgabevermerkeDTOMapperTest {

    StimmabgabevermerkeDTOMapper unitUnderTest = Mappers.getMapper(StimmabgabevermerkeDTOMapper.class);

    @Nested
    class ToStimmabgabevermerkeDTO {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toStimmabgabevermerkeDTO(null)).isNull();
        }

        @Test
        void should_returnDTO_when_givenModel() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 1L;
            val anzahlBlaetter = 4711L;

            val modelToMap = new StimmabgabevermerkeModel(
                    new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                    anzahlBlaetter,
                    Set.of(
                            Testdaten.Wahldaten.createModel(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                            Testdaten.Wahldaten.createModel(wahlbezirkID, wahlID, waehlerverzeichnisNummer + 1)));

            val result = unitUnderTest.toStimmabgabevermerkeDTO(modelToMap);

            val expectedResult = new StimmabgabevermerkeDTO(
                    wahlbezirkID,
                    waehlerverzeichnisNummer,
                    anzahlBlaetter,
                    Set.of(
                            Testdaten.Wahldaten.createDTO(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                            Testdaten.Wahldaten.createDTO(wahlbezirkID, wahlID, waehlerverzeichnisNummer + 1)));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(StimmzettelartModel.class)
        void should_mapToEnumWithSameName_when_givenModelStimmzettelartEnumValue(final StimmzettelartModel stimmzettelart) {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 1L;
            val anzahlBlaetter = 4711L;

            val modelToMap = new StimmabgabevermerkeModel(
                    new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                    anzahlBlaetter,
                    Set.of(
                            new WahldatenModel(
                                    wahlbezirkID, wahlID, waehlerverzeichnisNummer,
                                    Set.of(
                                            new VermerkModel(2, Set.of(new StimmzettelModel(20, stimmzettelart)))),
                                    Set.of(
                                            new EingenommenerWahlscheinModel(91, stimmzettelart)))));

            val result = unitUnderTest.toStimmabgabevermerkeDTO(modelToMap);

            Assertions.assertThat(result.wahldaten().stream().allMatch(
                    (wahldatenModel) -> wahldatenModel.vermerke().stream().allMatch(
                            (vermerk) -> vermerk.stimmzettel().stream().allMatch(
                                    (sz) -> sz.stimmzettelart().name().equals(stimmzettelart.name())))))
                    .isTrue();
            Assertions.assertThat(result.wahldaten().stream().allMatch(
                    (wahldatenModel) -> wahldatenModel.eingenommeneWahlscheine().stream().allMatch(
                            (wahlschein) -> wahlschein.stimmzettelart().name().equals(stimmzettelart.name()))))
                    .isTrue();
        }
    }

    @Nested
    class ToStimmabgabevermerkeModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toStimmabgabevermerkeModel(null)).isNull();
        }

        @Test
        void should_returnModel_when_givenDTO() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 1L;
            val anzahlBlaetter = 4711L;

            val dtoToMap = new StimmabgabevermerkeDTO(
                    wahlbezirkID,
                    waehlerverzeichnisNummer,
                    anzahlBlaetter,
                    Set.of(
                            Testdaten.Wahldaten.createDTO(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                            Testdaten.Wahldaten.createDTO(wahlbezirkID, wahlID, waehlerverzeichnisNummer + 1)));

            val result = unitUnderTest.toStimmabgabevermerkeModel(dtoToMap);

            val expectedResult = new StimmabgabevermerkeModel(
                    new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                    anzahlBlaetter,
                    Set.of(
                            Testdaten.Wahldaten.createModel(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                            Testdaten.Wahldaten.createModel(wahlbezirkID, wahlID, waehlerverzeichnisNummer + 1)));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(StimmzettelartDTO.class)
        void should_mapToEnumWithSameName_when_givenDTOStimmzettelartEnumValue(final StimmzettelartDTO stimmzettelart) {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 1L;
            val anzahlBlaetter = 4711L;

            val dtoToMap = new StimmabgabevermerkeDTO(
                    wahlbezirkID,
                    waehlerverzeichnisNummer,
                    anzahlBlaetter,
                    Set.of(
                            new WahldatenDTO(
                                    wahlbezirkID,
                                    wahlID,
                                    waehlerverzeichnisNummer,
                                    Set.of(
                                            new VermerkDTO(2, Set.of(new StimmzettelDTO(20, stimmzettelart)))),
                                    Set.of(
                                            new EingenommenerWahlscheinDTO(91, stimmzettelart)))));

            val result = unitUnderTest.toStimmabgabevermerkeModel(dtoToMap);

            Assertions.assertThat(result.wahldaten().stream().allMatch(
                    (wahldatenDTO) -> wahldatenDTO.vermerke().stream().allMatch(
                            (vermerk) -> vermerk.stimmzettel().stream().allMatch(
                                    (sz) -> sz.stimmzettelart().name().equals(stimmzettelart.name())))))
                    .isTrue();
            Assertions.assertThat(result.wahldaten().stream().allMatch(
                    (wahldatenDTO) -> wahldatenDTO.eingenommeneWahlscheine().stream().allMatch(
                            (wahlschein) -> wahlschein.stimmzettelart().name().equals(stimmzettelart.name()))))
                    .isTrue();
        }
    }
}
