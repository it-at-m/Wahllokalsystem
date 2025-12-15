import type { StimmzettelumschlaegeDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnismeldung/common/StimmzettelumschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";

const { generateRandomString } = useCommonTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  toYyyyMmDdWithTimeWithoutTimezoneOffset: vi.fn(),
}));

vi.mock("@/composables/common/dateTimeFormatter.ts", () => ({
  useDateTimeFormatter: () => ({
    toYyyyMmDdWithTimeWithoutTimezoneOffset:
      mockDefinitions.toYyyyMmDdWithTimeWithoutTimezoneOffset,
  }),
}));

describe("ergebnisermittlungMapper.ts", () => {
  const { toDto, toModel } = useErgebnisermittlungMapper();
  const {
    createStimmzettelumschlaege,
    createBezirkUndWahlIDDto,
    createStimmzettelumschlaegeDto,
  } = useStimmzettelumschlaegeTestDataFactory();

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("toDto", () => {
    it("should_returnDto_when_givenModelWithoutUhrzeit", () => {
      const model: Stimmzettelumschlaege = createStimmzettelumschlaege();
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const expectedDto: StimmzettelumschlaegeDTO = {
        bezirkUndWahlID: createBezirkUndWahlIDDto(wahlID, wahlbezirkID),
        anzahlWaehler: model.anzahlWaehler != null ? model.anzahlWaehler : 0,
      };

      const result = toDto(model, wahlID, wahlbezirkID);

      expect(result).toStrictEqual(expectedDto);
    });

    it("should_returnDto_when_givenModelWithUhrzeit", () => {
      const eroeffnungszeit = new Date("2025-08-20T15:00:00.000Z");
      const model: Stimmzettelumschlaege = createStimmzettelumschlaege({
        urneneroeffnungsUhrzeit: eroeffnungszeit,
      });
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const mockedMappedDate = "2025-08-20T15:00:00.000";
      mockDefinitions.toYyyyMmDdWithTimeWithoutTimezoneOffset.mockReturnValue(
        mockedMappedDate
      );

      const expectedDto: StimmzettelumschlaegeDTO = {
        bezirkUndWahlID: createBezirkUndWahlIDDto(wahlID, wahlbezirkID),
        anzahlWaehler: model.anzahlWaehler != null ? model.anzahlWaehler : 0,
        urneneroeffnungsUhrzeit: mockedMappedDate,
      };

      const result = toDto(model, wahlID, wahlbezirkID);

      expect(result).toStrictEqual(expectedDto);
    });
  });

  describe("toModel", () => {
    beforeEach(() => {
      const mockedNow = new Date();
      vi.useFakeTimers({
        now: mockedNow,
      });
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    it("should_returnModel_when_givenDtoWithoutUhrzeit", () => {
      const dto: StimmzettelumschlaegeDTO = createStimmzettelumschlaegeDto();

      const expectedModel: Stimmzettelumschlaege = {
        anzahlWaehler: dto.anzahlWaehler != null ? dto.anzahlWaehler : 0,
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });

    it("should_returnModel_when_givenDtoWithUhrzeit", () => {
      const eroeffnungszeit = "2025-09-25T15:00:00.000";
      const dto: StimmzettelumschlaegeDTO = createStimmzettelumschlaegeDto();
      dto.urneneroeffnungsUhrzeit = eroeffnungszeit;

      const expectedModel: Stimmzettelumschlaege = {
        anzahlWaehler: dto.anzahlWaehler != null ? dto.anzahlWaehler : 0,
        urneneroeffnungsUhrzeit: new Date(eroeffnungszeit),
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });
  });
});
