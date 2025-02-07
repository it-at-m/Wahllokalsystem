package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai.Mapping;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.BWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.WahlartPredicateHolder;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErgebnismeldungMappingServiceTest {

    @Mock
    WahlartPredicateHolder wahlartPredicateHolder;

    @Mock
    StimmzettelumschlaegeRepository stimmzettelumschlaegeRepo;
    @Mock
    StimmabgabevermerkeRepository stimmabgabevermerkeRepo;
    @Mock
    AWerteRepository aWerteRepo;

    @Mock
    AuthenticationService authenticationService;
    @Mock
    ErgebnisseRepository ergebnisseRepo;
    @Mock
    BriefwahlClient briefwahlClient;

    @Mock
    Mapping mapping;

    @InjectMocks
    ErgebnismeldungMappingService unitUnderTest;

    @Nested
    class CreateErgebnismeldung {

        @Nested
        class ForWahlbezirkArtUWB {

            @Test
            void should_createErgebnismeldungWithAllDataSet_when_allDataAreRetrievable() {
                val wahlart = WahlartModel.LTW;
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val waehlverzeichnisNummer = 2L;
                val meldungsart = ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
                val hauptwahlbezirkID = "hauptwahlbezirkID";

                val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
                val mockedUserWahlbezirkart = WahlbezirkArtModel.UWB;
                val mockedValidStapel = Stapelart.LTW_BZW_A;
                val mockedInvalidStapel = Stapelart.BTW_B_I_UNGUELTIG;
                val mockedValidErgebnisse = createErgebnisse(wahlID, wahlbezirkID, mockedValidStapel);
                val mockedValidErgebniseMappedToDTO = Set.of(new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                        new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()));
                val mockedInvalidErgebnisse = createErgebnisse(wahlID, wahlbezirkID, mockedInvalidStapel);
                val mockedErgebnisse = List.of(mockedValidErgebnisse, mockedInvalidErgebnisse);
                val mockedInvalidErgebnisseMappedToUngueltigeStimmzettel = Set.of(new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                        new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                        new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()));
                val mockedAWerteEntity = new AWerte();
                val mockedAWerteMappedToDTO = new AWerteDTO().a1(12L).a2(21L);
                val mockedStimmabgabevermerke = createStimmabgabevermerke(wahlID, wahlbezirkID, waehlverzeichnisNummer);

                Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedUserWahlbezirkart);
                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(wahlbezirkID, wahlID)).thenReturn(mockedErgebnisse);
                Mockito.when(stimmabgabevermerkeRepo.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlverzeichnisNummer)))
                        .thenReturn(Optional.of(mockedStimmabgabevermerke));
                Mockito.when(wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(wahlart))
                        .thenReturn(stapelart -> !stapelart.equals(mockedValidStapel));
                Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(Optional.of(mockedAWerteEntity));
                Mockito.when(mapping.toClientDTO(mockedAWerteEntity)).thenReturn(mockedAWerteMappedToDTO);
                Mockito.when(mapping.toDtoErgebnisseSet(List.of(mockedValidErgebnisse))).thenReturn(mockedValidErgebniseMappedToDTO);
                Mockito.when(mapping.toDtoSet(List.of(mockedInvalidErgebnisse))).thenReturn(mockedInvalidErgebnisseMappedToUngueltigeStimmzettel);

                val result = unitUnderTest.createErgebnismeldung(wahlart, wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirkID);

                val expectedBWerte = new BWerteDTO()
                        .b2(43L) //sum is based on the generated Testdata
                        .b1(866L); //sum is based on the generated Testdata
                expectedBWerte.setB(expectedBWerte.getB1() + expectedBWerte.getB2());
                val expectedResult = new ErgebnismeldungDTO()
                        .wahlID(wahlID)
                        .wahlbezirkID(wahlbezirkID)
                        .meldungsart(meldungsart)
                        .aWerte(mockedAWerteMappedToDTO)
                        .bWerte(expectedBWerte)
                        .ergebnisse(mockedValidErgebniseMappedToDTO)
                        .ungueltigeStimmzettelAnzahl((long) mockedInvalidErgebnisse.getErgebnisse().size())
                        .ungueltigeStimmzettels(mockedInvalidErgebnisseMappedToUngueltigeStimmzettel)
                        .wahlart(mockedMappedWahlart);

                Assertions.assertThat(result)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expectedResult);
            }

            @Test
            void should_returnErgebnismeldungWithoutAWerte_when_noAWerteArePresent() {
                val wahlart = WahlartModel.LTW;
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val waehlverzeichnisNummer = 0L;
                val meldungsart = ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
                val hauptwahlbezirkID = "hauptwahlbezirkID";

                val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
                val mockedUserWahlbezirkart = WahlbezirkArtModel.UWB;
                val mockedStimmzettelumschlaege = new Stimmzettelumschlaege();

                Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedUserWahlbezirkart);
                Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(Optional.empty());
                Mockito.when(stimmzettelumschlaegeRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                        .thenReturn(Optional.of(mockedStimmzettelumschlaege));
                Mockito.when(stimmabgabevermerkeRepo.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlverzeichnisNummer)))
                        .thenReturn(Optional.of(new Stimmabgabevermerke()));
                Mockito.when(wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(wahlart)).thenReturn(stapelart -> true);

                val result = unitUnderTest.createErgebnismeldung(wahlart, wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirkID);

                Assertions.assertThat(result.getaWerte()).isNull();
            }

            @Test
            void should_returnErgebnismeldungWithoutAWerte_when_wahlbezirkIsNotUWB() {
                val wahlart = WahlartModel.LTW;
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val waehlverzeichnisNummer = 0L;
                val meldungsart = ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
                val hauptwahlbezirkID = "hauptwahlbezirkID";

                val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
                val mockedUserWahlbezirkart = WahlbezirkArtModel.BWB;
                val mockedStimmzettelumschlaege = new Stimmzettelumschlaege();

                Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedUserWahlbezirkart);
                Mockito.when(stimmzettelumschlaegeRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                        .thenReturn(Optional.of(mockedStimmzettelumschlaege));
                Mockito.when(wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(wahlart)).thenReturn(stapelart -> true);

                val result = unitUnderTest.createErgebnismeldung(wahlart, wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirkID);

                Assertions.assertThat(result.getaWerte()).isNull();
            }
        }

        @Nested
        class ForWahlbezirkArtBWB {

            @Test
            void should_createErgebnismeldungWithAllDataSet_when_allDataAreRetrievable() {
                val wahlart = WahlartModel.EUW;
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val waehlverzeichnisNummer = 2L;
                val meldungsart = ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
                val hauptwahlbezirkID = "hauptwahlbezirkID";

                val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
                val mockedUserWahlbezirkart = WahlbezirkArtModel.BWB;
                val mockedValidStapel = Stapelart.EUW_C_GUELTIG;
                val mockedInvalidStapel = Stapelart.EUW_C_UNGUELTIG;
                val mockedValidErgebnisse = createErgebnisse(wahlID, wahlbezirkID, mockedValidStapel);
                val mockedValidErgebniseMappedToDTO = Set.of(new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                        new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()));
                val mockedInvalidErgebnisse = createErgebnisse(wahlID, wahlbezirkID, mockedInvalidStapel);
                val mockedErgebnisse = List.of(mockedValidErgebnisse, mockedInvalidErgebnisse);
                val mockedInvalidErgebnisseMappedToUngueltigeStimmzettel = Set.of(new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                        new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                        new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()));
                val mockedStimmzettelumschlaege = createStimmzettelumschlaege(10);

                Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedUserWahlbezirkart);
                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(wahlbezirkID, wahlID)).thenReturn(mockedErgebnisse);
                Mockito.when(stimmzettelumschlaegeRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                        .thenReturn(Optional.of(mockedStimmzettelumschlaege));
                Mockito.when(wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(wahlart))
                        .thenReturn(stapelart -> !stapelart.equals(mockedValidStapel));
                Mockito.when(mapping.toDtoErgebnisseSet(List.of(mockedValidErgebnisse))).thenReturn(mockedValidErgebniseMappedToDTO);
                Mockito.when(mapping.toDtoSet(List.of(mockedInvalidErgebnisse))).thenReturn(mockedInvalidErgebnisseMappedToUngueltigeStimmzettel);

                val result = unitUnderTest.createErgebnismeldung(wahlart, wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirkID);

                val expectedBWerte = new BWerteDTO().b(mockedStimmzettelumschlaege.getAnzahlWaehler());
                val expectedResult = new ErgebnismeldungDTO()
                        .wahlID(wahlID)
                        .wahlbezirkID(wahlbezirkID)
                        .meldungsart(meldungsart)
                        .aWerte(null)
                        .bWerte(expectedBWerte)
                        .ergebnisse(mockedValidErgebniseMappedToDTO)
                        .ungueltigeStimmzettelAnzahl((long) mockedInvalidErgebnisse.getErgebnisse().size())
                        .ungueltigeStimmzettels(mockedInvalidErgebnisseMappedToUngueltigeStimmzettel)
                        .wahlart(mockedMappedWahlart);

                Assertions.assertThat(result)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expectedResult);
            }

            @ParameterizedTest
            @MethodSource("streamWithWahlartenThatUsesBriefwahlService")
            void should_getWahlbriefwerteFromBriefwahlService_when_wahlbezirkArtIsBWBIsNiederschriftAndWahlartMatches(final ArgumentsAccessor arguments) {
                val wahlart = arguments.get(0, WahlartModel.class);
                val wahlID = "wahlID";
                val wahlbezirkID = "wahlbezirkID";
                val waehlverzeichnisNummer = 2L;
                val meldungsart = ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
                val hauptwahlbezirkID = "hauptwahlbezirkID";

                val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
                val mockedUserWahlbezirkart = WahlbezirkArtModel.BWB;
                val mockedValidStapel = Stapelart.EUW_C_GUELTIG;
                val mockedStimmzettelumschlaege = createStimmzettelumschlaege(10);
                val mockedBriefwahlClientResponse = 23L;

                Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
                Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).thenReturn(mockedUserWahlbezirkart);
                Mockito.when(stimmzettelumschlaegeRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                        .thenReturn(Optional.of(mockedStimmzettelumschlaege));
                Mockito.when(wahlartPredicateHolder.getPredicateForStapelWithInvalidErgebnisse(wahlart))
                        .thenReturn(stapelart -> !stapelart.equals(mockedValidStapel));
                Mockito.when(briefwahlClient.getAnzahlZurueckgewiesenerWahlbriefe(eq(hauptwahlbezirkID), eq(wahlID), eq(waehlverzeichnisNummer)))
                        .thenReturn(mockedBriefwahlClientResponse);

                val result = unitUnderTest.createErgebnismeldung(wahlart, wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirkID);

                Assertions.assertThat(result.getWahlbriefeWerte().getZurueckgewiesenGesamt())
                        .isEqualTo(mockedBriefwahlClientResponse);
            }

            public static Stream<Arguments> streamWithWahlartenThatUsesBriefwahlService() {
                return Stream.of(
                        Arguments.of(WahlartModel.LTW),
                        Arguments.of(WahlartModel.BZW));
            }

        }
    }

    private Stimmabgabevermerke createStimmabgabevermerke(final String wahlID, final String wahlbezirkID, final Long waehlerverzeichnisNummer) {
        val stimmabgabevermerke = new Stimmabgabevermerke();

        val wahldaten = Testdaten.Wahldaten.createEntity(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

        stimmabgabevermerke.setWahldaten(Set.of(wahldaten));

        return stimmabgabevermerke;
    }

    private Ergebnisse createErgebnisse(final String wahlID, final String wahlbezirkID, final Stapelart stapelart) {
        val ergebnisse = new Ergebnisse();

        ergebnisse.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart));

        ergebnisse.setErgebnisse(List.of(new Ergebnis()));

        return ergebnisse;
    }

    private Stimmzettelumschlaege createStimmzettelumschlaege(final long anzahlWaehler) {
        val stimmzettelumschlaege = new Stimmzettelumschlaege();

        stimmzettelumschlaege.setAnzahlWaehler(anzahlWaehler);

        return stimmzettelumschlaege;
    }
}
