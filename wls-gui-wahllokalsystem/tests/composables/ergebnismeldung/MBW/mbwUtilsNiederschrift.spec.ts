import type { NiederschriftDruckInputBWB } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBWB.ts";
import type { NiederschriftDruckInputUWB } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputUWB.ts";
import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand.ts";

import { useBeanstandeteWahlbriefeTestDataFactory } from "@tests/utils/briefwahl/BeanstandeteWahlbriefeTestDataFactory.ts";
import { useWahlbriefdatenTestDataFactory } from "@tests/utils/briefwahl/WahlbriefdatenTestDataFactory.ts";
import { useAWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/aWerteTestDataFactory.ts";
import { useBegruendungTestDataFactory } from "@tests/utils/ergebnismeldung/common/begruendungTestDataFactory.ts";
import { useBWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/bWerteTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/common/statusTestDataFactory.ts";
import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/common/wahlscheineTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useVorfaelleundvorkommnisseTestDataFactory } from "@tests/utils/vorfaelleundvorkommnisse/VorfaelleundvorkommnisseTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { usePflegeWaehlerverzeichnisTestDataFactory } from "@tests/utils/wahlhandlung/PflegeWaehlerverzeichnisTestDataFactory.ts";
import { useWahlvorbereitungTestDataFactory } from "@tests/utils/wahlhandlung/WahlvorbereitungTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useMbtUtilsNiederschrift } from "@/composables/ergebnismeldung/MBW/mbwUtilsNiederschrift.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getAWerte: vi.fn(),
  getUrnenwahlvorbereitung: vi.fn(),
  getStimmabgabevermerke: vi.fn(),
  getBegruendungStimmzettelumschlaege: vi.fn(),
  getWaehlerverzeichnis: vi.fn(),
  getWahlvorstand: vi.fn(),
  getWahlscheine: vi.fn(),
  getBWerteForWahlbezirkAndWahl: vi.fn(),
  getErgebnisseByWahlIdAndStapelartOrUndefined: vi.fn(),
  getWahlvorschlaegeByWahlIDAndWahlbezirkID: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  createDefaultPflegeWaehlerverzeichnis: vi.fn(),
  wahlenState: vi.fn(),
  getErgebnisse: vi.fn(),
  createBarcode: vi.fn(),
  createFooter: vi.fn(),
}));

vi.mock("jsbarcode");
vi.mock("@/composables/ergebnismeldung/common/aWerteService.ts", () => ({
  useAWerteService: () => ({ getAWerte: mockDefinitions.getAWerte }),
}));
vi.mock("@/composables/wahlhandlung/wahlvorbereitungService.ts", () => ({
  useWahlvorbereitungService: () => ({
    getUrnenwahlvorbereitung: mockDefinitions.getUrnenwahlvorbereitung,
  }),
}));
vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      getStimmabgabevermerke: mockDefinitions.getStimmabgabevermerke,
    }),
  })
);
vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getBegruendungStimmzettelumschlaege:
      mockDefinitions.getBegruendungStimmzettelumschlaege,
    getErgebnisse: mockDefinitions.getErgebnisse,
  }),
}));
vi.mock("@/composables/wahlhandlung/waehlerverzeichnisService.ts", () => ({
  useWaehlerverzeichnisService: () => ({
    getWaehlerverzeichnis: mockDefinitions.getWaehlerverzeichnis,
    createDefaultPflegeWaehlerverzeichnis:
      mockDefinitions.createDefaultPflegeWaehlerverzeichnis,
  }),
}));
vi.mock("@/composables/wahlvorstand/wahlvorstandService.ts", () => ({
  useWahlvorstandService: () => ({
    getWahlvorstand: mockDefinitions.getWahlvorstand,
  }),
}));
vi.mock("@/composables/ergebnismeldung/common/wahlscheineService.ts", () => ({
  useWahlscheineService: () => ({
    getWahlscheine: mockDefinitions.getWahlscheine,
  }),
}));
vi.mock("@/composables/ergebnismeldung/MBW/mbwUtils.ts", () => ({
  useMbwUtils: () => ({
    getBWerteForWahlbezirkAndWahl:
      mockDefinitions.getBWerteForWahlbezirkAndWahl,
    _createBarcode: mockDefinitions.createBarcode,
    _createFooter: mockDefinitions.createFooter,
  }),
}));
vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: () => ({
    getErgebnisseByWahlIdAndStapelartOrUndefined:
      mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined,
  }),
}));

describe("mbwUtilsNiederschrift prepareDataForNiederschriftDruck", () => {
  const { prepareWahl } = useWahlTestDataFactory();
  const { prepareUser } = useUserTestDataFactory();
  const { prepareWahlvorschlag } = useWahlvorschlaegeTestDataFactory();
  const { prepareBWerte } = useBWerteTestDataFactory();
  const { prepareAWerte } = useAWerteTestDataFactory();
  const { createWahlbriefdaten } = useWahlbriefdatenTestDataFactory();
  const {
    createStimmabgabevermerke,
    prepareStimmabgabevermerke,
    prepareWahldaten,
    prepareVermerk,
    prepareStimmzettel,
  } = useStimmabgabevermerkeTestDataFactory();
  const { prepareErgebnisse, prepareErgebnis, createErgebnis } =
    useErgebnisseTestDataFactory();
  const { createStatus } = useStatusTestDataFactory();
  const {
    createBeanstandeteWahlbriefe,
    createRandomBeanstandeteWahlbriefeValues,
    prepareBeanstandeteWahlbriefe,
  } = useBeanstandeteWahlbriefeTestDataFactory();
  const { createWahlscheine } = useWahlscheineTestDataFactory();
  const { createEreignis, prepareEreignis } =
    useVorfaelleundvorkommnisseTestDataFactory();
  const { createWahlvorstand } = useWahlvorstandTestDataFactory();
  const { createBegruendung } = useBegruendungTestDataFactory();
  const { createUrnenwahlvorbereitung } = useWahlvorbereitungTestDataFactory();
  const { createPflegeWaehlerverzeichnis } =
    usePflegeWaehlerverzeichnisTestDataFactory();

  const { toHhMm, toGermanDate } = useDateTimeFormatter();

  const wahlID = "wahl-id";
  const wahlbezirkID = "wahlbezirk-id";

  let unitUnderTest: ReturnType<typeof useMbtUtilsNiederschrift>;

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("PrepareDataForUWB", async () => {
    const mockedWahlbezirkNummer = "3";
    const mockedWaehlerverzeichnisNummer = 3;
    const eroeffnungsuhrzeit = new Date("2025-05-23T06:30:00");
    const schliessungsuhrzeit = new Date("2025-05-23T16:30:00");

    const userStore = useUserStore().setUser(
      prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .wahlbezirkNummer(mockedWahlbezirkNummer)
        .build()
    );
    const wahlbezirkStore = useWahlbezirkStore();

    const inputWahl = prepareWahl()
      .wahlID(wahlID)
      .waehlerverzeichnisNummer(mockedWaehlerverzeichnisNummer)
      .build();
    const expectedWahltagFormatiert = toGermanDate(inputWahl.wahltag);
    useWahlenStore().wahlenState.wahlen = [inputWahl];

    const expectedBarcode = "barcodeString";
    mockDefinitions.createBarcode.mockReturnValue(expectedBarcode);

    const mockedWahlvorstand: Wahlvorstand = createWahlvorstand(2);
    mockDefinitions.getWahlvorstand.mockReturnValue(mockedWahlvorstand);

    wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit =
      eroeffnungsuhrzeit;
    wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit =
      schliessungsuhrzeit;

    const mockedAnzahlStimmzettelKlein = 10;
    const mockedWahldaten = prepareWahldaten()
      .wahlID(wahlID)
      .waehlerverzeichnisNummer(mockedWaehlerverzeichnisNummer)
      .eingenommeneWahlscheine(
        new Map([
          [StimmzettelStimmzettelartEnum.Klein, mockedAnzahlStimmzettelKlein],
        ])
      )
      .build();
    const mockedStimmabgabevermerke = prepareStimmabgabevermerke()
      .wahldaten([mockedWahldaten])
      .build();
    mockDefinitions.getStimmabgabevermerke.mockReturnValue(
      mockedStimmabgabevermerke
    );

    const mockedBegruendung = createBegruendung();
    mockDefinitions.getBegruendungStimmzettelumschlaege.mockReturnValue(
      mockedBegruendung
    );

    const mockedBWerte = prepareBWerte().b1(3).b2(2).build();
    mockDefinitions.getBWerteForWahlbezirkAndWahl.mockResolvedValue(
      mockedBWerte
    );

    const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
    const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();
    useWahlvorschlaegeStore().wahlvorschlaege.push({
      wahlID: wahlID,
      wahlbezirkID: wahlbezirkID,
      stimmzettelgebietID: "id",
      wahlvorschlaege: [wahlvorschlag1, wahlvorschlag2],
    });

    const ergebnisseA = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwA,
      })
      .ergebnisse([
        {
          wahlvorschlagID: wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
          ergebnis: 1,
          numIndex: null,
        },
        {
          wahlvorschlagID: wahlvorschlag2.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag2.ordnungszahl,
          ergebnis: 2,
          numIndex: null,
        },
      ])
      .build();
    const ergebnisseB = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwB,
      })
      .ergebnisse([
        {
          wahlvorschlagID: wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
          ergebnis: 3,
          numIndex: null,
        },
        {
          wahlvorschlagID: wahlvorschlag2.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag2.ordnungszahl,
          ergebnis: 4,
          numIndex: null,
        },
      ])
      .build();
    const ergebnisseBC = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwBC,
      })
      .ergebnisse([])
      .build();
    const ergebnisseD = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwDUngueltig,
      })
      .ergebnisse([
        {
          wahlvorschlagID: wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
          ergebnis: 7,
          numIndex: null,
        },
      ])
      .build();

    mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockImplementation(
      (id, stapel) => {
        if (stapel === StapelArtEnum.MbwA) return ergebnisseA;
        if (stapel === StapelArtEnum.MbwB) return ergebnisseB;
        if (stapel === StapelArtEnum.MbwBC) return ergebnisseBC;
        if (stapel === StapelArtEnum.MbwDUngueltig) return ergebnisseD;
        return undefined;
      }
    );

    mockDefinitions.getErgebnisse.mockReturnValue(ergebnisseD);

    const mockedFooter = "footer";
    mockDefinitions.createFooter.mockReturnValue(mockedFooter);

    const mockedUrnenwahlvorbereitung = createUrnenwahlvorbereitung();
    mockDefinitions.getUrnenwahlvorbereitung.mockReturnValue(
      mockedUrnenwahlvorbereitung
    );

    const mockedWaehlerverzeichnis = createPflegeWaehlerverzeichnis();
    mockDefinitions.getWaehlerverzeichnis.mockReturnValue(
      mockedWaehlerverzeichnis
    );

    const mockedAWerte = prepareAWerte()
      .bezirkUndWahlID({
        wahlbezirkID: wahlbezirkID,
        wahlID: inputWahl.wahlID,
      })
      .a1(1)
      .a2(3)
      .build();
    mockDefinitions.getAWerte.mockReturnValue([mockedAWerte]);

    const status = createStatus();
    status.niederschrift.validierungsstatus = "VALIDE";
    unitUnderTest = useMbtUtilsNiederschrift(wahlID, wahlbezirkID);
    const result = await unitUnderTest.prepareDataForNiederschriftDruck(
      status,
      MeldungsArtEnum.Niederschrift,
      inputWahl
    );

    const expectedResult: NiederschriftDruckInputUWB = {
      aktuelleWahl: inputWahl,
      wahltagFormatiert: expectedWahltagFormatiert,
      barcode: expectedBarcode,
      wahlbezirkNummer: mockedWahlbezirkNummer,
      wahlvorstaende: [
        {
          nachname: mockedWahlvorstand.wahlvorstandsmitglieder[0]?.familienname,
          vorname: mockedWahlvorstand.wahlvorstandsmitglieder[0]?.vorname,
          funktionsName:
            mockedWahlvorstand.wahlvorstandsmitglieder[0]?.funktionsname,
        },
        {
          nachname: mockedWahlvorstand.wahlvorstandsmitglieder[1]?.familienname,
          vorname: mockedWahlvorstand.wahlvorstandsmitglieder[1]?.vorname,
          funktionsName:
            mockedWahlvorstand.wahlvorstandsmitglieder[1]?.funktionsname,
        },
      ],
      eroeffnungsuhrzeit: {
        stunde: eroeffnungsuhrzeit?.getHours().toString() ?? "",
        minute: eroeffnungsuhrzeit?.getMinutes().toString() ?? "",
      },
      schliessungsuhrzeit: {
        stunde: schliessungsuhrzeit?.getHours().toString() ?? "",
        minute: schliessungsuhrzeit?.getMinutes().toString() ?? "",
      },
      anzahlStimmzettel: inputWahl.stimmzettelumschlaege.anzahlWaehler,
      anzahlWahlscheine: mockedAnzahlStimmzettelKlein,
      begruendungStimmzettelumschlaege: {
        grund: `${mockedBegruendung.grund} `,
      },
      bWerte: mockedBWerte.b,
      ungueltigeStimmen: ergebnisseD.ergebnisse.length,
      gueltigeStimmenListe: [
        {
          ordnungszahl: 1,
          bewerbername: wahlvorschlag1.kandidaten[0].name,
          parteiname: wahlvorschlag1.kurzname,
          stapelA: 1,
          stapelB: 3,
          stapelBC: 0,
          gesamt: 4,
        },
        {
          ordnungszahl: 2,
          bewerbername: wahlvorschlag2.kandidaten[0].name,
          parteiname: wahlvorschlag2.kurzname,
          stapelA: 2,
          stapelB: 4,
          stapelBC: 0,
          gesamt: 6,
        },
      ],
      gueltigeStimmenErgebnisGesamt: {
        stapelA: 3,
        stapelB: 7,
        stapelBC: 0,
        gesamt: 10,
      },
      ereignisse: { hasEreignisse: false, vorfaelle: [], vorkommnisse: [] },
      footer: mockedFooter,
      anzahlStimmabgabevermerke: mockedBWerte.b,
      aWerte: mockedAWerte.a1 + mockedAWerte.a2,
      a1: mockedAWerte.a1,
      a2: mockedAWerte.a2,
      anzahlWahltische: mockedUrnenwahlvorbereitung.anzahlWahltische,
      b1: mockedBWerte.b1,
      wvz: {
        nachtraeglicheBerichtigung:
          mockedWaehlerverzeichnis.nachtraeglicheBerichtigung,
        verzeichnisLagVor: mockedWaehlerverzeichnis.waehlerverzeichnisUnchanged,
        berichtigungVorBeginnDerAbstimmung:
          !mockedWaehlerverzeichnis.waehlerverzeichnisUnchanged,
      },
      parteienListe: [],
    };

    expect(result).toBeDefined();
    // es wird nur überprüft ob die Parteien liste existiert, weitere tests erfolgen nach dem Refactoring des dazugehörigen codes
    expect(result.parteienListe).toBeDefined();
    result.parteienListe = [];
    expect(result).toEqual(expectedResult);
  });

  it("PrepareDataForBWB", async () => {
    const mockedWahlbezirkNummer = "3";
    const mockedWaehlerverzeichnisNummer = 3;
    const eroeffnungsuhrzeit = new Date("2025-05-23T06:30:00");
    const schliessungsuhrzeit = new Date("2025-05-23T16:30:00");

    const userStore = useUserStore().setUser(
      prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .wahlbezirkNummer(mockedWahlbezirkNummer)

        .build()
    );
    const wahlbezirkStore = useWahlbezirkStore();

    const inputWahl = prepareWahl()
      .wahlID(wahlID)
      .waehlerverzeichnisNummer(mockedWaehlerverzeichnisNummer)
      .beanstandeteWahlbriefe([
        ZurueckweisungsgrundEnum.ScheinUngueltig,
        ZurueckweisungsgrundEnum.ScheinUngueltig,
        ZurueckweisungsgrundEnum.UmschlagFehlt,
      ])
      .build();
    const expectedWahltagFormatiert = toGermanDate(inputWahl.wahltag);
    useWahlenStore().wahlenState.wahlen = [inputWahl];

    const expectedBarcode = "barcodeString";
    mockDefinitions.createBarcode.mockReturnValue(expectedBarcode);

    const mockedWahlvorstand: Wahlvorstand = createWahlvorstand(2);
    mockDefinitions.getWahlvorstand.mockReturnValue(mockedWahlvorstand);

    wahlbezirkStore.eroeffnungsuhrzeitState.eroeffnungsuhrzeit =
      eroeffnungsuhrzeit;
    wahlbezirkStore.schliessungsuhrzeitState.schliessungsuhrzeit =
      schliessungsuhrzeit;

    const mockedWahlbriefdaten = createWahlbriefdaten();
    wahlbezirkStore.wahlbriefDatenState.wahlbriefDaten = mockedWahlbriefdaten;

    const mockedAnzahlStimmzettelKlein = 10;
    const mockedWahldaten = prepareWahldaten()
      .wahlID(wahlID)
      .waehlerverzeichnisNummer(mockedWaehlerverzeichnisNummer)
      .eingenommeneWahlscheine(
        new Map([
          [StimmzettelStimmzettelartEnum.Klein, mockedAnzahlStimmzettelKlein],
        ])
      )
      .build();
    const mockedStimmabgabevermerke = prepareStimmabgabevermerke()
      .wahldaten([mockedWahldaten])
      .build();
    mockDefinitions.getStimmabgabevermerke.mockReturnValue(
      mockedStimmabgabevermerke
    );

    const mockedBegruendung = createBegruendung();
    mockDefinitions.getBegruendungStimmzettelumschlaege.mockReturnValue(
      mockedBegruendung
    );

    const mockedBWerte = prepareBWerte().b1(3).b2(2).build();
    mockDefinitions.getBWerteForWahlbezirkAndWahl.mockResolvedValue(
      mockedBWerte
    );

    const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
    const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();
    useWahlvorschlaegeStore().wahlvorschlaege.push({
      wahlID: wahlID,
      wahlbezirkID: wahlbezirkID,
      stimmzettelgebietID: "id",
      wahlvorschlaege: [wahlvorschlag1, wahlvorschlag2],
    });

    const ergebnisseA = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwA,
      })
      .ergebnisse([
        {
          wahlvorschlagID: wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
          ergebnis: 1,
          numIndex: null,
        },
        {
          wahlvorschlagID: wahlvorschlag2.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag2.ordnungszahl,
          ergebnis: 2,
          numIndex: null,
        },
      ])
      .build();
    const ergebnisseB = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwB,
      })
      .ergebnisse([
        {
          wahlvorschlagID: wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
          ergebnis: 3,
          numIndex: null,
        },
        {
          wahlvorschlagID: wahlvorschlag2.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag2.ordnungszahl,
          ergebnis: 4,
          numIndex: null,
        },
      ])
      .build();
    const ergebnisseBC = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwBC,
      })
      .ergebnisse([])
      .build();
    const ergebnisseD = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID,
        wahlbezirkID,
        stapelArt: StapelArtEnum.MbwDUngueltig,
      })
      .ergebnisse([
        {
          wahlvorschlagID: wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
          ergebnis: 7,
          numIndex: null,
        },
      ])
      .build();

    mockDefinitions.getErgebnisseByWahlIdAndStapelartOrUndefined.mockImplementation(
      (id, stapel) => {
        if (stapel === StapelArtEnum.MbwA) return ergebnisseA;
        if (stapel === StapelArtEnum.MbwB) return ergebnisseB;
        if (stapel === StapelArtEnum.MbwBC) return ergebnisseBC;
        if (stapel === StapelArtEnum.MbwDUngueltig) return ergebnisseD;
        return undefined;
      }
    );

    mockDefinitions.getErgebnisse.mockReturnValue(ergebnisseD);

    const mockedFooter = "footer";
    mockDefinitions.createFooter.mockReturnValue(mockedFooter);

    const mockedWahlscheine = createWahlscheine();
    mockDefinitions.getWahlscheine.mockReturnValue(mockedWahlscheine);

    const status = createStatus();
    status.niederschrift.validierungsstatus = "VALIDE";
    unitUnderTest = useMbtUtilsNiederschrift(wahlID, wahlbezirkID);
    const result = await unitUnderTest.prepareDataForNiederschriftDruck(
      status,
      MeldungsArtEnum.Niederschrift,
      inputWahl
    );

    const expectedResult: NiederschriftDruckInputBWB = {
      aktuelleWahl: inputWahl,
      wahltagFormatiert: expectedWahltagFormatiert,
      barcode: expectedBarcode,
      wahlbezirkNummer: mockedWahlbezirkNummer,
      wahlvorstaende: [
        {
          nachname: mockedWahlvorstand.wahlvorstandsmitglieder[0]?.familienname,
          vorname: mockedWahlvorstand.wahlvorstandsmitglieder[0]?.vorname,
          funktionsName:
            mockedWahlvorstand.wahlvorstandsmitglieder[0]?.funktionsname,
        },
        {
          nachname: mockedWahlvorstand.wahlvorstandsmitglieder[1]?.familienname,
          vorname: mockedWahlvorstand.wahlvorstandsmitglieder[1]?.vorname,
          funktionsName:
            mockedWahlvorstand.wahlvorstandsmitglieder[1]?.funktionsname,
        },
      ],
      eroeffnungsuhrzeit: {
        stunde: eroeffnungsuhrzeit?.getHours().toString() ?? "",
        minute: eroeffnungsuhrzeit?.getMinutes().toString() ?? "",
      },
      schliessungsuhrzeit: {
        stunde: schliessungsuhrzeit?.getHours().toString() ?? "",
        minute: schliessungsuhrzeit?.getMinutes().toString() ?? "",
      },
      anzahlStimmzettel: inputWahl.stimmzettelumschlaege.anzahlWaehler,
      anzahlWahlscheine: mockedWahlscheine.stimmabgabevermerke,
      begruendungStimmzettelumschlaege: {
        grund: `${mockedBegruendung.grund} `,
      },
      bWerte: mockedBWerte.b,
      ungueltigeStimmen: ergebnisseD.ergebnisse.length,
      gueltigeStimmenListe: [
        {
          ordnungszahl: 1,
          bewerbername: wahlvorschlag1.kandidaten[0].name,
          parteiname: wahlvorschlag1.kurzname,
          stapelA: 1,
          stapelB: 3,
          stapelBC: 0,
          gesamt: 4,
        },
        {
          ordnungszahl: 2,
          bewerbername: wahlvorschlag2.kandidaten[0].name,
          parteiname: wahlvorschlag2.kurzname,
          stapelA: 2,
          stapelB: 4,
          stapelBC: 0,
          gesamt: 6,
        },
      ],
      gueltigeStimmenErgebnisGesamt: {
        stapelA: 3,
        stapelB: 7,
        stapelBC: 0,
        gesamt: 10,
      },
      ereignisse: { hasEreignisse: false, vorfaelle: [], vorkommnisse: [] },
      footer: mockedFooter,
      parteienListe: [],
      beanstandeteWahlbriefe: {
        gefaehrdetWahlgeheimnis: 0,
        gegenstandImUmschlag: 0,
        gesamt: 3,
        gesamtMinusZugelassen: 3,
        keinAmtlicherStimmzettelumschlag: 0,
        keinGueltigerWahlschein: 2,
        keinStimmzettelumschlag: 1,
        keineUnterschrift: 0,
        loseStimmzettel: 0,
        mehrereStimmzettelumschlaege: 0,
        nichtVerschlossen: 0,
        zugelassen: 0,
      },
      wahlbriefdaten: {
        wahlbriefe: mockedWahlbriefdaten.wahlbriefe,
        verzeichnisseUngueltige: mockedWahlbriefdaten.verzeichnisseUngueltige,
        nachtraege: mockedWahlbriefdaten.nachtraege,
        nachtraeglichUeberbrachte:
          mockedWahlbriefdaten.nachtraeglichUeberbrachte,
      },
    };

    expect(result).toBeDefined();
    // es wird nur überprüft ob die Parteien liste existiert, weitere tests erfolgen nach dem Refactoring des dazugehörigen codes
    expect(result.parteienListe).toBeDefined();
    result.parteienListe = [];
    expect(result).toEqual(expectedResult);
  });
});
