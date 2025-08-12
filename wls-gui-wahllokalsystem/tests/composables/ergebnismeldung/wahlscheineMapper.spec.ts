import type { Wahlscheine } from "@/types/ereignismeldung/Wahlscheine.ts";

import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/wahlscheineTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useWahlscheineMapper } from "@/composables/ergebnismeldung/wahlscheineMapper.ts";

const { createWahlscheineDTO } = useWahlscheineTestDataFactory();

describe("wahlscheineMapper", () => {
  let unitUnderTest: ReturnType<typeof useWahlscheineMapper>;

  beforeEach(() => {
    unitUnderTest = useWahlscheineMapper();
  });

  describe("toModel", () => {
    it("should_createWahlscheineModel_when_wahlscheineDTOIsGiven", () => {
      const objectToMap = createWahlscheineDTO();

      const result = unitUnderTest.toModel(objectToMap);

      const expectedObject: Wahlscheine = {
        bezirkUndWahlID: objectToMap.bezirkUndWahlID,
        stimmabgabevermerke: objectToMap.stimmabgabevermerke,
      };

      expect(result).toStrictEqual(expectedObject);
    });
  });
});
