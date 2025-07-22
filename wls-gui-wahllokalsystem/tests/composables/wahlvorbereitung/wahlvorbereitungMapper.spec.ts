import type {
  BriefwahlvorbereitungDTO,
  BriefwahlvorbereitungWriteDTO,
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
  UrnenwahlvorbereitungDTO,
  UrnenwahlvorbereitungWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";
import type { Wahlvorbereitung } from "@/types/wahlvorbereitung/Wahlvorbereitung.ts";

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
    toBriefwahlvorbereitungWriteDto,
    toBriefwahlvorbereitungModel,
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
        urneVersiegelt: false,
        wahlbezirkID: "123",
        anzahlWahlkabinen: 5,
        anzahlWahltische: 10,
        anzahlNebenraeume: 2,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
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
            urneVersiegelt: false,
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
            urneVersiegelt: false,
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
        urneVersiegelt: false,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnModelWithUrneVersiegeltTrue_when_allUrneVersiegeltareTrue", () => {
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
            urneVersiegelt: true,
          },
        ],
      };

      const result = toUrnenwahlvorbereitungModel(urnenwahlvorbereitungDTO);

      const expectedResult: Urnenwahlvorbereitung = {
        wahlbezirkID: "123",
        anzahlWahlkabinen: 5,
        anzahlWahltische: 10,
        anzahlNebenraeume: 2,
        urneVersiegelt: true,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnModelWithUrneVersiegeltFalse_when_oneUrneVersiegeltISFalse", () => {
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
        urneVersiegelt: false,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toBriefwahlvorbereitungWriteDto", () => {
    it("should_returnDTO_when_validModelIsGiven", () => {
      const briefwahlvorbereitung: Wahlvorbereitung = {
        urneVersiegelt: false,
        wahlbezirkID: "123",
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
          },
        ],
      };

      const result = toBriefwahlvorbereitungWriteDto(briefwahlvorbereitung);

      const expectedResult: BriefwahlvorbereitungWriteDTO = {
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: false,
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

  describe("toBriefwahlvorbereitungModel", () => {
    it("should_returnModel_when_validDTOIsGiven", () => {
      const briefwahlvorbereitungDTO: BriefwahlvorbereitungDTO = {
        wahlbezirkID: "123",
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: false,
          },
          {
            wahlID: "2",
            anzahl: 2,
            urneVersiegelt: false,
          },
        ],
      };

      const result = toBriefwahlvorbereitungModel(briefwahlvorbereitungDTO);

      const expectedResult: Wahlvorbereitung = {
        wahlbezirkID: "123",
        urneVersiegelt: false,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnModelWithUrneVersiegeltTrue_when_allUrneVersiegeltareTrue", () => {
      const briefwahlvorbereitungDTO: BriefwahlvorbereitungDTO = {
        wahlbezirkID: "123",
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: true,
          },
          {
            wahlID: "2",
            anzahl: 2,
            urneVersiegelt: true,
          },
        ],
      };

      const result = toBriefwahlvorbereitungModel(briefwahlvorbereitungDTO);

      const expectedResult: Wahlvorbereitung = {
        wahlbezirkID: "123",
        urneVersiegelt: true,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnModelWithUrneVersiegeltFalse_when_oneUrneVersiegeltISFalse", () => {
      const briefwahlvorbereitungDTO: BriefwahlvorbereitungDTO = {
        wahlbezirkID: "123",
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
            urneVersiegelt: false,
          },
          {
            wahlID: "2",
            anzahl: 2,
            urneVersiegelt: true,
          },
        ],
      };

      const result = toBriefwahlvorbereitungModel(briefwahlvorbereitungDTO);

      const expectedResult: Wahlvorbereitung = {
        wahlbezirkID: "123",
        urneVersiegelt: false,
        urnenAnzahl: [
          {
            wahlID: "1",
            anzahl: 3,
          },
          {
            wahlID: "2",
            anzahl: 2,
          },
        ],
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
