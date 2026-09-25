import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useAWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/aWerteTestDataFactory.ts";
import { useBWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/bWerteTestDataFactory.ts";
import { useErgebnismeldungDruckInputTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnismeldungDruckInputTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/common/statusTestDataFactory.ts";
import { useBedenklicherStimmzettelTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/bedenklicherStimmzettelTestDataFactory.ts";
import { useMbwErgebnisseAndWahlvorschlagTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagTestDataFactory.ts";
import { useStimmabgabevermerkeTestDataFactory } from "@tests/utils/stimmabgabevermerke/StimmabgabevermerkeTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { useMbwSchnellmeldungDruckUtils } from "@/composables/ergebnismeldung/MBW/mbwSchnellmeldungDruckUtils.ts";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  createBarcode: vi.fn(),
  createFooter: vi.fn(),
  getErgebnisse: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  getWahlvorschlaege: vi.fn(),
  mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse: vi.fn(),
  getWaehlerverzeichnisNummerOrUndefinedById: vi.fn(),
  getStimmabgabevermerke: vi.fn(),
  getStimmzettelumschlaege: vi.fn(),
  getAWerteForWahlbezirkAndWahl: vi.fn(),
  generateUuidv4: vi.fn(),
  getBedenklicheStimmzettel: vi.fn(),
}));

vi.mock(import("@/composables/drucken/commonPrintService.ts"), () => ({
  useCommonPrintService: () => ({
    createBarcode: mockDefinitions.createBarcode,
    createFooter: mockDefinitions.createFooter,
  }),
}));

vi.mock(
  import("@/composables/ergebnismeldung/common/ergebnisService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useErgebnisService: () => ({
        ...mod.useErgebnisService(),
        getErgebnisse: mockDefinitions.getErgebnisse,
        getStimmzettelumschlaege: mockDefinitions.getStimmzettelumschlaege,
      }),
    };
  }
);
vi.mock(
  import("@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts"),
  () => ({
    useBedenklicheStimmzettelService: () => ({
      getBedenklicheStimmzettel: mockDefinitions.getBedenklicheStimmzettel,
      saveBedenklicheStimmzettel: vi.fn(),
    }),
  })
);
vi.mock(
  "@/composables/ergebnismeldung/MBW/mbwErgebnisAndWahlvorschlagMapper.ts",
  () => ({
    useMbwErgebnisAndWahlvorschlagMapper: () => ({
      mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse:
        mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse,
    }),
  })
);
vi.mock(
  import("@/composables/wahlvorschlaege/wahlvorschlaegeService.ts"),
  () => ({
    useWahlvorschlaegeService: () => ({
      getWahlvorschlaege: mockDefinitions.getWahlvorschlaege,
    }),
  })
);
vi.mock(
  "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts",
  () => ({
    useStimmabgabevermerkeService: () => ({
      getStimmabgabevermerke: mockDefinitions.getStimmabgabevermerke,
    }),
  })
);
vi.mock(
  import("@/composables/ergebnismeldung/common/aWerteService.ts"),
  () => ({
    useAWerteService: () => ({
      getAWerte: vi.fn(),
      getAWerteForWahlbezirkAndWahl:
        mockDefinitions.getAWerteForWahlbezirkAndWahl,
    }),
  })
);
vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenActions: {
      getWahlOrUndefinedById: mockDefinitions.getWahlOrUndefinedById,
    },
    waehlerverzeichnisActions: {
      getWaehlerverzeichnisNummerOrUndefinedById:
        mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById,
    },
  }),
}));

const mockIsDseAktiv = ref(false);
vi.mock("@/stores/infomanagementStore.ts", () => ({
  useInfomanagementStore: () => ({
    isDseAktiv: mockIsDseAktiv,
  }),
}));

const { generateRandomString } = useCommonTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareWahl } = useWahlTestDataFactory();
const { createErgebnis, prepareErgebnisse, prepareErgebnis } =
  useErgebnisseTestDataFactory();
const { prepareWahlvorschlag, prepareWahlvorschlaege } =
  useWahlvorschlaegeTestDataFactory();
const { createStatus } = useStatusTestDataFactory();
const { prepareAWerte } = useAWerteTestDataFactory();
const { prepareBWerte } = useBWerteTestDataFactory();
const { prepareStimmabgabevermerke, prepareVermerk, prepareStimmzettel } =
  useStimmabgabevermerkeTestDataFactory();
const { prepareMbwErgebnisseAndWahlvorschlag } =
  useMbwErgebnisseAndWahlvorschlagTestDataFactory();
const { prepareBedenklicherStimmzettel } =
  useBedenklicherStimmzettelTestDataFactory();

const { prepareSchnellmeldungDruckInput } =
  useErgebnismeldungDruckInputTestDataFactory();

const { convertToSixDigitArray } = useNumberFormatter();

const mockedNow = new Date();
crypto.randomUUID = mockDefinitions.generateUuidv4;

describe("mbwSchnellmeldungDruckUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useMbwSchnellmeldungDruckUtils>;

  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  beforeEach(() => {
    createTestingPinia({ createSpy: vi.fn, stubActions: false });
    mockIsDseAktiv.value = false;
    unitUnderTest = useMbwSchnellmeldungDruckUtils(wahlID, wahlbezirkID);

    vi.useFakeTimers({
      now: mockedNow,
    });
    vi.setSystemTime(mockedNow);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("prepareDataForSchnellmeldungDruck", () => {
    it("should_returnErgebnismeldungDruckInput_when_givenWahlStatusAndMeldungsart", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      // --- prepare mock Values ---
      const wahl = prepareWahl().wahlID(wahlID).build();
      const status = createStatus();
      status.schnellmeldung.validierungsstatus =
        MeldungValidierungsstatusEnum.Valide;
      const meldungsArt = MeldungsArtEnum.Schnellmeldung;

      // mock aWerte
      const aWerte = prepareAWerte()
        .bezirkUndWahlID({
          wahlbezirkID: wahlbezirkID,
          wahlID: wahl.wahlID,
        })
        .build();
      mockDefinitions.getAWerteForWahlbezirkAndWahl.mockResolvedValue(aWerte);

      // mock bWerte
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        wahl.waehlerverzeichnisNummer
      );
      mockDefinitions.getStimmabgabevermerke.mockReturnValue(
        prepareStimmabgabevermerke()
          .eingenommeneWahlscheine(
            new Map([
              [StimmzettelStimmzettelartEnum.Klein, 1], // b2
              [StimmzettelStimmzettelartEnum.Beide, 1], // b2
            ])
          )
          .vermerke([
            prepareVermerk()
              .blattnummer(1)
              .stimmzettel([
                prepareStimmzettel()
                  .anzahl(1) // b1
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                  .build(),
                prepareStimmzettel()
                  .anzahl(1) // b1
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Beide)
                  .build(),
              ])
              .build(),
            prepareVermerk()
              .blattnummer(2)
              .stimmzettel([
                prepareStimmzettel()
                  .anzahl(1) // b1
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                  .build(),
              ])
              .build(),
          ])
          .build()
      );
      const expectedBWerte = prepareBWerte()
        .bezirkUndWahlID({
          wahlbezirkID: wahlbezirkID,
          wahlID: wahl.wahlID,
        })
        .b(5)
        .b1(3)
        .b2(2)
        .build();

      // wahlvorschlaege
      const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
      const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();
      const sortedWahlvorschlaege = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
        .build();
      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        sortedWahlvorschlaege
      );

      // stapel A
      const ergebnisA1 = prepareErgebnis()
        .wahlvorschlagID(wahlvorschlag1.identifikator)
        .wahlvorschlagsOrdnungszahl(wahlvorschlag1.ordnungszahl)
        .build();
      const ergebnisA2 = prepareErgebnis()
        .wahlvorschlagID(wahlvorschlag2.identifikator)
        .wahlvorschlagsOrdnungszahl(wahlvorschlag2.ordnungszahl)
        .build();
      const mockedErgebnisseStapelA: Ergebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: StapelArtEnum.MbwA,
        })
        .ergebnisse([
          {
            wahlvorschlagID: wahlvorschlag1.identifikator,
            kandidatID: null,
            wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
            ergebnis: ergebnisA1.ergebnis,
            numIndex: null,
          },
          {
            wahlvorschlagID: wahlvorschlag2.identifikator,
            kandidatID: null,
            wahlvorschlagsOrdnungszahl: wahlvorschlag2.ordnungszahl,
            ergebnis: ergebnisA2.ergebnis,
            numIndex: null,
          },
        ])
        .build();
      mockDefinitions.getErgebnisse.mockResolvedValueOnce(
        mockedErgebnisseStapelA
      );

      // stapel b
      const ergebnisB1 = prepareErgebnis()
        .wahlvorschlagID(wahlvorschlag1.identifikator)
        .wahlvorschlagsOrdnungszahl(wahlvorschlag1.ordnungszahl)
        .build();
      const ergebnisB2 = prepareErgebnis()
        .wahlvorschlagID(wahlvorschlag2.identifikator)
        .wahlvorschlagsOrdnungszahl(wahlvorschlag2.ordnungszahl)
        .build();
      const mockedErgebnisseStaplB = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: StapelArtEnum.MbwB,
        })
        .ergebnisse([
          {
            wahlvorschlagID: wahlvorschlag1.identifikator,
            kandidatID: null,
            wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
            ergebnis: ergebnisB1.ergebnis,
            numIndex: null,
          },
          {
            wahlvorschlagID: wahlvorschlag2.identifikator,
            kandidatID: null,
            wahlvorschlagsOrdnungszahl: wahlvorschlag2.ordnungszahl,
            ergebnis: ergebnisB2.ergebnis,
            numIndex: null,
          },
        ])
        .build();
      mockDefinitions.getErgebnisse.mockResolvedValueOnce(
        mockedErgebnisseStaplB
      );

      // stapel e
      const mockedBedenklicheStimmzettel = [
        prepareBedenklicherStimmzettel().validity(ValidityEnum.INVALID).build(),
        prepareBedenklicherStimmzettel().validity(ValidityEnum.INVALID).build(),
        prepareBedenklicherStimmzettel().validity(ValidityEnum.VALID).build(),
        prepareBedenklicherStimmzettel().validity(ValidityEnum.VALID).build(),
        prepareBedenklicherStimmzettel()
          .validity(ValidityEnum.PARTIAL_VALID)
          .build(),
        prepareBedenklicherStimmzettel().validity(ValidityEnum.INVALID).build(),
      ];
      mockDefinitions.getBedenklicheStimmzettel.mockReturnValue(
        mockedBedenklicheStimmzettel
      );

      // stapel d
      const ergebnisD1 = createErgebnis();
      const mockedErgebnisseStaplD = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: StapelArtEnum.MbwDUngueltig,
        })
        .ergebnisse([
          {
            wahlvorschlagID: wahlvorschlag1.identifikator,
            kandidatID: null,
            wahlvorschlagsOrdnungszahl: wahlvorschlag1.ordnungszahl,
            ergebnis: ergebnisD1.ergebnis,
            numIndex: null,
          },
        ])
        .build();
      mockDefinitions.getErgebnisse.mockResolvedValueOnce(
        mockedErgebnisseStaplD
      );
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const expectedUngueltigeStimen = ergebnisD1.ergebnis! + 3; //3 von stapel e ungültig

      // combined gueltige ergebnisse
      const ergebnisseAndWahlvorschlaege: MbwErgebnisseAndWahlvorschlag[] = [
        prepareMbwErgebnisseAndWahlvorschlag()
          .wahlvorschlag(wahlvorschlag1)
          .ergebnisStapelA(ergebnisA1)
          .ergebnisStapelB(ergebnisB1)
          .build(),
        prepareMbwErgebnisseAndWahlvorschlag()
          .wahlvorschlag(wahlvorschlag2)
          .ergebnisStapelA(ergebnisA2)
          .ergebnisStapelB(ergebnisB2)
          .build(),
      ];
      const expectedGueltigeStimmen =
        /* eslint-disable @typescript-eslint/no-non-null-assertion */
        // ergebnisse are explicitly set in test data factory, so they can not be null
        ergebnisA1.ergebnis! +
        ergebnisA2.ergebnis! +
        ergebnisB1.ergebnis! +
        ergebnisB2.ergebnis!;
      /* eslint-enable @typescript-eslint/no-non-null-assertion */

      // combined alle ergebnisse
      const expectedAlleStimmen =
        expectedGueltigeStimmen + expectedUngueltigeStimen;

      // barcode
      const dummyBarcodeUrl = generateRandomString(100);
      mockDefinitions.createBarcode.mockReturnValue(dummyBarcodeUrl);

      // footer
      const dummyUUID = "uuidv4";
      mockDefinitions.generateUuidv4.mockReturnValue(dummyUUID);
      const expectedFooter = generateRandomString(100);
      mockDefinitions.createFooter.mockReturnValue(expectedFooter);

      const result = await unitUnderTest.prepareDataForSchnellmeldungDruck(
        wahl,
        status,
        meldungsArt
      );

      const expectedResult = prepareSchnellmeldungDruckInput()
        .meldungsArt(meldungsArt)
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .aktuelleWahl(wahl)
        .footer(expectedFooter)
        .alleStimmen(convertToSixDigitArray(expectedAlleStimmen))
        .gueltigeStimmenListe(ergebnisseAndWahlvorschlaege)
        .gueltigeStimmenGesamt(convertToSixDigitArray(expectedGueltigeStimmen))
        .ungueltigeStimmen(convertToSixDigitArray(expectedUngueltigeStimen))
        .aWerte(aWerte)
        .bWerte(expectedBWerte)
        .wahlbezirkNummer("")
        .barcode(dummyBarcodeUrl)
        .sendOk(false)
        .build();

      expect(result).toStrictEqual(expectedResult);
    });
  });
});
