import type { EroeffnungsUhrzeitWriteDTO } from "@/api/wls-clients/generated-wahlvorbereitung-api";

import { describe, expect, it, vi } from "vitest";

import { useWahlvorbereitungMapper } from "@/composables/wahlvorbereitung/wahlvorbereitungMapper.ts";

const mockDefinitions = vi.hoisted(() => ({
  applyLocalTimezoneOffset: vi.fn(),
}));

vi.mock("@/composables/common/dateTimeFormatter.ts", () => ({
  useDateTimeFormatter: () => ({
    applyLocalTimezoneOffset: mockDefinitions.applyLocalTimezoneOffset,
  }),
}));

describe("wahlvorbereitungMapper.ts", () => {
  const { toEroeffnungsuhrzeitWriteDTO } = useWahlvorbereitungMapper();

  describe("toEroeffnungsuhrzeitWriteDTO", () => {
    it("should_returnDTO_when_dateIsGiven", () => {
      const dateToMap = new Date("2025-05-23T07:05:01+02:00");

      const mockedApplyLocalTimezoneOffsetResponse = new Date(
        "2025-05-23T07:05:01Z"
      );
      mockDefinitions.applyLocalTimezoneOffset.mockReturnValue(
        mockedApplyLocalTimezoneOffsetResponse
      );

      const result = toEroeffnungsuhrzeitWriteDTO(dateToMap);

      const expectedResult: EroeffnungsUhrzeitWriteDTO = {
        eroeffnungsuhrzeit:
          mockedApplyLocalTimezoneOffsetResponse.toISOString(),
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
