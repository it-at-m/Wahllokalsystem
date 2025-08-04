import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnisermittlung/StimmzettelumschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";

const { generateRandomString } = useCommonTestDataFactory();

describe("ergebnisermittlungMapper.ts", () => {
  const { toModel, toDto } = useErgebnisermittlungMapper();
  const { createStimmzettelumschlaegeDto, createStimmzettelumschlaege } =
    useStimmzettelumschlaegeTestDataFactory();

  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const dto: StimmzettelumschlaegeDTO = createStimmzettelumschlaegeDto();

      const expectedModel: Stimmzettelumschlaege = {
        anzahlWaehler: dto.anzahlWaehler,
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });
  });

  describe("toDto", () => {
    it("should_returnDto_when_givenModel", () => {
      const model: Stimmzettelumschlaege = createStimmzettelumschlaege();
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const expectedDto: StimmzettelumschlaegeDTO = {
        bezirkUndWahlID: _createBezirkUndWahlIDDto(wahlID, wahlbezirkID),
        anzahlWaehler: model.anzahlWaehler,
      };

      const result = toDto(model, wahlID, wahlbezirkID);

      expect(result).toStrictEqual(expectedDto);
    });
  });

  function _createBezirkUndWahlIDDto(wahlID: string, wahlbezirkID: string) {
    const dto: BezirkUndWahlID = {
      wahlID: wahlID,
      wahlbezirkID: wahlbezirkID,
    };
    return dto;
  }
});
