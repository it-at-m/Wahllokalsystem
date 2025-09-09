import type { StimmzettelumschlaegeDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelumschlaegeTestDataFactory } from "@tests/utils/ergebnisermittlung/StimmzettelumschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";

const { generateRandomString } = useCommonTestDataFactory();

describe("ergebnisermittlungMapper.ts", () => {
  const { toDto } = useErgebnisermittlungMapper();
  const { createStimmzettelumschlaege, createBezirkUndWahlIDDto } =
    useStimmzettelumschlaegeTestDataFactory();

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
});
