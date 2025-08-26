import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { BezirkUndWahlIDStapelartDTOStapelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/ergebnisMapper.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { toModel } = useErgebnisMapper();
const {
  prepareErgebnisseDTO,
  createErgebnisDTO,
  prepareErgebnisse,
  createErgebnis,
} = useErgebnisseTestDataFactory();

describe("ergebnisMapper.ts", () => {
  describe("toModel", () => {
    it("should_returnModel_when_givenDto", () => {
      const wahlbezirkID = "wahlbezirkID";
      const wahlID = "wahlID";
      const stapelArtDTO = BezirkUndWahlIDStapelartDTOStapelartEnum.ObwA;
      const stapelArtModel = StapelArtEnum.ObwA;

      const dtoErgebnis = createErgebnisDTO();
      const dtoErgebnisse = prepareErgebnisseDTO()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelart: stapelArtDTO,
        })
        .ergebnisse([dtoErgebnis])
        .build();

      const modelErgebnis: Ergebnis = createErgebnis();
      modelErgebnis.ergebnis = dtoErgebnis.ergebnis;

      const modelErgebnisse: Ergebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: stapelArtModel,
        })
        .ergebnisse([modelErgebnis])
        .build();

      const result = toModel(dtoErgebnisse);

      expect(result).toStrictEqual(modelErgebnisse);
      expect(result.ergebnisse).not.toBe(dtoErgebnisse.ergebnisse);
    });
  });
});
