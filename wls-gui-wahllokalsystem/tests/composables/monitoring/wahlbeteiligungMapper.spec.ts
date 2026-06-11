import type { WaehleranzahlDTO } from "@/api/wls-clients/generated-monitoring-api";
import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import { describe, expect, it, vi } from "vitest";

import { useWahlbeteiligungMapper } from "@/composables/monitoring/wahlbeteiligungMapper.ts";

const mockDefinitions = vi.hoisted(() => ({
  isValidDate: vi.fn(),
}));

vi.mock(
  import("@/composables/common/dateTimeUtils.ts"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      useDateTimeUtils: () => ({
        ...mod.useDateTimeUtils(),
        isValidDate: mockDefinitions.isValidDate,
      }),
    };
  }
);

describe("wahlbeteiligungMapper.ts", () => {
  const { toModel, toDto } = useWahlbeteiligungMapper();

  describe("toDto", () => {
    it("should_returnDTO_when_wahlbeteiligungitIsGiven", () => {
      const date = new Date("2025-05-23T18:00:00");
      const wahlbeteiligungToMap: Waehleranzahl = {
        anzahlWaehler: 17,
        uhrzeit: date,
      };

      mockDefinitions.isValidDate.mockReturnValue(true);

      const expectedResult: WaehleranzahlDTO = {
        anzahlWaehler: 17,
        uhrzeit: "2025-05-23T18:00:00.000",
      };

      const result = toDto(wahlbeteiligungToMap);

      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toModel", () => {
    it("should_returnModelWithUhrzeit_when_wahlbeteiligungDtoIsGivenWithValidDate", () => {
      const uhrzeit = "2025-05-23T18:00:00Z";
      const dtoToMap: WaehleranzahlDTO = {
        anzahlWaehler: 9,
        uhrzeit: uhrzeit,
      };
      const expectedResult: Waehleranzahl = {
        anzahlWaehler: 9,
        uhrzeit: new Date(uhrzeit),
      };

      mockDefinitions.isValidDate.mockReturnValue(true);

      const result = toModel(dtoToMap);

      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnModelWithUndefinedUhrzeit_when_wahlbeteiligungDtoIsGivenWithInvalidDate", () => {
      const dtoToMap: WaehleranzahlDTO = {
        anzahlWaehler: 9,
        uhrzeit: "2025-05-23T18:00:00Z",
      };
      const expectedResult: Waehleranzahl = {
        anzahlWaehler: 9,
        uhrzeit: undefined,
      };

      mockDefinitions.isValidDate.mockReturnValue(false);

      const result = toModel(dtoToMap);

      expect(result).toStrictEqual(expectedResult);
    });
  });
});
