import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
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
  "@/composables/ergebnisermittlung/mbwErgebnisAndWahlvorschlagMapper.ts",
  () => ({
    useMbwErgebnisAndWahlvorschlagMapper: () => ({
      mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse:
        mockDefinitions.mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse,
    }),
  })
);

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnis, prepareErgebnisse } = useErgebnisseTestDataFactory();
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

  describe("createEmptyErgebnisForWahlvorschlag", () => {
    it("should_returnErgebnisWithEmptyErgebnisse_when_givenWahlvorschlag", () => {
      const wahlvorschlag = createWahlvorschlag();

      const expectedErgebnis: Ergebnis = {
        wahlvorschlagID: wahlvorschlag.identifikator,
        kandidatID: null,
        wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
        ergebnis: null,
        numIndex: null,
      };

      expect(
        unitUnderTest.createEmptyErgebnisForWahlvorschlag(wahlvorschlag)
      ).toStrictEqual(expectedErgebnis);
    });
  });

  describe("sortWahlvorschlaegeByOrdnungszahl", () => {
    it("should_returnSetOfSortedWahlvorschlaege_when_givenWahlvorschlaege", () => {
      const wahlvorschlag1 = prepareWahlvorschlag().ordnungszahl(1).build();
      const wahlvorschlag2 = prepareWahlvorschlag().ordnungszahl(2).build();
      const wahlvorschlag3 = prepareWahlvorschlag().ordnungszahl(3).build();
      const wahlvorschlag4 = prepareWahlvorschlag().ordnungszahl(4).build();

      const unsortedWahlvorschlaege = prepareWahlvorschlaege()
        .wahlvorschlaege(
          new Set([
            wahlvorschlag4,
            wahlvorschlag2,
            wahlvorschlag1,
            wahlvorschlag3,
          ])
        )
        .build();

      const sortedWahlvorschlaege =
        unitUnderTest.sortWahlvorschlaegeByOrdnungszahl(
          unsortedWahlvorschlaege
        );

      let expectedOrdnungszahl = 1;
      sortedWahlvorschlaege.forEach((wahlvorschlag) => {
        expect(wahlvorschlag.ordnungszahl).toBe(expectedOrdnungszahl);
        expectedOrdnungszahl++;
      });
      expect(Array.from(sortedWahlvorschlaege)).toEqual([
        wahlvorschlag1,
        wahlvorschlag2,
        wahlvorschlag3,
        wahlvorschlag4,
      ]);
    });
  });
});
