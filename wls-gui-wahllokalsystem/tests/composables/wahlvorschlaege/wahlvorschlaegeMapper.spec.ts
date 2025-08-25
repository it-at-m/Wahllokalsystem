import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";

import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahlvorschlaegeMapper } from "@/composables/wahlvorschlaege/wahlvorschlaegeMapper.ts";

const { prepareWahlvorschlaegeDto } = useWahlvorschlaegeTestDataFactory();
const { toModel } = useWahlvorschlaegeMapper();

describe("wahlvorschlaegeMapper.ts", () => {
  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const dto = prepareWahlvorschlaegeDto().build();

      const model: Wahlvorschlaege = {
        wahlID: dto.wahlID,
        wahlbezirkID: dto.wahlbezirkID,
        stimmzettelgebietID: dto.stimmzettelgebietID,
        wahlvorschlaege: dto.wahlvorschlaege,
      };
      const result = toModel(dto);

      expect(result).toStrictEqual(model);
    });
  });
});
