package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.EingenommenerWahlscheinDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTOMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmzettelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.VermerkDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.WahldatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.EingenommenerWahlscheinModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmzettelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.VermerkModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.WahldatenModel;
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
                            new WahldatenModel(
                                    new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                                    Set.of(
                                            new VermerkModel(2, Set.of(new StimmzettelModel(20, StimmzettelartModel.KLEIN))),
                                            new VermerkModel(2, Set.of(new StimmzettelModel(21, StimmzettelartModel.GROSS))),
                                            new VermerkModel(2, Set.of(new StimmzettelModel(22, StimmzettelartModel.BEIDE))),
                                            new VermerkModel(3, Set.of(new StimmzettelModel(30, StimmzettelartModel.KLEIN))),
                                            new VermerkModel(3, Set.of(new StimmzettelModel(31, StimmzettelartModel.GROSS))),
                                            new VermerkModel(3, Set.of(new StimmzettelModel(32, StimmzettelartModel.BEIDE))),
                                            new VermerkModel(4, Set.of(new StimmzettelModel(40, StimmzettelartModel.KLEIN))),
                                            new VermerkModel(4, Set.of(new StimmzettelModel(41, StimmzettelartModel.GROSS))),
                                            new VermerkModel(4, Set.of(new StimmzettelModel(42, StimmzettelartModel.BEIDE)))),
                                    Set.of(
                                            new EingenommenerWahlscheinModel(91, StimmzettelartModel.KLEIN),
                                            new EingenommenerWahlscheinModel(92, StimmzettelartModel.GROSS),
                                            new EingenommenerWahlscheinModel(93, StimmzettelartModel.BEIDE)))));

            val result = unitUnderTest.toStimmabgabevermerkeDTO(modelToMap);

            val expectedResult = new StimmabgabevermerkeDTO(
                    wahlbezirkID,
                    waehlerverzeichnisNummer,
                    anzahlBlaetter,
                    Set.of(
                            new WahldatenDTO(
                                    wahlbezirkID,
                                    wahlID,
                                    waehlerverzeichnisNummer,
                                    Set.of(
                                            new VermerkDTO(2, Set.of(new StimmzettelDTO(20, StimmzettelartDTO.KLEIN))),
                                            new VermerkDTO(2, Set.of(new StimmzettelDTO(21, StimmzettelartDTO.GROSS))),
                                            new VermerkDTO(2, Set.of(new StimmzettelDTO(22, StimmzettelartDTO.BEIDE))),
                                            new VermerkDTO(3, Set.of(new StimmzettelDTO(30, StimmzettelartDTO.KLEIN))),
                                            new VermerkDTO(3, Set.of(new StimmzettelDTO(31, StimmzettelartDTO.GROSS))),
                                            new VermerkDTO(3, Set.of(new StimmzettelDTO(32, StimmzettelartDTO.BEIDE))),
                                            new VermerkDTO(4, Set.of(new StimmzettelDTO(40, StimmzettelartDTO.KLEIN))),
                                            new VermerkDTO(4, Set.of(new StimmzettelDTO(41, StimmzettelartDTO.GROSS))),
                                            new VermerkDTO(4, Set.of(new StimmzettelDTO(42, StimmzettelartDTO.BEIDE)))),
                                    Set.of(
                                            new EingenommenerWahlscheinDTO(91, StimmzettelartDTO.KLEIN),
                                            new EingenommenerWahlscheinDTO(92, StimmzettelartDTO.GROSS),
                                            new EingenommenerWahlscheinDTO(93, StimmzettelartDTO.BEIDE)))));
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
                                    new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                                    Set.of(
                                            new VermerkModel(2, Set.of(new StimmzettelModel(20, stimmzettelart)))),
                                    Set.of(
                                            new EingenommenerWahlscheinModel(91, stimmzettelart)))));

            val result = unitUnderTest.toStimmabgabevermerkeDTO(modelToMap);

            Assertions.assertThat(result.wahldaten().stream().allMatch(
                    (wahldatenModel) -> wahldatenModel.vermerke().stream().allMatch(
                            (vermerk) -> vermerk.stimmzetteln().stream().allMatch(
                                    (sz) -> sz.stimmzettelart().name().equals(stimmzettelart.name())))))
                    .isTrue();
            Assertions.assertThat(result.wahldaten().stream().allMatch(
                    (wahldatenModel) -> wahldatenModel.eingenommenewahlscheine().stream().allMatch(
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
                            new WahldatenDTO(
                                    wahlbezirkID,
                                    wahlID,
                                    waehlerverzeichnisNummer,
                                    Set.of(
                                            new VermerkDTO(2, Set.of(new StimmzettelDTO(20, StimmzettelartDTO.KLEIN))),
                                            new VermerkDTO(2, Set.of(new StimmzettelDTO(21, StimmzettelartDTO.GROSS))),
                                            new VermerkDTO(2, Set.of(new StimmzettelDTO(22, StimmzettelartDTO.BEIDE))),
                                            new VermerkDTO(3, Set.of(new StimmzettelDTO(30, StimmzettelartDTO.KLEIN))),
                                            new VermerkDTO(3, Set.of(new StimmzettelDTO(31, StimmzettelartDTO.GROSS))),
                                            new VermerkDTO(3, Set.of(new StimmzettelDTO(32, StimmzettelartDTO.BEIDE))),
                                            new VermerkDTO(4, Set.of(new StimmzettelDTO(40, StimmzettelartDTO.KLEIN))),
                                            new VermerkDTO(4, Set.of(new StimmzettelDTO(41, StimmzettelartDTO.GROSS))),
                                            new VermerkDTO(4, Set.of(new StimmzettelDTO(42, StimmzettelartDTO.BEIDE)))),
                                    Set.of(
                                            new EingenommenerWahlscheinDTO(91, StimmzettelartDTO.KLEIN),
                                            new EingenommenerWahlscheinDTO(92, StimmzettelartDTO.GROSS),
                                            new EingenommenerWahlscheinDTO(93, StimmzettelartDTO.BEIDE)))));

            val result = unitUnderTest.toStimmabgabevermerkeModel(dtoToMap);

            val expectedResult = new StimmabgabevermerkeModel(
                    new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                    anzahlBlaetter,
                    Set.of(
                            new WahldatenModel(
                                    new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                                    Set.of(
                                            new VermerkModel(2, Set.of(new StimmzettelModel(20, StimmzettelartModel.KLEIN))),
                                            new VermerkModel(2, Set.of(new StimmzettelModel(21, StimmzettelartModel.GROSS))),
                                            new VermerkModel(2, Set.of(new StimmzettelModel(22, StimmzettelartModel.BEIDE))),
                                            new VermerkModel(3, Set.of(new StimmzettelModel(30, StimmzettelartModel.KLEIN))),
                                            new VermerkModel(3, Set.of(new StimmzettelModel(31, StimmzettelartModel.GROSS))),
                                            new VermerkModel(3, Set.of(new StimmzettelModel(32, StimmzettelartModel.BEIDE))),
                                            new VermerkModel(4, Set.of(new StimmzettelModel(40, StimmzettelartModel.KLEIN))),
                                            new VermerkModel(4, Set.of(new StimmzettelModel(41, StimmzettelartModel.GROSS))),
                                            new VermerkModel(4, Set.of(new StimmzettelModel(42, StimmzettelartModel.BEIDE)))),
                                    Set.of(
                                            new EingenommenerWahlscheinModel(91, StimmzettelartModel.KLEIN),
                                            new EingenommenerWahlscheinModel(92, StimmzettelartModel.GROSS),
                                            new EingenommenerWahlscheinModel(93, StimmzettelartModel.BEIDE)))));
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
                            (vermerk) -> vermerk.stimmzetteln().stream().allMatch(
                                    (sz) -> sz.stimmzettelart().name().equals(stimmzettelart.name())))))
                    .isTrue();
            Assertions.assertThat(result.wahldaten().stream().allMatch(
                    (wahldatenDTO) -> wahldatenDTO.eingenommenewahlscheine().stream().allMatch(
                            (wahlschein) -> wahlschein.stimmzettelart().name().equals(stimmzettelart.name()))))
                    .isTrue();
        }

    }

}
