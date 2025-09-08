import type { StimmzettelumschlaegeDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnisermittlung/StimmzettelumschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";

const { generateRandomString } = useCommonTestDataFactory();

describe("ergebnisermittlungMapper.ts", () => {
  const { toDto, toModel } = useErgebnisermittlungMapper();
  const {
    createStimmzettelumschlaege,
    createBezirkUndWahlIDDto,
    createStimmzettelumschlaegeDto,
  } = useStimmzettelumschlaegeTestDataFactory();

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

      const expectedDto: StimmzettelumschlaegeDTO = {
        bezirkUndWahlID: createBezirkUndWahlIDDto(wahlID, wahlbezirkID),
        anzahlWaehler: model.anzahlWaehler != null ? model.anzahlWaehler : 0,
        urneneroeffnungsUhrzeit: eroeffnungszeit.toISOString(),
      };

      const result = toDto(model, wahlID, wahlbezirkID);

      expect(result).toStrictEqual(expectedDto);
    });
  });

  describe("toModel", () => {
    it("should_returnModel_when_givenDtoWithoutUhrzeit", () => {
      const dto: StimmzettelumschlaegeDTO = createStimmzettelumschlaegeDto();

      const expectedModel: Stimmzettelumschlaege = {
        anzahlWaehler: dto.anzahlWaehler != null ? dto.anzahlWaehler : 0,
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });

    it("should_returnModel_when_givenDtoWithUhrzeit", () => {
      const eroeffnungszeit = "15:00:00";
      const dto: StimmzettelumschlaegeDTO = createStimmzettelumschlaegeDto();
      dto.urneneroeffnungsUhrzeit = eroeffnungszeit;

      const expectedEroeffnungszeit = new Date();
      expectedEroeffnungszeit.setHours(15, 0, 0, 0);

      const expectedModel: Stimmzettelumschlaege = {
        anzahlWaehler: dto.anzahlWaehler != null ? dto.anzahlWaehler : 0,
        urneneroeffnungsUhrzeit: expectedEroeffnungszeit,
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });
  });
});
