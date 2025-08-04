import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnisermittlung/StimmzettelumschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";

describe("ergebnisermittlungMapper.ts", () => {
  const { toModel, toDto } = useErgebnisermittlungMapper();
  const { createStimmzettelumschlaegeDto, createStimmzettelumschlaege } =
    useStimmzettelumschlaegeTestDataFactory();

  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const dto: StimmzettelumschlaegeDTO = createStimmzettelumschlaegeDto();

      const expectedModel: Stimmzettelumschlaege = {
        wahlID: dto.bezirkUndWahlID.wahlID,
        wahlbezirkID: dto.bezirkUndWahlID.wahlbezirkID,
        urneneroeffnungsUhrzeit: dto.urneneroeffnungsUhrzeit,
        anzahlWaehler: dto.anzahlWaehler,
        anzahlWaehler2: dto.anzahlWaehler2,
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });
  });

  describe("toDto", () => {
    it("should_returnDto_when_givenModel", () => {
      const model: Stimmzettelumschlaege = createStimmzettelumschlaege();

      const expectedDto: StimmzettelumschlaegeDTO = {
        bezirkUndWahlID: _createBezirkUndWahlIDDto(model),
        urneneroeffnungsUhrzeit: model.urneneroeffnungsUhrzeit,
        anzahlWaehler: model.anzahlWaehler,
        anzahlWaehler2: model.anzahlWaehler2,
      };

      const result = toDto(model);

      expect(result).toStrictEqual(expectedDto);
    });
  });

  function _createBezirkUndWahlIDDto(model: Stimmzettelumschlaege) {
    const dto: BezirkUndWahlID = {
      wahlID: model.wahlID,
      wahlbezirkID: model.wahlbezirkID,
    };
    return dto;
  }
});
