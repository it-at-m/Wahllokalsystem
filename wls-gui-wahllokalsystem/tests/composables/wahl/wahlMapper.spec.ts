import type { WahlDTO } from "@/api/wls-clients/generated-basisdaten-api";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { WahlDTOWahlartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { useWahlMapper } from "@/composables/wahl/wahlMapper.ts";
import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

describe("WahlMapper", () => {
  const { toModel, toDto } = useWahlMapper();
  const { prepareWahlDTO, prepareWahl } = useWahlTestDataFactory();

  it("should_returnModel_when_dtoIsGiven", () => {
    const dto: WahlDTO = prepareWahlDTO()
      .wahlart(WahlDTOWahlartEnum.Baw)
      .build();

    const expectedFarbe = dto.farbe
      ? { r: dto.farbe.r, g: dto.farbe.g, b: dto.farbe.b }
      : undefined;

    const expectedModel: Wahl = {
      wahlID: dto.wahlID,
      name: dto.name,
      reihenfolge: dto.reihenfolge,
      waehlerverzeichnisnummer: dto.waehlerverzeichnisnummer,
      wahltag: dto.wahltag,
      wahlart: WahlWahlartEnum.Baw,
      farbe: expectedFarbe,
      nummer: dto.nummer,
    };

    const model: Wahl = toModel(dto);

    expect(model).toEqual(expectedModel);
  });

  it("should_returnDto_when_modelIsGiven", () => {
    const model: Wahl = prepareWahl().wahlart(WahlWahlartEnum.Beb).build();

    const expectedFarbeDTO = model.farbe
      ? { r: model.farbe.r, g: model.farbe.g, b: model.farbe.b }
      : undefined;

    const expectedDto: WahlDTO = {
      wahlID: model.wahlID,
      name: model.name,
      reihenfolge: model.reihenfolge,
      waehlerverzeichnisnummer: model.waehlerverzeichnisnummer,
      wahltag: model.wahltag,
      wahlart: WahlDTOWahlartEnum.Beb,
      farbe: expectedFarbeDTO,
      nummer: model.nummer,
    };

    const dto: WahlDTO = toDto(model);

    expect(dto).toEqual(expectedDto);
  });
});
