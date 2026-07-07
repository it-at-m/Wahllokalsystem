import type { WahlDTO } from "@/api/wls-clients/generated-basisdaten-api";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { WahlDTOWahlartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { useWahlMapper } from "@/composables/wahl/wahlMapper.ts";
import { StimmzettelumschlaegeBuilder } from "@/types/ergebnismeldung/common/Stimmzettelumschlaege.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

describe("wahlMapper", () => {
  const { toModel } = useWahlMapper();
  const { prepareWahlDTO } = useWahlTestDataFactory();

  describe("toModel", () => {
    it("should_returnModel_when_dtoIsGiven", () => {
      const dto: WahlDTO = prepareWahlDTO()
        .wahlart(WahlDTOWahlartEnum.Baw)
        .build();

      if (!dto.farbe) {
        throw new Error("Testdaten haben keine Farbe");
      }
      const expectedFarbe = { r: dto.farbe.r, g: dto.farbe.g, b: dto.farbe.b };

      const expectedModel: Wahl = {
        wahlID: dto.wahlID,
        name: dto.name,
        reihenfolge: dto.reihenfolge,
        waehlerverzeichnisNummer: dto.waehlerverzeichnisNummer,
        wahltag: dto.wahltag,
        wahlart: WahlWahlartEnum.Baw,
        farbe: expectedFarbe,
        nummer: dto.nummer,
        beanstandeteWahlbriefe: [],
        stimmzettelumschlaege: StimmzettelumschlaegeBuilder.create(),
        kennzeichen: dto.kennzeichen,
      };

      const model: Wahl = toModel(dto);

      expect(model).toEqual(expectedModel);
    });

    it("should_returnModelWithUndefined_when_dtoIsGivenWithUndefined", () => {
      const dto: WahlDTO = prepareWahlDTO()
        .wahlart(WahlDTOWahlartEnum.Baw)
        .nummer(undefined)
        .build();

      const expectedModel: Wahl = {
        wahlID: dto.wahlID,
        name: dto.name,
        reihenfolge: dto.reihenfolge,
        waehlerverzeichnisNummer: dto.waehlerverzeichnisNummer,
        wahltag: dto.wahltag,
        wahlart: WahlWahlartEnum.Baw,
        farbe: {
          r: dto.farbe.r,
          g: dto.farbe.g,
          b: dto.farbe.b,
        },
        nummer: undefined,
        beanstandeteWahlbriefe: [],
        stimmzettelumschlaege: StimmzettelumschlaegeBuilder.create(),
        kennzeichen: dto.kennzeichen,
      };

      const model: Wahl = toModel(dto);

      expect(model).toEqual(expectedModel);
    });
  });
});
