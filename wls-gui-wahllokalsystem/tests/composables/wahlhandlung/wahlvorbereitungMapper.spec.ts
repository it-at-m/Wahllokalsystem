import type {
  BriefwahlvorbereitungDTO,
  BriefwahlvorbereitungWriteDTO,
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
  UrnenwahlvorbereitungDTO,
  UrnenwahlvorbereitungWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlhandlung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlhandlung/Urnenwahlvorbereitung.ts";
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";

import { afterEach, describe, expect, it, vi } from "vitest";

import { useWahlvorbereitungMapper } from "@/composables/wahlhandlung/wahlvorbereitungMapper.ts";

const mockDefinitions = vi.hoisted(() => ({
  toYyyyMmDdWithTimeWithoutTimezoneOffset: vi.fn(),
}));

vi.mock("@/composables/common/dateTimeFormatter.ts", () => ({
  useDateTimeFormatter: () => ({
    toYyyyMmDdWithTimeWithoutTimezoneOffset:
      mockDefinitions.toYyyyMmDdWithTimeWithoutTimezoneOffset,
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

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("toEroeffnungsuhrzeitWriteDTO", () => {
    it("should_returnDTO_when_dateIsGiven", () => {
      const dateToMap = new Date("2025-05-23T07:05:01+02:00");

      const mockedMappedDate = "2025-05-23T07:05:01";
      mockDefinitions.toYyyyMmDdWithTimeWithoutTimezoneOffset.mockReturnValue(
        mockedMappedDate
      );

      const result = toEroeffnungsuhrzeitWriteDTO(dateToMap);

      const expectedResult: EroeffnungsUhrzeitWriteDTO = {
        eroeffnungsuhrzeit: mockedMappedDate,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toUrnenwahlSchliessungsuhrzeitDTO", () => {
    it("should_returnDTO_when_schliessungsUhrzeitIsGiven", () => {
      const dateToMap = new Date("2025-05-23T18:00:00+02:00");
      const mockedMappedDate = "2025-05-23T18:00:00";

      const expectedResult: UrnenwahlSchliessungsUhrzeitWriteDTO = {
        schliessungsuhrzeit: mockedMappedDate,
      };

      mockDefinitions.toYyyyMmDdWithTimeWithoutTimezoneOffset.mockReturnValue(
        mockedMappedDate
      );

      const result = toUrnenwahlSchliessungsuhrzeitDTO(dateToMap);

      expect(result).toStrictEqual(expectedResult);
    });

    it("should_createDtoWithEqualUhrzeitWithMilliSeconds_when_readDtoWasMappedToModelAndMappedToDto", () => {
      const dateTimeString = "2025-08-13T16:29:31.352";
      const model = toUrnenwahlSchliessungsuhrzeitModel({
        wahlbezirkID: "",
        schliessungsuhrzeit: dateTimeString,
      });

      mockDefinitions.toYyyyMmDdWithTimeWithoutTimezoneOffset.mockReturnValue(
        dateTimeString
      );

      const schliessungsuhrzeitAsDate = new Date(model.schliessungsuhrzeit);
      const dto = toUrnenwahlSchliessungsuhrzeitDTO(schliessungsuhrzeitAsDate);

      expect(dto.schliessungsuhrzeit).toStrictEqual(dateTimeString);
      expect(
        mockDefinitions.toYyyyMmDdWithTimeWithoutTimezoneOffset.mock.calls
      ).toStrictEqual([[schliessungsuhrzeitAsDate]]);
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
