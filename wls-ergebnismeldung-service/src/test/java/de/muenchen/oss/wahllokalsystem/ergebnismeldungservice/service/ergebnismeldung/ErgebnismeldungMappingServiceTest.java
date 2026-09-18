package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai.Mapping;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.BWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlbriefeWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider.ErgebnismeldungErgebnisseService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider.ErgebnismeldungsErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErgebnismeldungMappingServiceTest {
  @Mock StimmzettelumschlaegeRepository stimmzettelumschlaegeRepo;
  @Mock StimmabgabevermerkeRepository stimmabgabevermerkeRepo;
  @Mock AWerteRepository aWerteRepo;
  @Mock MBWBedenklicheStimmzettelService mbwBedenklicheStimmzettelService;

  @Mock AuthenticationService authenticationService;
  @Mock BriefwahlClient briefwahlClient;

  @Mock
  ErgebnismeldungErgebnisseService ergebnismeldungErgebnisseService;

  @Mock Mapping mapping;

  @InjectMocks ErgebnismeldungMappingService unitUnderTest;

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
        val meldungsart = MeldungsartModel.V1;
        val hauptwahlbezirkID = "hauptwahlbezirkID";

        val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
        val mockedUserWahlbezirkart = WahlbezirkArtModel.UWB;
        val mockedValidErgebnisse = Instancio.create(ErgebnisseModel.class);
        val mockedValidErgebniseMappedToDTO =
            Set.of(
                new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()));
        val mockedInvalidErgebnisse = Instancio.create(ErgebnisseModel.class);
        val mockedInvalidErgebnisseMappedToUngueltigeStimmzettel =
            new HashSet<>(
                Set.of(
                    new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                    new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                    new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString())));
        val mockedAWerteEntity = new AWerte();
        val mockedAWerteMappedToDTO = new AWerteDTO().a1(12L).a2(21L);
        val mockedStimmabgabevermerke =
            createStimmabgabevermerke(wahlID, wahlbezirkID, waehlverzeichnisNummer);
        val anzahlUngueltigeBedenklicheStimmzettel = 2L;
        val mockedUngueltigeBedenklicheStimzettel =
            new UngueltigeStimmzettelDTO()
                .anzahl(anzahlUngueltigeBedenklicheStimmzettel)
                .stimmenart("MBW_E_UNGUELTIG");
        mockedInvalidErgebnisseMappedToUngueltigeStimmzettel.add(
            mockedUngueltigeBedenklicheStimzettel);

        Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
        Mockito.when(mapping.toDTO(meldungsart))
            .thenReturn(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(mockedUserWahlbezirkart);
        Mockito.when(
                stimmabgabevermerkeRepo.findById(
                    new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                        wahlbezirkID, wahlID, waehlverzeichnisNummer)))
            .thenReturn(Optional.of(mockedStimmabgabevermerke));
        Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(Optional.of(mockedAWerteEntity));
        Mockito.when(mapping.toClientDTO(mockedAWerteEntity)).thenReturn(mockedAWerteMappedToDTO);
        Mockito.when(mapping.toDtoErgebnisseSet(List.of(mockedValidErgebnisse)))
            .thenReturn(mockedValidErgebniseMappedToDTO);
        Mockito.when(mapping.toDtoSet(List.of(mockedInvalidErgebnisse)))
            .thenReturn(mockedInvalidErgebnisseMappedToUngueltigeStimmzettel);
        Mockito.when(
                ergebnismeldungErgebnisseService.getErgebnisse(
                    wahlID, wahlbezirkID, wahlart, meldungsart))
            .thenReturn(
                new ErgebnismeldungsErgebnisseModel(
                    List.of(mockedValidErgebnisse), List.of(mockedInvalidErgebnisse)));
        Mockito.when(
                mbwBedenklicheStimmzettelService.getAnzahlUngueltigeBedenklicheStimmzettel(
                    new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(anzahlUngueltigeBedenklicheStimmzettel);

        val result =
            unitUnderTest.createErgebnismeldung(
                wahlart,
                wahlID,
                wahlbezirkID,
                waehlverzeichnisNummer,
                meldungsart,
                hauptwahlbezirkID);

        val expectedBWerte =
            new BWerteDTO()
                .b2(43L) // sum is based on the generated Testdata
                .b1(866L); // sum is based on the generated Testdata
        expectedBWerte.setB(expectedBWerte.getB1() + expectedBWerte.getB2());
        val expectedResult =
            new ErgebnismeldungDTO()
                .wahlID(wahlID)
                .wahlbezirkID(wahlbezirkID)
                .meldungsart(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT)
                .aWerte(mockedAWerteMappedToDTO)
                .bWerte(expectedBWerte)
                .ergebnisse(mockedValidErgebniseMappedToDTO)
                .ungueltigeStimmzettelAnzahl((long) 1)
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
        val meldungsart = MeldungsartModel.V1;
        val hauptwahlbezirkID = "hauptwahlbezirkID";

        val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
        val mockedUserWahlbezirkart = WahlbezirkArtModel.UWB;
        val mockedStimmzettelumschlaege = new Stimmzettelumschlaege();

        Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
        Mockito.when(mapping.toDTO(meldungsart))
            .thenReturn(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(mockedUserWahlbezirkart);
        Mockito.when(
                ergebnismeldungErgebnisseService.getErgebnisse(
                    wahlID, wahlbezirkID, wahlart, meldungsart))
            .thenReturn(
                new ErgebnismeldungsErgebnisseModel(
                    Collections.emptyList(), Collections.emptyList()));
        Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(Optional.empty());
        Mockito.when(stimmzettelumschlaegeRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(Optional.of(mockedStimmzettelumschlaege));
        Mockito.when(
                stimmabgabevermerkeRepo.findById(
                    new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                        wahlbezirkID, wahlID, waehlverzeichnisNummer)))
            .thenReturn(Optional.of(new Stimmabgabevermerke()));

        val result =
            unitUnderTest.createErgebnismeldung(
                wahlart,
                wahlID,
                wahlbezirkID,
                waehlverzeichnisNummer,
                meldungsart,
                hauptwahlbezirkID);

        Assertions.assertThat(result.getaWerte()).isNull();
      }

      @Test
      void should_returnErgebnismeldungWithoutAWerte_when_wahlbezirkIsNotUWB() {
        val wahlart = WahlartModel.LTW;
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val waehlverzeichnisNummer = 0L;
        val meldungsart = MeldungsartModel.V1;
        val hauptwahlbezirkID = "hauptwahlbezirkID";

        val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
        val mockedUserWahlbezirkart = WahlbezirkArtModel.BWB;
        val mockedStimmzettelumschlaege = new Stimmzettelumschlaege();

        Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
        Mockito.when(mapping.toDTO(meldungsart))
            .thenReturn(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(mockedUserWahlbezirkart);
        Mockito.when(
                ergebnismeldungErgebnisseService.getErgebnisse(
                    wahlID, wahlbezirkID, wahlart, meldungsart))
            .thenReturn(
                new ErgebnismeldungsErgebnisseModel(
                    Collections.emptyList(), Collections.emptyList()));
        Mockito.when(stimmzettelumschlaegeRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(Optional.of(mockedStimmzettelumschlaege));

        val result =
            unitUnderTest.createErgebnismeldung(
                wahlart,
                wahlID,
                wahlbezirkID,
                waehlverzeichnisNummer,
                meldungsart,
                hauptwahlbezirkID);

        Assertions.assertThat(result.getaWerte()).isNull();
      }

      @Test
      void
          should_returnErgebnismeldungWithUngueltigeBedenklicheStimmzettel_when_noOtherUngueltigeStimmzettelArePresent() {
        val wahlart = WahlartModel.LTW;
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val waehlverzeichnisNummer = 0L;
        val meldungsart = MeldungsartModel.V1;
        val hauptwahlbezirkID = "hauptwahlbezirkID";

        val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
        val mockedUserWahlbezirkart = WahlbezirkArtModel.UWB;
        val mockedValidErgebnisse = Instancio.create(ErgebnisseModel.class);
        val mockedValidErgebniseMappedToDTO =
            Set.of(
                new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()));
        val mockedAWerteEntity = new AWerte();
        val mockedAWerteMappedToDTO = new AWerteDTO().a1(12L).a2(21L);
        val mockedStimmabgabevermerke =
            createStimmabgabevermerke(wahlID, wahlbezirkID, waehlverzeichnisNummer);
        val anzahlUngueltigeBedenklicheStimmzettel = 2L;
        val mockedUngueltigeBedenklicheStimzettel =
            new UngueltigeStimmzettelDTO()
                .anzahl(anzahlUngueltigeBedenklicheStimmzettel)
                .stimmenart("MBW_E_UNGUELTIG");

        Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
        Mockito.when(mapping.toDTO(meldungsart))
            .thenReturn(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(mockedUserWahlbezirkart);
        Mockito.when(
                stimmabgabevermerkeRepo.findById(
                    new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                        wahlbezirkID, wahlID, waehlverzeichnisNummer)))
            .thenReturn(Optional.of(mockedStimmabgabevermerke));
        Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(Optional.of(mockedAWerteEntity));
        Mockito.when(mapping.toClientDTO(mockedAWerteEntity)).thenReturn(mockedAWerteMappedToDTO);
        Mockito.when(mapping.toDtoErgebnisseSet((List.of(mockedValidErgebnisse))))
            .thenReturn(mockedValidErgebniseMappedToDTO);
        Mockito.when(
                ergebnismeldungErgebnisseService.getErgebnisse(
                    wahlID, wahlbezirkID, wahlart, meldungsart))
            .thenReturn(
                new ErgebnismeldungsErgebnisseModel(
                    List.of(mockedValidErgebnisse), Collections.emptyList()));
        Mockito.when(
                mbwBedenklicheStimmzettelService.getAnzahlUngueltigeBedenklicheStimmzettel(
                    new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(anzahlUngueltigeBedenklicheStimmzettel);

        val result =
            unitUnderTest.createErgebnismeldung(
                wahlart,
                wahlID,
                wahlbezirkID,
                waehlverzeichnisNummer,
                meldungsart,
                hauptwahlbezirkID);

        Assertions.assertThat(result.getUngueltigeStimmzettels())
            .isEqualTo(Set.of(mockedUngueltigeBedenklicheStimzettel));
      }
    }

    @Nested
    class ForWahlbezirkArtBWB {

      @ParameterizedTest
      @EnumSource(MeldungsartModel.class)
      void should_createErgebnismeldungWithAllDataSet_when_allDataAreRetrievable(
          final MeldungsartModel meldungsart) {
        val wahlart = WahlartModel.EUW;
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val waehlverzeichnisNummer = 2L;
        val hauptwahlbezirkID = "hauptwahlbezirkID";

        val mockedMappedWahlart = ErgebnismeldungDTO.WahlartEnum.LTW;
        val mockedUserWahlbezirkart = WahlbezirkArtModel.BWB;
        val mockedValidErgebnisse = Instancio.create(ErgebnisseModel.class);
        val mockedValidErgebniseMappedToDTO =
            Set.of(
                new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                new ErgebnisDTO().wahlvorschlagID(UUID.randomUUID().toString()));
        val mockedInvalidErgebnisse = Instancio.create(ErgebnisseModel.class);
        val mockedInvalidErgebnisseMappedToUngueltigeStimmzettel =
            new HashSet<>(
                Set.of(
                    new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                    new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString()),
                    new UngueltigeStimmzettelDTO().wahlvorschlagID(UUID.randomUUID().toString())));
        val mockedStimmzettelumschlaege = createStimmzettelumschlaege(10);
        val mockedBriefwahlClientResponse = 23L;
        val anzahlUngueltigeBedenklicheStimmzettel = 2L;
        val mockedUngueltigeBedenklicheStimzettel =
            new UngueltigeStimmzettelDTO()
                .anzahl(anzahlUngueltigeBedenklicheStimmzettel)
                .stimmenart("MBW_E_UNGUELTIG");
        mockedInvalidErgebnisseMappedToUngueltigeStimmzettel.add(
            mockedUngueltigeBedenklicheStimzettel);

        Mockito.when(
                ergebnismeldungErgebnisseService.getErgebnisse(
                    wahlID, wahlbezirkID, wahlart, meldungsart))
            .thenReturn(
                new ErgebnismeldungsErgebnisseModel(
                    List.of(mockedValidErgebnisse), List.of(mockedInvalidErgebnisse)));
        Mockito.when(mapping.toWahlartDTO(wahlart)).thenReturn(mockedMappedWahlart);
        if (meldungsart.equals(MeldungsartModel.V1)) {
          Mockito.when(mapping.toDTO(MeldungsartModel.V1))
              .thenReturn(ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT);
        }
        if (meldungsart.equals(MeldungsartModel.V3)) {
          Mockito.when(mapping.toDTO(MeldungsartModel.V3))
              .thenReturn(ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG);
        }
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(mockedUserWahlbezirkart);
        Mockito.when(stimmzettelumschlaegeRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(Optional.of(mockedStimmzettelumschlaege));
        Mockito.when(mapping.toDtoErgebnisseSet(List.of(mockedValidErgebnisse)))
            .thenReturn(mockedValidErgebniseMappedToDTO);
        Mockito.when(mapping.toDtoSet(List.of(mockedInvalidErgebnisse)))
            .thenReturn(mockedInvalidErgebnisseMappedToUngueltigeStimmzettel);
        if (meldungsart.equals(MeldungsartModel.V1)) {
          Mockito.when(
                  briefwahlClient.getAnzahlZurueckgewiesenerWahlbriefe(
                      eq(hauptwahlbezirkID), eq(wahlID), eq(waehlverzeichnisNummer)))
              .thenReturn(mockedBriefwahlClientResponse);
        }
        Mockito.when(
                mbwBedenklicheStimmzettelService.getAnzahlUngueltigeBedenklicheStimmzettel(
                    new BezirkUndWahlID(wahlID, wahlbezirkID)))
            .thenReturn(anzahlUngueltigeBedenklicheStimmzettel);

        val result =
            unitUnderTest.createErgebnismeldung(
                wahlart,
                wahlID,
                wahlbezirkID,
                waehlverzeichnisNummer,
                meldungsart,
                hauptwahlbezirkID);

        val expectedWahlbriefWerte =
            meldungsart.equals(MeldungsartModel.V1)
                ? new WahlbriefeWerteDTO().zurueckgewiesenGesamt(mockedBriefwahlClientResponse)
                : null;
        val expectedBWerte = new BWerteDTO().b(mockedStimmzettelumschlaege.getAnzahlWaehler());
        val expectedResult =
            new ErgebnismeldungDTO()
                .wahlID(wahlID)
                .wahlbezirkID(wahlbezirkID)
                .meldungsart(
                    meldungsart.equals(MeldungsartModel.V1)
                        ? ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT
                        : ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG)
                .aWerte(null)
                .bWerte(expectedBWerte)
                .ergebnisse(mockedValidErgebniseMappedToDTO)
                .ungueltigeStimmzettelAnzahl((long) 1)
                .ungueltigeStimmzettels(mockedInvalidErgebnisseMappedToUngueltigeStimmzettel)
                .wahlbriefeWerte(expectedWahlbriefWerte)
                .wahlart(mockedMappedWahlart);

        Assertions.assertThat(result)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedResult);
      }
    }
  }

  private Stimmabgabevermerke createStimmabgabevermerke(
      final String wahlID, final String wahlbezirkID, final Long waehlerverzeichnisNummer) {

    return Testdaten.Stimmabgabevermerke.createEntity(
        wahlbezirkID, wahlID, waehlerverzeichnisNummer);
  }

  private Stimmzettelumschlaege createStimmzettelumschlaege(final long anzahlWaehler) {
    val stimmzettelumschlaege = new Stimmzettelumschlaege();

    stimmzettelumschlaege.setAnzahlWaehler(anzahlWaehler);

    return stimmzettelumschlaege;
  }
}
