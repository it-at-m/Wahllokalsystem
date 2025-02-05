package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MappingTest {

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    Mapping unitUnderTest;

    @Nested
    class ToEntity {

        @Test
        void should_mapToAWerteDTO_when_aWerteEntityIsGiven() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val a1 = 11L;
            val a2 = 22L;
            val entityToMap = new AWerte(new BezirkUndWahlID(wahlID, wahlbezirkID), a1, a2);

            val result = unitUnderTest.toEntity(entityToMap);

            val expectedResult = new AWerteDTO().a1(a1).a2(a2);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_createEmptyAWerteDTO_when_aWerteEntityIsNull() {
            val result = unitUnderTest.toEntity(null);

            Assertions.assertThat(result).isEqualTo(new AWerteDTO());
        }
    }

    @Nested
    class ToAoueaiErgebnisseSet {

        @Test
        void should_mapToSetWithDTO_when_listOfEntitiesIsGiven() {
            val entitiesToMap = List.of(
                    new Ergebnisse(new BezirkUndWahlIDStapelart("wbzID1", "wahlID1", Stapelart.BTW_A),
                            List.of(new Ergebnis("wahlvorschlag11", "kandidat11", 1L, 11, null),
                                    new Ergebnis("wahlvorschlag12", "kandidat12", 2L, 12, null))),
                    new Ergebnisse(new BezirkUndWahlIDStapelart("wbzID2", "wahlID2", Stapelart.EUW_C_GUELTIG),
                            List.of(new Ergebnis("wahlvorschlag21", "kandidat21", 3L, 21, null),
                                    new Ergebnis("wahlvorschlag22", "kandidat22", 4L, 22, null))));

            val result = unitUnderTest.toAoueaiErgebnisseSet(entitiesToMap);

            val expectedResult = Set.of(
                    new ErgebnisDTO().stimmenart("BTW_A").ergebnis(11L).kandidatID("kandidat11").wahlvorschlagID("wahlvorschlag11")
                            .wahlvorschlagsordnungszahl(1L),
                    new ErgebnisDTO().stimmenart("BTW_A").ergebnis(12L).kandidatID("kandidat12").wahlvorschlagID("wahlvorschlag12")
                            .wahlvorschlagsordnungszahl(2L),
                    new ErgebnisDTO().stimmenart("EUW_C_GUELTIG").ergebnis(21L).kandidatID("kandidat21").wahlvorschlagID("wahlvorschlag21")
                            .wahlvorschlagsordnungszahl(3L),
                    new ErgebnisDTO().stimmenart("EUW_C_GUELTIG").ergebnis(22L).kandidatID("kandidat22").wahlvorschlagID("wahlvorschlag22")
                            .wahlvorschlagsordnungszahl(4L));

            Assertions.assertThat(result)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedResult);
        }

    }

    @Nested
    class ToWahlart {

        @ParameterizedTest
        @EnumSource(WahlartModel.class)
        void should_returnWahlartEnumWithSameName_when_wahlartModelIsGiven(final WahlartModel wahlartModel) {
            Assertions.assertThat(unitUnderTest.toWahlart(wahlartModel).name()).isEqualTo(wahlartModel.name());
        }
    }

    @Nested
    class ToDtoSet {

        @Test
        void should_returnSetWithUngueltigeStimmzettelDTO_when_listWithErgebnisseIsGiven() {
            val listWithErgebnisse = List.of(
                    new Ergebnisse(new BezirkUndWahlIDStapelart("wbzID1", "wahlID1", Stapelart.BTW_A),
                            List.of(new Ergebnis("wahlvorschlag11", null, null, 11, null),
                                    new Ergebnis("wahlvorschlag12", null, null, 12, null))),
                    new Ergebnisse(new BezirkUndWahlIDStapelart("wbzID2", "wahlID2", Stapelart.EUW_C_GUELTIG),
                            List.of(new Ergebnis("wahlvorschlag21", null, null, 21, null),
                                    new Ergebnis("wahlvorschlag22", null, null, 22, null))));

            val result = unitUnderTest.toDtoSet(listWithErgebnisse);

            val expectedResult = Set.of(
                    new UngueltigeStimmzettelDTO().wahlvorschlagID("wahlvorschlag11").anzahl(11L).stimmenart("BTW_A"),
                    new UngueltigeStimmzettelDTO().wahlvorschlagID("wahlvorschlag12").anzahl(12L).stimmenart("BTW_A"),
                    new UngueltigeStimmzettelDTO().wahlvorschlagID("wahlvorschlag21").anzahl(21L).stimmenart("EUW_C_GUELTIG"),
                    new UngueltigeStimmzettelDTO().wahlvorschlagID("wahlvorschlag22").anzahl(22L).stimmenart("EUW_C_GUELTIG"));

            Assertions.assertThat(result)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Stapelart.class)
        void should_mapToStimmenart_when_anyStapelIsGiven(final Stapelart stapelart) {
            val listWithErgebnisse = List.of(
                    new Ergebnisse(new BezirkUndWahlIDStapelart("wbzID1", "wahlID1", stapelart),
                            List.of(new Ergebnis("wahlvorschlag11", null, null, 11, null))));

            val result = unitUnderTest.toDtoSet(listWithErgebnisse);

            val expectedResult = Set.of(
                    new UngueltigeStimmzettelDTO().wahlvorschlagID("wahlvorschlag11").anzahl(11L).stimmenart(stapelart.name()));

            Assertions.assertThat(result)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedResult);
        }

    }

    @Nested
    class ToDTO {

        @ParameterizedTest(name = "given: {0} - expected: {1}")
        @MethodSource("argumentsWithGivenAndExpectedValues")
        void should_returnMeldungsartEnum_when_meldungsartModelIsGiven(final ArgumentsAccessor arguments) {
            val result = unitUnderTest.toDTO(arguments.get(0, MeldungsartModel.class));

            val expectedResult = arguments.get(1, ErgebnismeldungDTO.MeldungsartEnum.class);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        public static Stream<Arguments> argumentsWithGivenAndExpectedValues() {
            return Stream.of(
                    Arguments.of(MeldungsartModel.V1, ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT),
                    Arguments.of(MeldungsartModel.V3, ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG));
        }
    }

    @Nested
    class GetWahlbezirkart {

        @ParameterizedTest
        @EnumSource(WahlbezirkArtModel.class)
        void should_returnResultOfAuthenticationService(final WahlbezirkArtModel wahlbezirkArt) {
            Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(wahlbezirkArt);

            Assertions.assertThat(unitUnderTest.getWahlbezirkart()).isEqualTo(wahlbezirkArt);

        }
    }

}
