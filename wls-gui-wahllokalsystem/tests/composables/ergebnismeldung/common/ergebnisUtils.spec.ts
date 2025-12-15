import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useErgebnisUtils } from "@/composables/ergebnismeldung/common/ergebnisUtils.ts";

const { prepareErgebnis } = useErgebnisseTestDataFactory();
const { generateRandomNumber } = useCommonTestDataFactory();

describe("ergebnisUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useErgebnisUtils>;

  beforeEach(() => {
    unitUnderTest = useErgebnisUtils();
  });

  describe("orderedByNumIndexWithNullAtEnd", () => {
    it("should_returnEqual_when_bothNumIndexValuesAreNull", () => {
      const ergebnis1 = prepareErgebnis().numIndex(null).build();
      const ergebnis2 = prepareErgebnis().numIndex(null).build();

      const result = unitUnderTest.orderedByNumIndexWithNullAtEnd(
        ergebnis1,
        ergebnis2
      );

      expect(result).toStrictEqual(0);
    });

    it("should_returnGreater_when_ergebnisAHasNoNumIndexButErgebnisBHasOne", () => {
      const ergebnis1 = prepareErgebnis().numIndex(null).build();
      const ergebnis2 = prepareErgebnis()
        .numIndex(generateRandomNumber(4))
        .build();

      const result = unitUnderTest.orderedByNumIndexWithNullAtEnd(
        ergebnis1,
        ergebnis2
      );

      expect(result).toStrictEqual(1);
    });

    it("should_returnSmaller_when_ergebnisBHasNoNumIndexButErgebnisAHasOne", () => {
      const ergebnis1 = prepareErgebnis()
        .numIndex(generateRandomNumber(4))
        .build();
      const ergebnis2 = prepareErgebnis().numIndex(null).build();

      const result = unitUnderTest.orderedByNumIndexWithNullAtEnd(
        ergebnis1,
        ergebnis2
      );

      expect(result).toStrictEqual(-1);
    });

    it("should_returnDifferenceOfNumIndex_when_ergebnisAAndErgebnisBHasAValue", () => {
      const numIndex1 = generateRandomNumber(4);
      const ergebnis1 = prepareErgebnis().numIndex(numIndex1).build();

      const numIndex2 = generateRandomNumber(4);
      const ergebnis2 = prepareErgebnis().numIndex(numIndex2).build();

      const result = unitUnderTest.orderedByNumIndexWithNullAtEnd(
        ergebnis1,
        ergebnis2
      );

      expect(result).toStrictEqual(numIndex1 - numIndex2);
    });
  });
});
