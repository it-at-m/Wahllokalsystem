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
  mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse: vi.fn(),
}));

vi.mock("@/composables/ergebnismeldung/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    postErgebnisse: mockDefinitions.postErgebnisse,
  }),
}));
vi.mock(
  "@/composables/ergebnisermittlung/mbwErgebnisAndWahlvorschalgMapper.ts",
  () => ({
    useMbwErgebnisAndWahlvorschlagMapper: () => ({
      mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse:
        mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse,
    }),
  })
);

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();
const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();

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
      const mockedErgebnisse: Ergebnisse[] = [
        ergebnisseStaplA,
        ergebnisseStaplB,
      ];

      mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mockReturnValue(
        mockedErgebnisse
      );

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
      const saveErgebnissePromise = unitUnderTest.saveGueltigeErgebnisse(
        mockedErgebnisseWithWahlvorschlag
      );
      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(true);

      await saveErgebnissePromise;

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
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

      const mockedErgebnisse: Ergebnisse[] = [
        ergebnisseStaplA,
        ergebnisseStaplB,
      ];

      mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse.mockReturnValue(
        mockedErgebnisse
      );

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
      const saveErgebnissePromise = unitUnderTest.saveGueltigeErgebnisse(
        ergebnisseAndWahlvorschlag
      );
      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(true);

      await saveErgebnissePromise;

      expect(unitUnderTest.isErgebnisseSaving.value).toStrictEqual(false);
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
});
