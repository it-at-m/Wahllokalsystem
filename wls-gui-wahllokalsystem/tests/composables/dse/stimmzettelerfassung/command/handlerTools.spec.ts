import { describe, expect, it } from "vitest";

import { useHandlerTools } from "@/composables/dse/stimmzettelerfassung/command/handlerTools.ts";

describe("handlerTools.ts", () => {
  const {
    isValidKandidatOrdnungszahl,
    isValidWahlvorschlagOrdnungszahl,
    isValidCount,
    isValidRange,
    normalizeBounds,
    parseOptionalPlusCountToNumber,
  } = useHandlerTools();

  describe("isValidKandidatOrdnungszahl", () => {
    it.each([101, 202, 999, 1201])(
      "should_returnTrue_when_ordnungszahlIsSafeIntegerAndNotMultipleOf100: %s",
      (value) => {
        expect(isValidKandidatOrdnungszahl(value)).toBe(true);
      }
    );

    it.each([100, 200, 300, 1000, NaN, 1.5])(
      "should_returnFalse_when_ordnungszahlIsMultipleOf100OrInvalid: %s",
      (value: number) => {
        expect(isValidKandidatOrdnungszahl(value as number)).toBe(false);
      }
    );
  });

  describe("isValidCount", () => {
    it.each([1, 2, 10, 999])(
      "should_returnTrue_when_countIsPositiveSafeInteger: %s",
      (count) => {
        expect(isValidCount(count)).toBe(true);
      }
    );

    it.each([0, -1, -5, 1.1, NaN, Infinity, -Infinity])(
      "should_returnFalse_when_countIsNotPositiveSafeInteger: %s",
      (count: number) => {
        expect(isValidCount(count as number)).toBe(false);
      }
    );
  });

  describe("isValidWahlvorschlagOrdnungszahl", () => {
    it.each([1, 2, 9, 10, 11, 99, 100, 200, 1000])(
      "should_returnTrue_when_ordnungszahlIsSafeIntegerAndLessThan100OrMultipleOf100: %s",
      (value) => {
        expect(isValidWahlvorschlagOrdnungszahl(value)).toBe(true);
      }
    );

    it.each([101, 999, 1001, 9999, NaN, 1.5, Infinity, -Infinity])(
      "should_returnFalse_when_ordnungszahlIsGreaterOrEqual100AndNotMultipleOf100OrInvalid: %s",
      (value: number) => {
        expect(isValidWahlvorschlagOrdnungszahl(value as number)).toBe(false);
      }
    );
  });

  describe("normalizeBounds", () => {
    it.each([
      [101, 103, { lower: 101, upper: 103 }],
      [103, 101, { lower: 101, upper: 103 }],
      [201, 299, { lower: 201, upper: 299 }],
    ])(
      "should_returnLowerUpperPairSorted_when_called",
      (a: number, b: number, expected: { lower: number; upper: number }) => {
        expect(normalizeBounds(a, b)).toStrictEqual(expected);
      }
    );
  });

  describe("isValidRange", () => {
    it.each([
      [101, 199],
      [199, 101],
      [100, 199],
      [200, 299],
      [1000, 1099],
    ])(
      "should_returnTrue_when_boundsAreWithinSameHundreds: %s-%s",
      (lower: number, upper: number) => {
        expect(isValidRange(lower, upper)).toBe(true);
      }
    );

    it.each([
      [99, 101],
      [199, 200],
      [100, 200],
      [0, 100],
      [50, 150],
    ])(
      "should_returnFalse_when_boundsSpanDifferentHundreds: %s-%s",
      (lower: number, upper: number) => {
        expect(isValidRange(lower, upper)).toBe(false);
      }
    );
  });

  describe("parseOptionalPlusCountToNumber", () => {
    it("should_defaultToOne_when_textIsUndefinedOrEmpty", () => {
      expect(parseOptionalPlusCountToNumber(undefined)).toBe(1);
      expect(parseOptionalPlusCountToNumber("")).toBe(1);
    });

    it.each([
      ["1", 1],
      ["2", 2],
      ["10", 10],
      ["003", 3],
    ])("should_parsePositiveInteger_when_textIsProvided", (text, expected) => {
      expect(parseOptionalPlusCountToNumber(text)).toBe(expected);
    });

    it.each(["a", "-1", "+", "1.5"])(
      "should_returnNaN_when_textIsInvalid: %s",
      (text) => {
        expect(
          Number.isNaN(parseOptionalPlusCountToNumber(text as string))
        ).toBe(true);
      }
    );
  });
});
