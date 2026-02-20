import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";

import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahlvorschlaegeMapper } from "@/composables/wahlvorschlaege/wahlvorschlaegeMapper.ts";

const { createWahlvorschlaegeDto } = useWahlvorschlaegeTestDataFactory();
const { toModel } = useWahlvorschlaegeMapper();

describe("wahlvorschlaegeMapper.ts", () => {
  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const dto = createWahlvorschlaegeDto();

      const model: Wahlvorschlaege = {
        wahlID: dto.wahlID,
        wahlbezirkID: dto.wahlbezirkID,
        stimmzettelgebietID: dto.stimmzettelgebietID,
        wahlvorschlaege: dto.wahlvorschlaege,
      };
      const result = toModel(dto);

      expect(result).toStrictEqual(model);
      expect(result.wahlvorschlaege).not.toBe(dto.wahlvorschlaege);
    });
  });
});
