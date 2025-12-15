import type { BegruendungDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Begruendung } from "@/types/ergebnismeldung/common/Begruendung.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";

import { useBegruendungTestDataFactory } from "@tests/utils/ergebnismeldung/common/begruendungTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import {
  BezirkUndWahlIDStapelartDTOStapelartEnum,
  BezirkUndWahlIDStapelartDTOStapelartEnum as DtoStapelArtEnum,
  GetErgebnisseStapelartEnum,
  PostErgebnisseStapelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/common/ergebnisMapper.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const {
  toModel,
  toDto,
  toPostErgebnisseStapelartEnum,
  toGetErgebnisseStapelartEnum,
  toBegruendungModel,
  toBegruendungDto,
} = useErgebnisMapper();
const {
  prepareErgebnisseDTO,
  createErgebnisDTO,
  prepareErgebnisse,
  createErgebnis,
} = useErgebnisseTestDataFactory();
const { prepareBegruendungDTO, prepareBegruendung } =
  useBegruendungTestDataFactory();

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
      [DtoStapelArtEnum.MbwA, StapelArtEnum.MbwA],
      [DtoStapelArtEnum.MbwAB, StapelArtEnum.MbwAB],
      [DtoStapelArtEnum.MbwB, StapelArtEnum.MbwB],
      [DtoStapelArtEnum.MbwBC, StapelArtEnum.MbwBC],
      [DtoStapelArtEnum.MbwDUngueltig, StapelArtEnum.MbwDUngueltig],
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
        ergebnis: modelErgebnis.ergebnis ?? 0,
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
      [StapelArtEnum.MbwA, DtoStapelArtEnum.MbwA],
      [StapelArtEnum.MbwAB, DtoStapelArtEnum.MbwAB],
      [StapelArtEnum.MbwB, DtoStapelArtEnum.MbwB],
      [StapelArtEnum.MbwBC, DtoStapelArtEnum.MbwBC],
      [StapelArtEnum.MbwDUngueltig, DtoStapelArtEnum.MbwDUngueltig],
    ])(
      "should_mapDtoStapelart%s_when_givenModelStapelart%s",
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

  describe("toPostErgebnisseStapelartEnum", () => {
    const mapStapelArtModelToDtoCases: string[][] = [
      [StapelArtEnum.ObwA, PostErgebnisseStapelartEnum.ObwA],
      [StapelArtEnum.ObwBLeer, PostErgebnisseStapelartEnum.ObwBLeer],
      [
        StapelArtEnum.ObwBUngekennzeichnet,
        PostErgebnisseStapelartEnum.ObwBUngekennzeichnet,
      ],
      [StapelArtEnum.ObwCGueltig, PostErgebnisseStapelartEnum.ObwCGueltig],
      [StapelArtEnum.ObwCUngueltig, PostErgebnisseStapelartEnum.ObwCUngueltig],
      [StapelArtEnum.SrwBawA, PostErgebnisseStapelartEnum.SrwBawA],
      [StapelArtEnum.SrwBawB, PostErgebnisseStapelartEnum.SrwBawB],
      [StapelArtEnum.SrwBawAB, PostErgebnisseStapelartEnum.SrwBawAB],
      [
        StapelArtEnum.SrwBawDUngueltig,
        PostErgebnisseStapelartEnum.SrwBawDUngueltig,
      ],
      [StapelArtEnum.SrwBawBC, PostErgebnisseStapelartEnum.SrwBawBC],
      [StapelArtEnum.MbwA, PostErgebnisseStapelartEnum.MbwA],
      [StapelArtEnum.MbwAB, PostErgebnisseStapelartEnum.MbwAB],
      [StapelArtEnum.MbwB, PostErgebnisseStapelartEnum.MbwB],
      [StapelArtEnum.MbwBC, PostErgebnisseStapelartEnum.MbwBC],
      [StapelArtEnum.MbwDUngueltig, PostErgebnisseStapelartEnum.MbwDUngueltig],
      [
        StapelArtEnum.StimmzettelUmschlaege,
        PostErgebnisseStapelartEnum.StimmzettelUmschlaege,
      ],
    ];

    it("should_haveCompleteListOfMappingTestCases_when_allStapelArtValuesWereTestedInMapping", () => {
      expect(mapStapelArtModelToDtoCases.length).toStrictEqual(
        Object.values(StapelArtEnum).length
      );
    });

    it.each(mapStapelArtModelToDtoCases)(
      "should_mapModelStapelart%s_when_givenDtoStapelart%s",
      (modelStapelart, dtoStapelart) => {
        const result = toPostErgebnisseStapelartEnum(
          modelStapelart as StapelArtEnum
        );
        expect(result).toBe(dtoStapelart);
      }
    );

    it("should_returnAValueForAllStapelartEnumValues_when_stapelArtIsGiven", () => {
      const enumValues = Object.values(StapelArtEnum);
      enumValues.forEach((value) => {
        expect(() => toPostErgebnisseStapelartEnum(value)).not.toThrow();
      });
    });
  });

  describe("toGetErgebnisseStapelartEnum", () => {
    const mapStapelArtModelToDtoCases: string[][] = Array.from([
      [StapelArtEnum.ObwA, GetErgebnisseStapelartEnum.ObwA],
      [StapelArtEnum.ObwBLeer, GetErgebnisseStapelartEnum.ObwBLeer],
      [
        StapelArtEnum.ObwBUngekennzeichnet,
        GetErgebnisseStapelartEnum.ObwBUngekennzeichnet,
      ],
      [StapelArtEnum.ObwCGueltig, GetErgebnisseStapelartEnum.ObwCGueltig],
      [StapelArtEnum.ObwCUngueltig, GetErgebnisseStapelartEnum.ObwCUngueltig],
      [StapelArtEnum.SrwBawA, GetErgebnisseStapelartEnum.SrwBawA],
      [StapelArtEnum.SrwBawB, GetErgebnisseStapelartEnum.SrwBawB],
      [StapelArtEnum.SrwBawAB, GetErgebnisseStapelartEnum.SrwBawAB],
      [
        StapelArtEnum.SrwBawDUngueltig,
        GetErgebnisseStapelartEnum.SrwBawDUngueltig,
      ],
      [StapelArtEnum.SrwBawBC, GetErgebnisseStapelartEnum.SrwBawBC],
      [StapelArtEnum.MbwA, GetErgebnisseStapelartEnum.MbwA],
      [StapelArtEnum.MbwAB, GetErgebnisseStapelartEnum.MbwAB],
      [StapelArtEnum.MbwB, GetErgebnisseStapelartEnum.MbwB],
      [StapelArtEnum.MbwBC, GetErgebnisseStapelartEnum.MbwBC],
      [StapelArtEnum.MbwDUngueltig, GetErgebnisseStapelartEnum.MbwDUngueltig],
      [
        StapelArtEnum.StimmzettelUmschlaege,
        GetErgebnisseStapelartEnum.StimmzettelUmschlaege,
      ],
    ]);

    it("should_haveCompleteListOfMappingTestCases_when_allStapelArtValuesWereTestedInMapping", () => {
      expect(mapStapelArtModelToDtoCases.length).toStrictEqual(
        Object.values(StapelArtEnum).length
      );
    });

    it.each(mapStapelArtModelToDtoCases)(
      "should_mapModelStapelart%s_when_givenDtoStapelart%s",
      (modelStapelart, dtoStapelart) => {
        const result = toGetErgebnisseStapelartEnum(
          modelStapelart as StapelArtEnum
        );
        expect(result).toBe(dtoStapelart);
      }
    );

    it("should_returnAValueForAllStapelartEnumValues_when_stapelArtIsGiven", () => {
      const enumValues = Object.values(StapelArtEnum);
      enumValues.forEach((value) => {
        expect(() => toGetErgebnisseStapelartEnum(value)).not.toThrow();
      });
    });
  });

  describe("toBegruendungModel", () => {
    it("should_returnModel_when_givenDTO", () => {
      const dtoBegruendung: BegruendungDTO = prepareBegruendungDTO()
        .bezirkUndWahlIDStapelart({
          wahlID: "wahlID",
          wahlbezirkID: "wahlbezirkID",
          stapelart: DtoStapelArtEnum.StimmzettelUmschlaege,
        })
        .grund("eben weil")
        .build();

      const modelBegruendung: Begruendung = prepareBegruendung()
        .wahlID(dtoBegruendung.bezirkUndWahlIDStapelart.wahlID)
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund(dtoBegruendung.grund)
        .nachzaehlung(undefined)
        .unstimmigkeiten(undefined)
        .build();

      const result = toBegruendungModel(dtoBegruendung);

      expect(result).toStrictEqual(modelBegruendung);
    });
  });

  describe("toBegruendungDto", () => {
    it("should_returnDto_when_givenModel", () => {
      const modelBegruendung: Begruendung = prepareBegruendung()
        .wahlID("wahlID")
        .stapelart(StapelArtEnum.StimmzettelUmschlaege)
        .grund("so halt")
        .nachzaehlung(undefined)
        .unstimmigkeiten(undefined)
        .build();

      const expectedDto: BegruendungDTO = {
        bezirkUndWahlIDStapelart: {
          wahlID: modelBegruendung.wahlID,
          wahlbezirkID: "wahlbezirkID",
          stapelart:
            BezirkUndWahlIDStapelartDTOStapelartEnum.StimmzettelUmschlaege,
        },
        grund: modelBegruendung.grund,
        nachzaehlung: modelBegruendung.nachzaehlung,
        unstimmigkeiten: modelBegruendung.unstimmigkeiten,
      };

      const result = toBegruendungDto(modelBegruendung, "wahlbezirkID");

      expect(result).toStrictEqual(expectedDto);
      expect(result).not.toBe(expectedDto);
    });

    it("should_mapAllFieldsCorrectly_when_givenCompleteModel", () => {
      const modelBegruendung: Begruendung = prepareBegruendung()
        .wahlID("wahlID")
        .stapelart(StapelArtEnum.MbwA)
        .grund("So halt")
        .nachzaehlung(true)
        .unstimmigkeiten(true)
        .build();

      const expectedDto: BegruendungDTO = {
        bezirkUndWahlIDStapelart: {
          wahlID: modelBegruendung.wahlID,
          wahlbezirkID: "wahlbezirkID",
          stapelart: BezirkUndWahlIDStapelartDTOStapelartEnum.MbwA,
        },
        grund: modelBegruendung.grund,
        nachzaehlung: modelBegruendung.nachzaehlung,
        unstimmigkeiten: modelBegruendung.unstimmigkeiten,
      };

      const result = toBegruendungDto(modelBegruendung, "wahlbezirkID");

      expect(result).toStrictEqual(expectedDto);
      expect(result).not.toBe(expectedDto);
    });

    it("should_throwError_when_givenInvalidStapelart", () => {
      const modelBegruendung: Begruendung = prepareBegruendung()
        .wahlID("wahlID")
        .stapelart("UNKNOWN" as unknown as StapelArtEnum)
        .grund("So halt")
        .nachzaehlung(undefined)
        .unstimmigkeiten(undefined)
        .build();

      expect(() => toBegruendungDto(modelBegruendung, "wahlbezirkID")).toThrow(
        "Stapelart nicht gefunden"
      );
    });
  });
});
