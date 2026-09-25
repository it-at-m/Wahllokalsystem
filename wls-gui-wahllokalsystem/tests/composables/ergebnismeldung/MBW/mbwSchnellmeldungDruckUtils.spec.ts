import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { useAWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/aWerteTestDataFactory.ts";
import { useBWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/bWerteTestDataFactory.ts";
import { useErgebnismeldungDruckInputTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnismeldungDruckInputTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/common/statusTestDataFactory.ts";
import { useBedenklicherStimmzettelTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/bedenklicherStimmzettelTestDataFactory.ts";
import { useMbwErgebnisseAndWahlvorschlagTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { computed, ref } from "vue";

import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { useStringNumberMapTools } from "@/composables/common/stringNumberMapTools.ts";
import { useMbwSchnellmeldungDruckUtils } from "@/composables/ergebnismeldung/MBW/mbwSchnellmeldungDruckUtils.ts";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";
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
  getBWerteForWahlbezirkAndWahl: vi.fn(),
  getBedenklicheStimmzettel: vi.fn(),
  loadStimmzettelOfWahlbezirk: vi.fn(),
}));

vi.mock(import("@/composables/drucken/commonPrintService.ts"), () => ({
  useCommonPrintService: () => ({
    createBarcode: mockDefinitions.createBarcode,
    createFooter: mockDefinitions.createFooter,
  }),
}));

const mockedStimmzettelOfWahlbezirk = ref<PersistedStimmzettel[]>([]);
vi.mock(import("@/composables/dse/allStimmzettelOfWahlbezirkState.ts"), () => ({
  useAllStimmzettelOfWahlbezirkState: () => ({
    isLoading: ref(false),
    stimmzettelOfWahlbezirk: mockedStimmzettelOfWahlbezirk,
    loadStimmzettelOfWahlbezirk: mockDefinitions.loadStimmzettelOfWahlbezirk,
  }),
}));

const mockedStapelASumGroupedByWahlvorschlag = ref(
  useStringNumberMapTools(new Map())
);
const mockedStapelBSumGroupedByWahlvorschlag = ref(
  useStringNumberMapTools(new Map())
);
const mockedStapelDUngueltig = ref<PersistedStimmzettel[]>([]);
const mockedStapelEUngueltig = ref<PersistedStimmzettel[]>([]);
vi.mock(import("@/composables/dse/mbwStimmzettelFilterService.ts"), () => ({
  useMbwStimmzettelFilterService: () => ({
    stapelA: computed<PersistedStimmzettel[]>(() => []),
    stapelB: computed<PersistedStimmzettel[]>(() => []),
    stapelBC: computed<PersistedStimmzettel[]>(() => []),
    stapelASumGroupedByWahlvorschlag: computed(
      () => mockedStapelASumGroupedByWahlvorschlag.value
    ),
    stapelBSumGroupedByWahlvorschlag: computed(
      () => mockedStapelBSumGroupedByWahlvorschlag.value
    ),
    stapelDUngueltig: computed(() => mockedStapelDUngueltig.value),
    stapelEUngueltig: computed(() => mockedStapelEUngueltig.value),
  }),
}));

const mockedWahlvorschlaegeErgebnisseStapelAAndB = ref<
  MbwErgebnisseAndWahlvorschlag[]
>([]);
vi.mock(
  import("@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts"),
  () => ({
    useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper: () => ({
      wahlvorschlaegeErgebnisseStapelAAndB: computed(
        () => mockedWahlvorschlaegeErgebnisseStapelAAndB.value
      ),
    }),
  })
);

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
vi.mock(
  import("@/composables/ergebnismeldung/common/bWerteService.ts"),
  () => ({
    useBWerteService: () => ({
      getBWerteForWahlbezirkAndWahl:
        mockDefinitions.getBWerteForWahlbezirkAndWahl,
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
const { prepareMbwErgebnisseAndWahlvorschlag } =
  useMbwErgebnisseAndWahlvorschlagTestDataFactory();
const { prepareBedenklicherStimmzettel } =
  useBedenklicherStimmzettelTestDataFactory();
const { createPersistedStimmzettel } = usePersistedStimmzettelTestDataFactory();

const { prepareSchnellmeldungDruckInput } =
  useErgebnismeldungDruckInputTestDataFactory();

const { convertToSixDigitArray } = useNumberFormatter();

const mockedNow = new Date();

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
    mockedStimmzettelOfWahlbezirk.value = [];
    mockedStapelASumGroupedByWahlvorschlag.value = useStringNumberMapTools(
      new Map()
    );
    mockedStapelBSumGroupedByWahlvorschlag.value = useStringNumberMapTools(
      new Map()
    );
    mockedStapelDUngueltig.value = [];
    mockedStapelEUngueltig.value = [];
    mockedWahlvorschlaegeErgebnisseStapelAAndB.value = [];
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("prepareDataForSchnellmeldungDruck", () => {
    it("should_returnErgebnismeldungDruckInput_when_givenWahlStatusAndMeldungsartForStapel", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      // --- prepare mock Values ---
      const status = createStatus();
      status.schnellmeldung.validierungsstatus =
        MeldungValidierungsstatusEnum.Valide;
      const meldungsArt = MeldungsArtEnum.Schnellmeldung;

      const mockedValues = initDseIndependentMocks();
      const expectedErgebnisse = createdExpectedStapelErgebnisse(mockedValues);

      const result = await unitUnderTest.prepareDataForSchnellmeldungDruck(
        mockedValues.mockedWahl,
        status,
        meldungsArt
      );

      const expectedResult = prepareSchnellmeldungDruckInput()
        .meldungsArt(meldungsArt)
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .aktuelleWahl(mockedValues.mockedWahl)
        .footer(mockedValues.mockedFooter)
        .alleStimmen(convertToSixDigitArray(expectedErgebnisse.alleStimmen))
        .gueltigeStimmenListe(expectedErgebnisse.ergebnisseAndWahlvorschlaege)
        .gueltigeStimmenGesamt(
          convertToSixDigitArray(expectedErgebnisse.gueltigeStimmen)
        )
        .ungueltigeStimmen(
          convertToSixDigitArray(expectedErgebnisse.ungueltigeStimen)
        )
        .aWerte(mockedValues.mockedAWerte)
        .bWerte(mockedValues.mockedBWerte)
        .wahlbezirkNummer("")
        .barcode(mockedValues.mockedBarcodeUrl)
        .sendOk(false)
        .build();

      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnErgebnismeldungDruckInput_when_givenWahlStatusAndMeldungsartForDSE", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
      mockIsDseAktiv.value = true;

      const mockedValues = initDseIndependentMocks();

      // --- prepare mock Values ---
      const status = createStatus();
      status.schnellmeldung.validierungsstatus =
        MeldungValidierungsstatusEnum.Valide;
      const meldungsArt = MeldungsArtEnum.Schnellmeldung;

      const expectedErgebnisse =
        createExpectedStimmzettelErgebnisse(mockedValues);

      const result = await unitUnderTest.prepareDataForSchnellmeldungDruck(
        mockedValues.mockedWahl,
        status,
        meldungsArt
      );

      const expectedResult = prepareSchnellmeldungDruckInput()
        .meldungsArt(meldungsArt)
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .aktuelleWahl(mockedValues.mockedWahl)
        .footer(mockedValues.mockedFooter)
        .alleStimmen(
          convertToSixDigitArray(expectedErgebnisse.mockedAlleStimmen)
        )
        .gueltigeStimmenListe(expectedErgebnisse.mockedGueltigeStimmen)
        .gueltigeStimmenGesamt(
          convertToSixDigitArray(expectedErgebnisse.mockedGUeltigeStimmenGesamt)
        )
        .ungueltigeStimmen(
          convertToSixDigitArray(expectedErgebnisse.mockedUngueltigeStimmzettel)
        )
        .aWerte(mockedValues.mockedAWerte)
        .bWerte(mockedValues.mockedBWerte)
        .wahlbezirkNummer("")
        .barcode(mockedValues.mockedBarcodeUrl)
        .sendOk(false)
        .build();

      expect(result).toStrictEqual(expectedResult);
    });
  });

  function createdExpectedStapelErgebnisse(
    mockedValues: ReturnType<typeof initDseIndependentMocks>
  ) {
    // stapel A
    const ergebnisA1 = prepareErgebnis()
      .wahlvorschlagID(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag1.identifikator
      )
      .wahlvorschlagsOrdnungszahl(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag1.ordnungszahl
      )
      .build();
    const ergebnisA2 = prepareErgebnis()
      .wahlvorschlagID(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag2.identifikator
      )
      .wahlvorschlagsOrdnungszahl(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag2.ordnungszahl
      )
      .build();
    const mockedErgebnisseStapelA: Ergebnisse = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
        stapelArt: StapelArtEnum.MbwA,
      })
      .ergebnisse([
        {
          wahlvorschlagID:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag1.ordnungszahl,
          ergebnis: ergebnisA1.ergebnis,
          numIndex: null,
        },
        {
          wahlvorschlagID:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag2.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag2.ordnungszahl,
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
      .wahlvorschlagID(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag1.identifikator
      )
      .wahlvorschlagsOrdnungszahl(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag1.ordnungszahl
      )
      .build();
    const ergebnisB2 = prepareErgebnis()
      .wahlvorschlagID(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag2.identifikator
      )
      .wahlvorschlagsOrdnungszahl(
        mockedValues.mockedWahlvorschlaege.wahlvorschlag2.ordnungszahl
      )
      .build();
    const mockedErgebnisseStaplB = prepareErgebnisse()
      .bezirkUndWahlIDStapelart({
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
        stapelArt: StapelArtEnum.MbwB,
      })
      .ergebnisse([
        {
          wahlvorschlagID:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag1.ordnungszahl,
          ergebnis: ergebnisB1.ergebnis,
          numIndex: null,
        },
        {
          wahlvorschlagID:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag2.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag2.ordnungszahl,
          ergebnis: ergebnisB2.ergebnis,
          numIndex: null,
        },
      ])
      .build();
    mockDefinitions.getErgebnisse.mockResolvedValueOnce(mockedErgebnisseStaplB);

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
          wahlvorschlagID:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag1.identifikator,
          kandidatID: null,
          wahlvorschlagsOrdnungszahl:
            mockedValues.mockedWahlvorschlaege.wahlvorschlag1.ordnungszahl,
          ergebnis: ergebnisD1.ergebnis,
          numIndex: null,
        },
      ])
      .build();
    mockDefinitions.getErgebnisse.mockResolvedValueOnce(mockedErgebnisseStaplD);

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const ungueltigeStimen = ergebnisD1.ergebnis! + 3; //3 von stapel e ungültig

    // combined gueltige ergebnisse
    const ergebnisseAndWahlvorschlaege: MbwErgebnisseAndWahlvorschlag[] = [
      prepareMbwErgebnisseAndWahlvorschlag()
        .wahlvorschlag(mockedValues.mockedWahlvorschlaege.wahlvorschlag1)
        .ergebnisStapelA(ergebnisA1)
        .ergebnisStapelB(ergebnisB1)
        .build(),
      prepareMbwErgebnisseAndWahlvorschlag()
        .wahlvorschlag(mockedValues.mockedWahlvorschlaege.wahlvorschlag2)
        .ergebnisStapelA(ergebnisA2)
        .ergebnisStapelB(ergebnisB2)
        .build(),
    ];

    const gueltigeStimmen =
      /* eslint-disable @typescript-eslint/no-non-null-assertion */
      // ergebnisse are explicitly set in test data factory, so they can not be null
      ergebnisA1.ergebnis! +
      ergebnisA2.ergebnis! +
      ergebnisB1.ergebnis! +
      ergebnisB2.ergebnis!;
    /* eslint-enable @typescript-eslint/no-non-null-assertion */

    const alleStimmen = gueltigeStimmen + ungueltigeStimen;

    return {
      alleStimmen,
      ergebnisseAndWahlvorschlaege,
      gueltigeStimmen,
      ungueltigeStimen,
    };
  }

  function createExpectedStimmzettelErgebnisse(
    mockedValues: ReturnType<typeof initDseIndependentMocks>
  ) {
    mockedStapelDUngueltig.value = [
      createPersistedStimmzettel(),
      createPersistedStimmzettel(),
    ];
    mockedStapelEUngueltig.value = [
      createPersistedStimmzettel(),
      createPersistedStimmzettel(),
      createPersistedStimmzettel(),
    ];

    mockedWahlvorschlaegeErgebnisseStapelAAndB.value = [
      prepareMbwErgebnisseAndWahlvorschlag()
        .wahlvorschlag(prepareWahlvorschlag().identifikator("wv1").build())
        .ergebnisStapelA(prepareErgebnis().ergebnis(10).build())
        .ergebnisStapelB(prepareErgebnis().ergebnis(21).build())
        .build(),
      prepareMbwErgebnisseAndWahlvorschlag()
        .wahlvorschlag(prepareWahlvorschlag().identifikator("wv2").build())
        .ergebnisStapelA(prepareErgebnis().ergebnis(11).build())
        .ergebnisStapelB(prepareErgebnis().ergebnis(22).build())
        .build(),
    ];

    const countUngueltigeStimmzettel = [
      ...mockedStapelDUngueltig.value,
      ...mockedStapelEUngueltig.value,
    ].length;
    const countGueltigeStimmen = 10 + 21 + 11 + 22;

    return {
      mockedUngueltigeStimmzettel: countUngueltigeStimmzettel,
      mockedGueltigeStimmen: mockedWahlvorschlaegeErgebnisseStapelAAndB.value,
      mockedAlleStimmen: countUngueltigeStimmzettel + countGueltigeStimmen,
      mockedGUeltigeStimmenGesamt: countGueltigeStimmen,
    };
  }

  function initDseIndependentMocks() {
    const mockedWahl = prepareWahl().wahlID(wahlID).build();

    // mock aWerte
    const mockedAWerte = prepareAWerte()
      .bezirkUndWahlID({
        wahlbezirkID: wahlbezirkID,
        wahlID: mockedWahl.wahlID,
      })
      .build();
    mockDefinitions.getAWerteForWahlbezirkAndWahl.mockResolvedValue(
      mockedAWerte
    );

    //bWerte
    const mockedBWerte = prepareBWerte()
      .bezirkUndWahlID({
        wahlbezirkID: wahlbezirkID,
        wahlID: mockedWahl.wahlID,
      })
      .b(5)
      .b1(3)
      .b2(2)
      .build();
    mockDefinitions.getBWerteForWahlbezirkAndWahl.mockResolvedValue(
      mockedBWerte
    );

    // wahlvorschlaege
    const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
    const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();
    const sortedWahlvorschlaege = prepareWahlvorschlaege()
      .wahlID(wahlID)
      .wahlbezirkID(wahlbezirkID)
      .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
      .build();
    mockDefinitions.getWahlvorschlaege.mockResolvedValue(sortedWahlvorschlaege);

    const mockedBarcodeUrl = generateRandomString(100);
    mockDefinitions.createBarcode.mockReturnValue(mockedBarcodeUrl);

    const mockedFooter = generateRandomString(100);
    mockDefinitions.createFooter.mockReturnValue(mockedFooter);

    return {
      mockedBarcodeUrl,
      mockedAWerte,
      mockedBWerte,
      mockedFooter,
      mockedWahl,
      mockedWahlvorschlaege: {
        wahlvorschlag1,
        wahlvorschlag2,
      },
    };
  }
});
