import type {
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

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
  const { toModel, toDTO, toEroeffnungsuhrzeitWriteDTO } = useWahlvorbereitungMapper();

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

  describe("toDTO", () => {
    it("should_returnDTO_when_schliessungsUhrzeitIsGiven", () => {
      const dateToMap = new Date("2025-05-23T18:00:00+02:00");
      const mockedApplyLocalTimezoneOffsetResponse = new Date(
        "2025-05-23T18:00:00Z"
      );
      const expectedResult: UrnenwahlSchliessungsUhrzeitWriteDTO = {
        schliessungsuhrzeit:
          mockedApplyLocalTimezoneOffsetResponse.toISOString(),
      };

      mockDefinitions.applyLocalTimezoneOffset.mockReturnValue(
        mockedApplyLocalTimezoneOffsetResponse
      );

      const result = toDTO(dateToMap);

      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toModel", () => {
    it("should_returnModel_when_schliessungsUhrzeitDtoIsGiven", () => {
      const schliessungsuhrzeit = "2025-05-23T18:00:00Z";
      const dateToMap: UrnenwahlSchliessungsUhrzeitDTO = {
        wahlbezirkID: "id",
        schliessungsuhrzeit: schliessungsuhrzeit,
      };
      const expectedResult: UrnenwahlSchliessungsuhrzeit = {
        schliessungsuhrzeit: schliessungsuhrzeit,
      };

      const result = toModel(dateToMap);

      expect(result).toStrictEqual(expectedResult);
    });
  });
});
