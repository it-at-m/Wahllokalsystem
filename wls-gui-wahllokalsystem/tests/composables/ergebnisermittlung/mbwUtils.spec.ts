import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useMbwUtils } from "@/composables/ergebnisermittlung/mbwUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { createErgebnis } = useErgebnisseTestDataFactory();
const { createWahlvorschlag } = useWahlvorschlaegeTestDataFactory();

describe("mbwUtils", () => {
  const wahlID = generateRandomString(10);
  const wahlbezirkID = generateRandomString(10);

  let unitUnderTest: ReturnType<typeof useMbwUtils>;

  beforeEach(() => {
    unitUnderTest = useMbwUtils(wahlID, wahlbezirkID);
  });

  describe("getErgebnisseStapelAFromErgebnisseAndWahlvorschlagList", () => {
    it("should_returnErgebnisse_when_givenErgebnisseWithWahlvorschlagList", () => {
      const ergebnisA1 = createErgebnis();
      const ergebnisA2 = createErgebnis();
      const ergebnisB1 = createErgebnis();
      const ergebnisB2 = createErgebnis();
      const wahlvorschlag1 = createWahlvorschlag();
      const wahlvorschlag2 = createWahlvorschlag();

      const mockedErgebnisseWithWahlvorschlag: MbwErgebnisseAndWahlvorschlag[] =
        [
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

      const expectedErgebnisseStapelA: Ergebnisse = {
        bezirkUndWahlIDStapelart: {
          stapelArt: StapelArtEnum.MbwA,
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
        },
        ergebnisse: [
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
        ],
      };

      expect(
        unitUnderTest.getErgebnisseStapelAFromErgebnisseAndWahlvorschlagList(
          mockedErgebnisseWithWahlvorschlag
        )
      ).toStrictEqual(expectedErgebnisseStapelA);
    });

    it("should_retunrErgebnisseWithEmptyErgebnisse_when_givenEmptyErgebnisseWithWahlvorschlagList", () => {
      const mockedErgebnisseWithWahlvorschlag: MbwErgebnisseAndWahlvorschlag[] =
        [];

      const expectedErgebnisseStapelA: Ergebnisse = {
        bezirkUndWahlIDStapelart: {
          stapelArt: StapelArtEnum.MbwA,
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
        },
        ergebnisse: [],
      };

      expect(
        unitUnderTest.getErgebnisseStapelAFromErgebnisseAndWahlvorschlagList(
          mockedErgebnisseWithWahlvorschlag
        )
      ).toStrictEqual(expectedErgebnisseStapelA);
    });
  });

  describe("getErgebnisseStapelBFromErgebnisseAndWahlvorschlagList", () => {
    it("should_returnErgebnisse_when_givenErgebnisseWithWahlvorschlagList", () => {
      const ergebnisA1 = createErgebnis();
      const ergebnisA2 = createErgebnis();
      const ergebnisB1 = createErgebnis();
      const ergebnisB2 = createErgebnis();
      const wahlvorschlag1 = createWahlvorschlag();
      const wahlvorschlag2 = createWahlvorschlag();

      const mockedErgebnisseWithWahlvorschlag: MbwErgebnisseAndWahlvorschlag[] =
        [
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

      const expectedErgebnisseStapelB: Ergebnisse = {
        bezirkUndWahlIDStapelart: {
          stapelArt: StapelArtEnum.MbwB,
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
        },
        ergebnisse: [
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
        ],
      };

      expect(
        unitUnderTest.getErgebnisseStapelBFromErgebnisseAndWahlvorschlagList(
          mockedErgebnisseWithWahlvorschlag
        )
      ).toStrictEqual(expectedErgebnisseStapelB);
    });

    it("should_retunrErgebnisseWithEmptyErgebnisse_when_givenEmptyErgebnisseWithWahlvorschlagList", () => {
      const mockedErgebnisseWithWahlvorschlag: MbwErgebnisseAndWahlvorschlag[] =
        [];

      const expectedErgebnisseStapelA: Ergebnisse = {
        bezirkUndWahlIDStapelart: {
          stapelArt: StapelArtEnum.MbwB,
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
        },
        ergebnisse: [],
      };

      expect(
        unitUnderTest.getErgebnisseStapelBFromErgebnisseAndWahlvorschlagList(
          mockedErgebnisseWithWahlvorschlag
        )
      ).toStrictEqual(expectedErgebnisseStapelA);
    });
  });
});
