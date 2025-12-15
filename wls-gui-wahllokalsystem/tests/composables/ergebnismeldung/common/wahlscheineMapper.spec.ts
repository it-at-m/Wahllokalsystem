import type { WahlscheineDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Wahlscheine } from "@/types/ergebnismeldung/common/Wahlscheine.ts";

import { useWahlscheineTestDataFactory } from "@tests/utils/ergebnismeldung/common/wahlscheineTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useWahlscheineMapper } from "@/composables/ergebnismeldung/common/wahlscheineMapper.ts";

const { createWahlscheineDTO, createWahlscheine } =
  useWahlscheineTestDataFactory();

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

  describe("toDto", () => {
    it("should_createWahlscheineDTO_when_wahlscheineModelIsGiven", () => {
      const objectToMap = createWahlscheine();

      const result = unitUnderTest.toDto(objectToMap);

      const expectedObject: WahlscheineDTO = {
        bezirkUndWahlID: objectToMap.bezirkUndWahlID,
        stimmabgabevermerke: objectToMap.stimmabgabevermerke,
      };

      expect(result).toStrictEqual(expectedObject);
    });
  });
});
