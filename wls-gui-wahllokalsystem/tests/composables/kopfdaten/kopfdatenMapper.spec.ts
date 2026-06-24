import type { Kopfdaten } from "@/types/kopfdaten/kopfdaten.ts";

import { useKopfdatenTestDataFactory } from "@tests/utils/kopfdaten/KopfdatenTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { KopfdatenDTOStimmzettelgebietsartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { useKopfdatenMapper } from "@/composables/kopfdaten/kopfdatenMapper.ts";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

const { prepareKopfdatenDto } = useKopfdatenTestDataFactory();
const { toModel } = useKopfdatenMapper();

describe("kopfdatenMapper.ts", () => {
  describe("toModel", () => {
    it("should_returnModel_when_dtoIsGiven", () => {
      const dto = prepareKopfdatenDto()
        .stimmzettelgebietsart(KopfdatenDTOStimmzettelgebietsartEnum.Sg)
        .build();

      const model: Kopfdaten = {
        wahlID: dto.wahlID,
        wahlbezirkID: dto.wahlbezirkID,
        gemeinde: dto.gemeinde,
        stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum.Sg,
        stimmzettelgebietsnummer: dto.stimmzettelgebietsnummer,
        stimmzettelgebietsname: dto.stimmzettelgebietsname,
        wahlname: dto.wahlname,
        wahlbezirknummer: dto.wahlbezirknummer,
        maximalErlaubteStimmenProWaehler: dto.maximalErlaubteStimmenProWaehler,
      };

      const result = toModel(dto);

      expect(result).toStrictEqual(model);
    });
  });
});
