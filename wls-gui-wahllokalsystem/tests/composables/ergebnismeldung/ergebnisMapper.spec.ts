import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { BezirkUndWahlIDStapelartDTOStapelartEnum as DtoStapelArtEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/ergebnisMapper.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { toModel, toDto } = useErgebnisMapper();
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

    it.each([
      [DtoStapelArtEnum.ObwA, StapelArtEnum.ObwA],
      [DtoStapelArtEnum.ObwBLeer, StapelArtEnum.ObwBLeer],
      [
        DtoStapelArtEnum.ObwBUngekennzeichnet,
        StapelArtEnum.ObwBUngekennzeichnet,
      ],
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

  describe("toDto", () => {
    it("should_returnDto_when_givenModel", () => {
      const wahlbezirkID = "wahlbezirkID";
      const wahlID = "wahlID";
      const stapelArtDTO = DtoStapelArtEnum.ObwA;
      const stapelArtModel = StapelArtEnum.ObwA;

      const modelErgebnis = createErgebnis();
      const modelErgebnisse = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelArt: stapelArtModel,
        })
        .ergebnisse([modelErgebnis])
        .build();

      const dtoErgebnis = createErgebnisDTO({
        ergebnis: modelErgebnis.ergebnis,
        kandidatID: undefined,
        numIndex: undefined,
        wahlvorschlagID: undefined,
        wahlvorschlagsordnungszahl: undefined,
      });

      const expectedDto = prepareErgebnisseDTO()
        .bezirkUndWahlIDStapelart({
          wahlID: wahlID,
          wahlbezirkID: wahlbezirkID,
          stapelart: stapelArtDTO,
        })
        .ergebnisse([dtoErgebnis])
        .build();

      const result = toDto(modelErgebnisse);

      expect(result).toStrictEqual(expectedDto);
      expect(result.ergebnisse).not.toBe(expectedDto.ergebnisse);
    });

    it.each([
      [StapelArtEnum.ObwA, DtoStapelArtEnum.ObwA],
      [StapelArtEnum.ObwBLeer, DtoStapelArtEnum.ObwBLeer],
      [
        StapelArtEnum.ObwBUngekennzeichnet,
        DtoStapelArtEnum.ObwBUngekennzeichnet,
      ],
      [StapelArtEnum.ObwCGueltig, DtoStapelArtEnum.ObwCGueltig],
      [StapelArtEnum.ObwCUngueltig, DtoStapelArtEnum.ObwCUngueltig],
      [StapelArtEnum.SrwBawA, DtoStapelArtEnum.SrwBawA],
      [StapelArtEnum.SrwBawB, DtoStapelArtEnum.SrwBawB],
      [StapelArtEnum.SrwBawAB, DtoStapelArtEnum.SrwBawAB],
      [StapelArtEnum.SrwBawDUngueltig, DtoStapelArtEnum.SrwBawDUngueltig],
      [StapelArtEnum.SrwBawBC, DtoStapelArtEnum.SrwBawBC],
    ])(
      "should_mapModelStapelart%s_when_givenDtoStapelart%s",
      (modelStapelart, dtoStapelart) => {
        const model = prepareErgebnisse()
          .bezirkUndWahlIDStapelart({
            wahlID: "w",
            wahlbezirkID: "b",
            stapelArt: modelStapelart,
          })
          .build();

        const result = toDto(model);
        expect(result.bezirkUndWahlIDStapelart.stapelart).toBe(dtoStapelart);
      }
    );

    it("should_throwError_when_unknownStapelartProvided", () => {
      const invalidModel = prepareErgebnisse()
        .bezirkUndWahlIDStapelart({
          wahlID: "w",
          wahlbezirkID: "b",
          stapelArt: "UNKNOWN" as unknown as StapelArtEnum,
        })
        .build();

      expect(() => toDto(invalidModel)).toThrow("Stapelart nicht gefunden");
    });
  });
});
