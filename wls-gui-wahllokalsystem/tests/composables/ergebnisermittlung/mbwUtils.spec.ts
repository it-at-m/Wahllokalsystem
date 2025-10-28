import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useMbwUtils } from "@/composables/ergebnisermittlung/mbwUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  postErgebnisse: vi.fn(),
  getErgebnisse: vi.fn(),
  getWahlvorschlaege: vi.fn(),
  mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    postErgebnisse: mockDefinitions.postErgebnisse,
    getErgebnisse: mockDefinitions.getErgebnisse,
  }),
}));
vi.mock("@/composables/wahlvorschlaege/wahlvorschlaegeService.ts", () => ({
  useWahlvorschlaegeService: () => ({
    getWahlvorschlaege: mockDefinitions.getWahlvorschlaege,
  }),
}));
vi.mock(
  "@/composables/ergebnisermittlung/mbwErgebnisAndWahlvorschlagMapper.ts",
  () => ({
    useMbwErgebnisAndWahlvorschlagMapper: () => ({
      mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse:
        mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse,
    }),
  })
);

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnis, prepareErgebnisse, prepareErgebnis } =
  useErgebnisseTestDataFactory();
const { createWahlvorschlag, prepareWahlvorschlag, prepareWahlvorschlaege } =
  useWahlvorschlaegeTestDataFactory();

describe("mbwUtils", () => {
  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useMbwUtils>;

  beforeEach(() => {
    unitUnderTest = useMbwUtils(wahlID, wahlbezirkID);
  });

  afterEach(() => {
    vi.clearAllMocks();
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

      const mockedWahlvorschlaegeModel = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .wahlvorschlaege(new Set([wahlvorschlag2, wahlvorschlag1]))
        .build();

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        mockedWahlvorschlaegeModel
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
        async () =>
          await unitUnderTest.loadAndCombineErgebnisseAndWahlvorschlaege()
      ).rejects.toThrow();
    });

    it("should_returnErgebnisseWithEmptyErgebnisse_when_noDataFromApiCallGiven", async () => {
      const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
      const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();

      const mockedWahlvorschlaegeModel = prepareWahlvorschlaege()
        .wahlID(wahlID)
        .wahlbezirkID(wahlbezirkID)
        .wahlvorschlaege(new Set([wahlvorschlag2, wahlvorschlag1]))
        .build();

      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        mockedWahlvorschlaegeModel
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
});
