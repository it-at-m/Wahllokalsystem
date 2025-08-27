import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { BezirkUndWahlIDStapelartDTOStapelartEnum as DtoStapelArtEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
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
      const stapelArtDTO = DtoStapelArtEnum.ObwA;
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

  it.each([
    [DtoStapelArtEnum.ObwA, StapelArtEnum.ObwA],
    [DtoStapelArtEnum.ObwBLeer, StapelArtEnum.ObwBLeer],
    [DtoStapelArtEnum.ObwBUngekennzeichnet, StapelArtEnum.ObwBUngekennzeichnet],
    [DtoStapelArtEnum.ObwCGueltig, StapelArtEnum.ObwCGueltig],
    [DtoStapelArtEnum.ObwCUngueltig, StapelArtEnum.ObwCUngueltig],
    [DtoStapelArtEnum.SrwBawA, StapelArtEnum.SrwBawA],
    [DtoStapelArtEnum.SrwBawB, StapelArtEnum.SrwBawB],
    [DtoStapelArtEnum.SrwBawAB, StapelArtEnum.SrwBawAB],
    [DtoStapelArtEnum.SrwBawDUngueltig, StapelArtEnum.SrwBawDUngueltig],
    [DtoStapelArtEnum.SrwBawBC, StapelArtEnum.SrwBawBC],
  ])(
    "should_mapDtoStapelart%s_when_givenModelStapelart%s",
    (dtoStapelart, modelStapelart) => {
      const dto = prepareErgebnisseDTO()
        .bezirkUndWahlIDStapelart({
          wahlID: "w",
          wahlbezirkID: "b",
          stapelart: dtoStapelart,
        })
        .build();
      const result = toModel(dto);
      expect(result.bezirkUndWahlIDStapelart.stapelArt).toBe(modelStapelart);
    }
  );

  it("should_throwError_when_unknownStapelartProvided", () => {
    const invalidDto = prepareErgebnisseDTO()
      .bezirkUndWahlIDStapelart({
        wahlID: "w",
        wahlbezirkID: "b",
        stapelart: "UNKNOWN" as unknown as DtoStapelArtEnum,
      })
      .build();

    expect(() => toModel(invalidDto)).toThrow("Stapelart nicht gefunden");
  });
});
