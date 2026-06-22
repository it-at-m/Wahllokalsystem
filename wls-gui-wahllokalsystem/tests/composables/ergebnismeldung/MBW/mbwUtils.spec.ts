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
import { spyOn } from "storybook/test";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import pinia from "@/plugins/pinia.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  postErgebnisse: vi.fn(),
  getErgebnisse: vi.fn(),
  postSchnellmeldung: vi.fn(),
  postNiederschrift: vi.fn(),
  getWahlOrUndefinedById: vi.fn(),
  getWahlvorschlaege: vi.fn(),
  mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse: vi.fn(),
  sortWahlvorschlaegeByOrdnungszahl: vi.fn(),
  getWaehlerverzeichnisNummerOrUndefinedById: vi.fn(),
  getStimmabgabevermerke: vi.fn(),
  getStimmzettelumschlaege: vi.fn(),
  getAWerte: vi.fn(),
  generateUuidv4: vi.fn(),
  getStatus: vi.fn(),
  postStatus: vi.fn(),
  postAusdruck: vi.fn(),
  getBedenklicheStimmzettel: vi.fn(),
}));

vi.mock(
  import("@/composables/ergebnismeldung/common/ergebnisService.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useErgebnisService: () => ({
        ...mod.useErgebnisService(),
        postErgebnisse: mockDefinitions.postErgebnisse,
        getErgebnisse: mockDefinitions.getErgebnisse,
        postSchnellmeldung: mockDefinitions.postSchnellmeldung,
        postNiederschrift: mockDefinitions.postNiederschrift,
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
  import("@/composables/ergebnismeldung/common/statusService.ts"),
  () => ({
    useStatusService: () => ({
      getStatus: mockDefinitions.getStatus,
      postStatus: mockDefinitions.postStatus,
    }),
  })
);
vi.mock(
  import("@/composables/ergebnismeldung/common/ausdruckService.ts"),
  () => ({
    useAusdruckService: () => ({
      postAusdruck: mockDefinitions.postAusdruck,
    }),
  })
);
vi.mock(
  import("@/composables/wahlvorschlaege/wahlvorschlagUtils.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useWahlvorschlagUtils: () => ({
        ...mod.useWahlvorschlagUtils(),
        sortWahlvorschlaegeByOrdnungszahl:
          mockDefinitions.sortWahlvorschlaegeByOrdnungszahl,
      }),
    };
  }
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
      getAWerte: mockDefinitions.getAWerte,
    }),
  })
);
vi.mock(import("jsbarcode"));

crypto.randomUUID = mockDefinitions.generateUuidv4;

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnis, prepareErgebnisse, prepareErgebnis } =
  useErgebnisseTestDataFactory();
const {
  createWahlvorschlag,
  createWahlvorschlaege,
  prepareWahlvorschlag,
  prepareWahlvorschlaege,
} = useWahlvorschlaegeTestDataFactory();
const { createWahl, prepareWahl } = useWahlTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareStimmabgabevermerke, prepareVermerk, prepareStimmzettel } =
  useStimmabgabevermerkeTestDataFactory();
const { createStatus } = useStatusTestDataFactory();
const { prepareAWerte } = useAWerteTestDataFactory();
const { prepareBWerte } = useBWerteTestDataFactory();
const { prepareMbwErgebnisseAndWahlvorschlag } =
  useMbwErgebnisseAndWahlvorschlagTestDataFactory();
const { prepareSchnellmeldungDruckInput } =
  useErgebnismeldungDruckInputTestDataFactory();
const { convertToSixDigitArray } = useNumberFormatter();
const { toGermanDate, toHhMm, toYyyyMmDdWithTimeWithoutTimezoneOffset } =
  useDateTimeFormatter();
const { prepareBedenklicherStimmzettel } =
  useBedenklicherStimmzettelTestDataFactory();

const mockedNow = new Date();

describe("mbwUtils", () => {
  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useMbwUtils>;

  beforeEach(() => {
    createTestingPinia({ createSpy: vi.fn, stubActions: false });
    unitUnderTest = useMbwUtils(wahlID, wahlbezirkID);

    vi.useFakeTimers({
      now: mockedNow,
    });
    vi.setSystemTime(mockedNow);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("saveGueltigeErgebnisse", () => {
    const ergebnisA1 = createErgebnis();
    const ergebnisA2 = createErgebnis();
    const ergebnisB1 = createErgebnis();
    const ergebnisB2 = createErgebnis();
    const wahlvorschlag1 = createWahlvorschlag();
    const wahlvorschlag2 = createWahlvorschlag();

    const mockedErgebnisseWithWahlvorschlag: MbwErgebnisseAndWahlvorschlag[] = [
      {
        ergebnisStapelA: ergebnisA1,
        ergebnisStapelB: ergebnisB1,
        wahlvorschlag: wahlvorschlag1,
      },
      {
        ergebnisStapelA: ergebnisA2,
        ergebnisStapelB: ergebnisB2,
        wahlvorschlag: wahlvorschlag2,
      },
    ];

    const ergebnisseStaplA: Ergebnisse = prepareErgebnisse()
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

    const ergebnisseStaplB = prepareErgebnisse()
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

    it("should_saveErgebnisseForStapelA_when_givenValidErgebnisse", async () => {
      mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mockReturnValueOnce(
        ergebnisseStaplA
      );
      mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mockReturnValueOnce(
        ergebnisseStaplB
      );

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
      const saveErgebnissePromise = unitUnderTest.saveGueltigeErgebnisse(
        mockedErgebnisseWithWahlvorschlag
      );
      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(true);

      await saveErgebnissePromise;

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
      expect(
        mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse
      ).toHaveBeenCalledTimes(2);
      expect(
        mockDefinitions
          .mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mock
          .calls
      ).toStrictEqual([
        [StapelArtEnum.MbwA, mockedErgebnisseWithWahlvorschlag],
        [StapelArtEnum.MbwB, mockedErgebnisseWithWahlvorschlag],
      ]);
      expect(mockDefinitions.postErgebnisse).toHaveBeenCalledTimes(2);
      expect(mockDefinitions.postErgebnisse.mock.calls).toStrictEqual([
        [wahlbezirkID, wahlID, StapelArtEnum.MbwA, ergebnisseStaplA, true],
        [wahlbezirkID, wahlID, StapelArtEnum.MbwB, ergebnisseStaplB, true],
      ]);
    });

    it("should_sendEmptyErgebnisse_when_noErgebnisseAreGiven", async () => {
      const ergebnisseAndWahlvorschlag: MbwErgebnisseAndWahlvorschlag[] = [];

      ergebnisseStaplA.ergebnisse = [];
      ergebnisseStaplB.ergebnisse = [];

      mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mockReturnValueOnce(
        ergebnisseStaplA
      );
      mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mockReturnValueOnce(
        ergebnisseStaplB
      );

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
      const saveErgebnissePromise = unitUnderTest.saveGueltigeErgebnisse(
        ergebnisseAndWahlvorschlag
      );
      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(true);

      await saveErgebnissePromise;

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
      expect(
        mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse
      ).toHaveBeenCalledTimes(2);
      expect(
        mockDefinitions
          .mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mock
          .calls
      ).toStrictEqual([
        [StapelArtEnum.MbwA, ergebnisseAndWahlvorschlag],
        [StapelArtEnum.MbwB, ergebnisseAndWahlvorschlag],
      ]);
      expect(mockDefinitions.postErgebnisse).toHaveBeenCalledTimes(2);
      expect(mockDefinitions.postErgebnisse.mock.calls).toStrictEqual([
        [wahlbezirkID, wahlID, StapelArtEnum.MbwA, ergebnisseStaplA, true],
        [wahlbezirkID, wahlID, StapelArtEnum.MbwB, ergebnisseStaplB, true],
      ]);
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      mockDefinitions.postErgebnisse.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        unitUnderTest.saveGueltigeErgebnisse(mockedErgebnisseWithWahlvorschlag)
      ).rejects.toThrow();
    });
  });

  describe("loadAndCombineErgebnisseAndWahlvorschlaege", () => {
    it("should_loadErgebnisseAndWahlvorschlaegeAndSortAndReturnMbwErgebnisseAndWahlvorschlaegeList_when_called", async () => {
      const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
      const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();

      const ergebnisA1 = createErgebnis();
      const ergebnisA2 = createErgebnis();
      const ergebnisB1 = createErgebnis();
      const ergebnisB2 = createErgebnis();

      const mockedErgebnisseStaplA: Ergebnisse = prepareErgebnisse()
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

      const sortedWahlvorschlaege = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
        .build();

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        createWahlvorschlaege()
      );
      mockDefinitions.sortWahlvorschlaegeByOrdnungszahl.mockReturnValue(
        sortedWahlvorschlaege
      );
      mockDefinitions.getErgebnisse.mockResolvedValueOnce(
        mockedErgebnisseStaplA
      );
      mockDefinitions.getErgebnisse.mockResolvedValueOnce(
        mockedErgebnisseStaplB
      );

      const expectedResult: MbwErgebnisseAndWahlvorschlag[] = [
        {
          ergebnisStapelA: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag1.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag1.ordnungszahl)
            .ergebnis(ergebnisA1.ergebnis)
            .build(),
          ergebnisStapelB: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag1.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag1.ordnungszahl)
            .ergebnis(ergebnisB1.ergebnis)
            .build(),
          wahlvorschlag: wahlvorschlag1,
        },
        {
          ergebnisStapelA: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag2.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag2.ordnungszahl)
            .ergebnis(ergebnisA2.ergebnis)
            .build(),
          ergebnisStapelB: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag2.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag2.ordnungszahl)
            .ergebnis(ergebnisB2.ergebnis)
            .build(),
          wahlvorschlag: wahlvorschlag2,
        },
      ];

      const result =
        await unitUnderTest.loadAndCombineErgebnisseAndWahlvorschlaege();

      let expectedOrdnungszahl = 1;

      result.forEach((ergebnisseAndwWahlvorschlag) => {
        expect(ergebnisseAndwWahlvorschlag.wahlvorschlag.ordnungszahl).toBe(
          expectedOrdnungszahl
        );
        expectedOrdnungszahl++;
      });
      expect(mockDefinitions.getWahlvorschlaege.mock.calls).toStrictEqual([
        [wahlID, wahlbezirkID],
      ]);
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_throwError_when_calledServiceThrowsError", async () => {
      mockDefinitions.getWahlvorschlaege.mockRejectedValue(
        new Error("service call failed")
      );

      await expect(
        unitUnderTest.loadAndCombineErgebnisseAndWahlvorschlaege()
      ).rejects.toThrow();
    });

    it("should_returnErgebnisseWithEmptyErgebnisse_when_noDataFromApiCallGiven", async () => {
      const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
      const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();

      const sortedWahlvorschlaege = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .wahlvorschlaege([wahlvorschlag1, wahlvorschlag2])
        .build();

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        createWahlvorschlaege()
      );
      mockDefinitions.sortWahlvorschlaegeByOrdnungszahl.mockReturnValue(
        sortedWahlvorschlaege
      );
      mockDefinitions.getErgebnisse.mockResolvedValueOnce(null);
      mockDefinitions.getErgebnisse.mockResolvedValueOnce(null);

      const expectedResult: MbwErgebnisseAndWahlvorschlag[] = [
        {
          ergebnisStapelA: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag1.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag1.ordnungszahl)
            .ergebnis(null)
            .build(),
          ergebnisStapelB: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag1.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag1.ordnungszahl)
            .ergebnis(null)
            .build(),
          wahlvorschlag: wahlvorschlag1,
        },
        {
          ergebnisStapelA: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag2.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag2.ordnungszahl)
            .ergebnis(null)
            .build(),
          ergebnisStapelB: prepareErgebnis()
            .wahlvorschlagID(wahlvorschlag2.identifikator)
            .wahlvorschlagsOrdnungszahl(wahlvorschlag2.ordnungszahl)
            .ergebnis(null)
            .build(),
          wahlvorschlag: wahlvorschlag2,
        },
      ];

      const result =
        await unitUnderTest.loadAndCombineErgebnisseAndWahlvorschlaege();

      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("sendSchnellmeldung", () => {
    it("should_callPostSchnellmeldung_when_wahlForWahlIdIsGiven", async () => {
      mockDefinitions.postSchnellmeldung.mockResolvedValueOnce(null);

      const mockedWahl = createWahl();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);

      const userWahlbezirkID = generateRandomString(10);
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const spyOnValueSetterOfIsSendingSchnellmeldung = spyOn(
        unitUnderTest.isSendingSchnellmeldung,
        "value",
        "set"
      );

      expect(unitUnderTest.isSendingSchnellmeldung.value).toStrictEqual(false);

      await unitUnderTest.sendSchnellmeldung();

      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        {
          bezirkUndWahlID: {
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
          },
          niederschrift: {
            gedruckt: false,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
          schnellmeldung: {
            gedruckt: false,
            sendeuhrzeit: toYyyyMmDdWithTimeWithoutTimezoneOffset(mockedNow),
            uebermittelt: true,
            validierungsstatus: "VALIDE",
          },
        },
        false
      );

      expect(
        spyOnValueSetterOfIsSendingSchnellmeldung.mock.calls
      ).toStrictEqual([[true], [false]]);
      expect(mockDefinitions.postSchnellmeldung.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          userWahlbezirkID,
          mockedWahl.waehlerverzeichnisNummer,
        ],
      ]);

      spyOnValueSetterOfIsSendingSchnellmeldung.mockRestore();
    });

    it("should_notCallPostSchnellmeldung_when_wahlForWahlIdIsNotGiven", async () => {
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(undefined);

      const userWahlbezirkID = generateRandomString(10);
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const spyOnValueSetterOfIsSendingSchnellmeldung = spyOn(
        unitUnderTest.isSendingSchnellmeldung,
        "value",
        "set"
      );

      expect(unitUnderTest.isSendingSchnellmeldung.value).toStrictEqual(false);

      await unitUnderTest.sendSchnellmeldung();

      expect(
        spyOnValueSetterOfIsSendingSchnellmeldung.mock.calls
      ).toStrictEqual([[true], [false]]);
      expect(
        mockDefinitions.postSchnellmeldung.mock.calls.length
      ).toStrictEqual(0);

      spyOnValueSetterOfIsSendingSchnellmeldung.mockRestore();
    });

    it("should_updateIsSendingSchnellmeldung_when_apiCallFailed", async () => {
      const mockedServiceError = new Error("mocked service call failed");
      mockDefinitions.postSchnellmeldung.mockRejectedValue(mockedServiceError);

      const mockedWahl = createWahl();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);

      const userWahlbezirkID = generateRandomString(10);
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const spyOnValueSetterOfIsSendingSchnellmeldung = spyOn(
        unitUnderTest.isSendingSchnellmeldung,
        "value",
        "set"
      );

      await unitUnderTest.sendSchnellmeldung();

      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        {
          bezirkUndWahlID: {
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
          },
          niederschrift: {
            gedruckt: false,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
          schnellmeldung: {
            gedruckt: false,
            sendeuhrzeit: toYyyyMmDdWithTimeWithoutTimezoneOffset(mockedNow),
            uebermittelt: false,
            validierungsstatus: "VALIDE",
          },
        },
        false
      );

      expect(unitUnderTest.isSendingSchnellmeldung.value).toStrictEqual(false);

      expect(
        spyOnValueSetterOfIsSendingSchnellmeldung.mock.calls
      ).toStrictEqual([[true], [false]]);
      expect(mockDefinitions.postSchnellmeldung.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          userWahlbezirkID,
          mockedWahl.waehlerverzeichnisNummer,
        ],
      ]);

      spyOnValueSetterOfIsSendingSchnellmeldung.mockRestore();
    });
  });

  describe("sendNiederschrift", () => {
    it("should_callPostNiederschrift_when_wahlForWahlIdIsGiven", async () => {
      mockDefinitions.postNiederschrift.mockResolvedValueOnce(null);
      mockDefinitions.getStatus.mockResolvedValue(null);

      const mockedWahl = createWahl();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);

      const userWahlbezirkID = generateRandomString(10);
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const spyOnValueSetterOfIsSendingNiederschrift = spyOn(
        unitUnderTest.isSendingNiederschrift,
        "value",
        "set"
      );

      expect(unitUnderTest.isSendingNiederschrift.value).toStrictEqual(false);

      await unitUnderTest.sendNiederschrift();

      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        {
          bezirkUndWahlID: {
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
          },
          niederschrift: {
            gedruckt: false,
            sendeuhrzeit: toYyyyMmDdWithTimeWithoutTimezoneOffset(mockedNow),
            uebermittelt: true,
            validierungsstatus: "VALIDE",
          },
          schnellmeldung: {
            gedruckt: false,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
        },
        false
      );

      expect(spyOnValueSetterOfIsSendingNiederschrift.mock.calls).toStrictEqual(
        [[true], [false]]
      );
      expect(mockDefinitions.postNiederschrift.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          mockedWahl.waehlerverzeichnisNummer,
          userWahlbezirkID,
        ],
      ]);

      spyOnValueSetterOfIsSendingNiederschrift.mockRestore();
    });

    it("should_notCallPostNiederschrift_when_wahlForWahlIdIsNotGiven", async () => {
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(undefined);

      const userWahlbezirkID = generateRandomString(10);
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const spyOnValueSetterOfIsSendingNiederschrift = spyOn(
        unitUnderTest.isSendingNiederschrift,
        "value",
        "set"
      );

      expect(unitUnderTest.isSendingNiederschrift.value).toStrictEqual(false);

      await unitUnderTest.sendNiederschrift();

      expect(spyOnValueSetterOfIsSendingNiederschrift.mock.calls).toStrictEqual(
        [[true], [false]]
      );
      expect(mockDefinitions.postNiederschrift.mock.calls.length).toStrictEqual(
        0
      );

      spyOnValueSetterOfIsSendingNiederschrift.mockRestore();
    });

    it("should_updateIsSendingNiederschrift_when_apiCallFailed", async () => {
      const mockedServiceError = new Error("mocked service call failed");
      mockDefinitions.postNiederschrift.mockRejectedValue(mockedServiceError);

      const mockedWahl = createWahl();
      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(mockedWahl);

      const userWahlbezirkID = generateRandomString(10);
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const spyOnValueSetterOfIsSendingNiederschrift = spyOn(
        unitUnderTest.isSendingNiederschrift,
        "value",
        "set"
      );

      await unitUnderTest.sendNiederschrift();

      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        {
          bezirkUndWahlID: {
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
          },
          niederschrift: {
            gedruckt: false,
            sendeuhrzeit: toYyyyMmDdWithTimeWithoutTimezoneOffset(mockedNow),
            uebermittelt: false,
            validierungsstatus: "VALIDE",
          },
          schnellmeldung: {
            gedruckt: false,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
        },
        false
      );

      expect(unitUnderTest.isSendingNiederschrift.value).toStrictEqual(false);

      expect(spyOnValueSetterOfIsSendingNiederschrift.mock.calls).toStrictEqual(
        [[true], [false]]
      );
      expect(mockDefinitions.postNiederschrift.mock.calls).toStrictEqual([
        [
          wahlID,
          wahlbezirkID,
          mockedWahl.waehlerverzeichnisNummer,
          userWahlbezirkID,
        ],
      ]);

      spyOnValueSetterOfIsSendingNiederschrift.mockRestore();
    });
  });

  describe("updateStatusAfterSchnellmeldungDrucken", () => {
    it("should_updateStatusWithGedrucktTrue_when_schnellmeldungDruckenIsCalled", async () => {
      await unitUnderTest.updateStatusAfterSchnellmeldungDrucken();

      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        {
          bezirkUndWahlID: {
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
          },
          niederschrift: {
            gedruckt: false,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
          schnellmeldung: {
            gedruckt: true,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
        },
        false
      );
    });
  });

  describe("sendAusdruckNiederschrift", () => {
    it("should_updateStatusWithGedrucktTrue_when_postAusdruckSucceed", async () => {
      mockDefinitions.postAusdruck.mockResolvedValue(null);

      await unitUnderTest.sendAusdruckNiederschrift(
        MeldungsArtEnum.Niederschrift,
        "ausdruck"
      );

      expect(mockDefinitions.postStatus).toHaveBeenCalledWith(
        wahlID,
        wahlbezirkID,
        {
          bezirkUndWahlID: {
            wahlID: wahlID,
            wahlbezirkID: wahlbezirkID,
          },
          niederschrift: {
            gedruckt: true,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
          schnellmeldung: {
            gedruckt: false,
            sendeuhrzeit: undefined,
            uebermittelt: undefined,
            validierungsstatus: "NICHT_VALIDIERT",
          },
        },
        false
      );
    });

    it("should_notUpdateStatus_when_postAusdruckFailed", async () => {
      const mockedServiceError = new Error("mocked service call failed");
      mockDefinitions.postAusdruck.mockRejectedValue(mockedServiceError);

      await unitUnderTest.sendAusdruckNiederschrift(
        MeldungsArtEnum.Niederschrift,
        "ausdruck"
      );

      expect(mockDefinitions.postStatus).not.toHaveBeenCalled();
    });
  });

  describe("getBWerteForWahlbezirkAndWahl", () => {
    it("should_calculateBWerte_when_wahlbezirksartIsUWB", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        1
      );

      mockDefinitions.getStimmabgabevermerke.mockReturnValue(
        prepareStimmabgabevermerke()
          .eingenommeneWahlscheine(
            new Map([
              [StimmzettelStimmzettelartEnum.Klein, 1],
              [StimmzettelStimmzettelartEnum.Beide, 1],
            ])
          )
          .vermerke([
            prepareVermerk()
              .blattnummer(1)
              .stimmzettel([
                prepareStimmzettel()
                  .anzahl(1)
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                  .build(),
                prepareStimmzettel()
                  .anzahl(1)
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Beide)
                  .build(),
              ])
              .build(),
            prepareVermerk()
              .blattnummer(2)
              .stimmzettel([
                prepareStimmzettel()
                  .anzahl(1)
                  .stimmzettelart(StimmzettelStimmzettelartEnum.Klein)
                  .build(),
              ])
              .build(),
          ])
          .build()
      );

      const result = await unitUnderTest.getBWerteForWahlbezirkAndWahl();

      expect(mockDefinitions.getStimmabgabevermerke).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        1
      );
      expect(result.b1).toBe(3);
      expect(result.b2).toBe(2);
      expect(result.b).toBe(5);
    });

    it("should_calculateBWerteAs0_when_wahlbezirksartIsUWBAndStimmabgabevermerkeIsNull", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );

      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        1
      );

      mockDefinitions.getStimmabgabevermerke.mockReturnValue(null);

      const result = await unitUnderTest.getBWerteForWahlbezirkAndWahl();

      expect(mockDefinitions.getStimmabgabevermerke).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        1
      );
      expect(result.b1).toBe(0);
      expect(result.b2).toBe(0);
      expect(result.b).toBe(0);
    });

    it("should_calculateOnlyValueB_when_wahlbezirksartIsBWB", async () => {
      const userStore = useUserStore(pinia);
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build()
      );

      const wahl = prepareWahl().wahlID(wahlID).build();

      mockDefinitions.getWahlOrUndefinedById.mockReturnValue(wahl);

      mockDefinitions.getStimmzettelumschlaege.mockReturnValue({
        anzahlWaehler: 4,
      });

      const result = await unitUnderTest.getBWerteForWahlbezirkAndWahl();

      expect(mockDefinitions.getStimmzettelumschlaege).toHaveBeenCalledWith(
        wahl,
        wahlbezirkID,
        "",
        false
      );
      expect(result.b1).toBe(0);
      expect(result.b2).toBe(0);
      expect(result.b).toBe(4);
    });
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
      mockDefinitions.getAWerte.mockResolvedValue([aWerte]);

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
        createWahlvorschlaege()
      );
      mockDefinitions.sortWahlvorschlaegeByOrdnungszahl.mockReturnValue(
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
      const dummyBarcodeUrl = "dummyUrl";
      vi.spyOn(HTMLCanvasElement.prototype, "toDataURL").mockImplementationOnce(
        () => {
          return dummyBarcodeUrl;
        }
      );

      // footer
      const dummyUUID = "uuidv4";
      mockDefinitions.generateUuidv4.mockReturnValue(dummyUUID);
      const expectedFooter =
        dummyUUID +
        ", " +
        toGermanDate(mockedNow) +
        " " +
        toHhMm(mockedNow) +
        " O";

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
