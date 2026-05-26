import { describe, expect, it } from "vitest";

import {
  getStapelForWahlart,
  StapelArtEnum,
} from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

describe("StapelArtEnum.ts", () => {
  describe("getStapelForWahlart", () => {
    const testCaseArguments = [
      {
        wahlart: WahlWahlartEnum.Baw,
        expectedResult: [
          StapelArtEnum.SrwBawA,
          StapelArtEnum.SrwBawB,
          StapelArtEnum.SrwBawAB,
          StapelArtEnum.SrwBawBC,
          StapelArtEnum.SrwBawDUngueltig,
        ],
      },
      { wahlart: WahlWahlartEnum.Beb, expectedResult: [] },
      {
        wahlart: WahlWahlartEnum.Btw,
        expectedResult: [],
      },
      {
        wahlart: WahlWahlartEnum.Bzw,
        expectedResult: [],
      },
      {
        wahlart: WahlWahlartEnum.Euw,
        expectedResult: [],
      },
      {
        wahlart: WahlWahlartEnum.Ltw,
        expectedResult: [],
      },
      {
        wahlart: WahlWahlartEnum.Mbw,
        expectedResult: [
          StapelArtEnum.MbwA,
          StapelArtEnum.MbwB,
          StapelArtEnum.MbwAB,
          StapelArtEnum.MbwBC,
          StapelArtEnum.MbwDUngueltig,
        ],
      },
      {
        wahlart: WahlWahlartEnum.Obw,
        expectedResult: [
          StapelArtEnum.ObwA,
          StapelArtEnum.ObwCUngueltig,
          StapelArtEnum.ObwCGueltig,
          StapelArtEnum.ObwBLeer,
          StapelArtEnum.ObwBUngekennzeichnet,
        ],
      },
      {
        wahlart: WahlWahlartEnum.Srw,
        expectedResult: [
          StapelArtEnum.SrwBawA,
          StapelArtEnum.SrwBawB,
          StapelArtEnum.SrwBawAB,
          StapelArtEnum.SrwBawBC,
          StapelArtEnum.SrwBawDUngueltig,
        ],
      },
      {
        wahlart: WahlWahlartEnum.Svw,
        expectedResult: [],
      },
      {
        wahlart: WahlWahlartEnum.Ve,
        expectedResult: [],
      },
    ];

    it.each(testCaseArguments)(
      "should_returnCorrectListOfStapel_when'$wahlart'IsGiven",
      (testCaseArgument) => {
        const result = getStapelForWahlart(testCaseArgument.wahlart);

        testCaseArgument.expectedResult.forEach((expectedResultItem) => {
          expect(result).toContain(expectedResultItem);
        });
        expect(result.length).toStrictEqual(
          testCaseArgument.expectedResult.length
        );
      }
    );

    it("should_verifyThatAllWahlartenAreCheckable_whenGivenTestCaseArguments", () => {
      const wahlarten = Object.values(WahlWahlartEnum);
      wahlarten.forEach((wahlart) =>
        expect(
          testCaseArguments.some(
            (testCaseArgument) => testCaseArgument.wahlart === wahlart
          ),
          `${wahlart} is missing`
        ).toStrictEqual(true)
      );
    });
  });
});
