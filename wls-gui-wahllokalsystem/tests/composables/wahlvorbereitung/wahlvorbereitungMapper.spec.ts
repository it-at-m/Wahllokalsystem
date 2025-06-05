import type {
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
  UrnenwahlvorbereitungDTO,
  UrnenwahlvorbereitungWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";

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
  const {
    toEroeffnungsuhrzeitWriteDTO,
    toUrnenwahlvorbereitungWriteDto,
    toUrnenwahlvorbereitungModel,
    toUrnenwahlSchliessungsuhrzeitModel,
    toUrnenwahlSchliessungsuhrzeitDTO,
  } = useWahlvorbereitungMapper();

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

  describe("toUrnenwahlSchliessungsuhrzeitDTO", () => {
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

      const result = toUrnenwahlSchliessungsuhrzeitDTO(dateToMap);

      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toUrnenwahlSchliessungsuhrzeitModel", () => {
    it("should_returnModel_when_schliessungsUhrzeitDtoIsGiven", () => {
      const schliessungsuhrzeit = "2025-05-23T18:00:00Z";
      const dateToMap: UrnenwahlSchliessungsUhrzeitDTO = {
        wahlbezirkID: "id",
        schliessungsuhrzeit: schliessungsuhrzeit,
      };
      const expectedResult: UrnenwahlSchliessungsuhrzeit = {
        schliessungsuhrzeit: schliessungsuhrzeit,
      };

      const result = toUrnenwahlSchliessungsuhrzeitModel(dateToMap);

      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toUrnenwahlvorbereitungWriteDto", () => {
    it("should_returnDTO_when_validModelIsGiven", () => {
      const urnenwahlvorbereitung: Urnenwahlvorbereitung = {
        wahlbezirkID: "123",
        anzahlWahlkabinen: 5,
        anzahlWahltische: 10,
        anzahlNebenraeume: 2,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: true,
          },
          {
            wahlID: "2",
            anzahl: 2,
            urneVersiegelt: false,
          },
        ],
      };

      const result = toUrnenwahlvorbereitungWriteDto(urnenwahlvorbereitung);

      const expectedResult: UrnenwahlvorbereitungWriteDTO = {
        anzahlWahlkabinen: 5,
        anzahlWahltische: 10,
        anzahlNebenraeume: 2,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: true,
          },
          {
            wahlID: "2",
            anzahl: 2,
            urneVersiegelt: false,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toUrnenwahlvorbereitungModel", () => {
    it("should_returnModel_when_validDTOIsGiven", () => {
      const urnenwahlvorbereitungDTO: UrnenwahlvorbereitungDTO = {
        wahlbezirkID: "123",
        anzahlWahlkabinen: 5,
        anzahlWahltische: 10,
        anzahlNebenraeume: 2,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: true,
          },
          {
            wahlID: "2",
            anzahl: 2,
            urneVersiegelt: false,
          },
        ],
      };

      const result = toUrnenwahlvorbereitungModel(urnenwahlvorbereitungDTO);

      const expectedResult: Urnenwahlvorbereitung = {
        wahlbezirkID: "123",
        anzahlWahlkabinen: 5,
        anzahlWahltische: 10,
        anzahlNebenraeume: 2,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: true,
          },
          {
            wahlID: "2",
            anzahl: 2,
            urneVersiegelt: false,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
