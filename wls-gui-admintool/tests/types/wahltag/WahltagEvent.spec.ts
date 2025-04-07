import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { compareByNummerAsc } from "@/types/wahltag/WahltagEvent";

const { prepareWahltagEvent } = useWahltagTestDataFactory();

describe("WahltagEvent.ts", () => {
  describe("compareByNummerAsc", () => {
    it("should_return0_when_bothNummerAttributesAreEqual", () => {
      expect(
        compareByNummerAsc(
          prepareWahltagEvent().nummer("42").build(),
          prepareWahltagEvent().nummer("42").build()
        )
      ).toStrictEqual(0);
    });

    it("should_returnNegativeValue_when_firstNummerIsLowerThanSecondNummer", () => {
      expect(
        compareByNummerAsc(
          prepareWahltagEvent().nummer("12").build(),
          prepareWahltagEvent().nummer("42").build()
        )
      ).toBeLessThan(0);
    });

    it("should_returnPositivValue_when_firstNummerIsLowerThanSecondNummer", () => {
      expect(
        compareByNummerAsc(
          prepareWahltagEvent().nummer("42").build(),
          prepareWahltagEvent().nummer("12").build()
        )
      ).toBeGreaterThan(0);
    });
  });
});
