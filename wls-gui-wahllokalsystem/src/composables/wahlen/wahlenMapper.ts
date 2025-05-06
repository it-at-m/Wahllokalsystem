import type {
  FarbeDTO,
  WahlDTO,
} from "@/api/wls-clients/generated-basisdaten-api";
import type { Farbe } from "@/types/wahl/Farbe.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { WahlDTOWahlartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

export function useWahlenMapper() {
  function toModel(dto: WahlDTO): Wahl {
    return {
      wahlID: dto.wahlID,
      name: dto.name,
      reihenfolge: dto.reihenfolge,
      waehlerverzeichnisnummer: dto.waehlerverzeichnisnummer,
      wahltag: dto.wahltag,
      wahlart: dtoEnumToModelEnum(dto.wahlart),
      farbe: dto.farbe ? mapFarbeDtoToModel(dto.farbe) : undefined,
      nummer: dto.nummer,
    };
  }

  function toDto(model: Wahl): WahlDTO {
    return {
      wahlID: model.wahlID,
      name: model.name,
      reihenfolge: model.reihenfolge,
      waehlerverzeichnisnummer: model.waehlerverzeichnisnummer,
      wahltag: model.wahltag,
      wahlart: modelEnumToDtoEnum(model.wahlart),
      farbe: model.farbe ? mapFarbeModelToDto(model.farbe) : undefined,
      nummer: model.nummer,
    };
  }

  function mapFarbeDtoToModel(dto: FarbeDTO): Farbe {
    return {
      r: dto.r,
      g: dto.g,
      b: dto.b,
    };
  }

  function mapFarbeModelToDto(model: Farbe): FarbeDTO {
    return {
      r: model.r,
      g: model.g,
      b: model.b,
    };
  }

  function dtoEnumToModelEnum(dtoEnum: WahlDTOWahlartEnum): WahlWahlartEnum {
    switch (dtoEnum) {
      case WahlDTOWahlartEnum.Baw:
        return WahlWahlartEnum.Baw;
      case WahlDTOWahlartEnum.Beb:
        return WahlWahlartEnum.Beb;
      case WahlDTOWahlartEnum.Btw:
        return WahlWahlartEnum.Btw;
      case WahlDTOWahlartEnum.Bzw:
        return WahlWahlartEnum.Bzw;
      case WahlDTOWahlartEnum.Euw:
        return WahlWahlartEnum.Euw;
      case WahlDTOWahlartEnum.Ltw:
        return WahlWahlartEnum.Ltw;
      case WahlDTOWahlartEnum.Mbw:
        return WahlWahlartEnum.Mbw;
      case WahlDTOWahlartEnum.Obw:
        return WahlWahlartEnum.Obw;
      case WahlDTOWahlartEnum.Srw:
        return WahlWahlartEnum.Srw;
      case WahlDTOWahlartEnum.Svw:
        return WahlWahlartEnum.Svw;
      case WahlDTOWahlartEnum.Ve:
        return WahlWahlartEnum.Ve;
    }
  }

  function modelEnumToDtoEnum(modelEnum: WahlWahlartEnum): WahlDTOWahlartEnum {
    switch (modelEnum) {
      case WahlWahlartEnum.Baw:
        return WahlDTOWahlartEnum.Baw;
      case WahlWahlartEnum.Beb:
        return WahlDTOWahlartEnum.Beb;
      case WahlWahlartEnum.Btw:
        return WahlDTOWahlartEnum.Btw;
      case WahlWahlartEnum.Bzw:
        return WahlDTOWahlartEnum.Bzw;
      case WahlWahlartEnum.Euw:
        return WahlDTOWahlartEnum.Euw;
      case WahlWahlartEnum.Ltw:
        return WahlDTOWahlartEnum.Ltw;
      case WahlWahlartEnum.Mbw:
        return WahlDTOWahlartEnum.Mbw;
      case WahlWahlartEnum.Obw:
        return WahlDTOWahlartEnum.Obw;
      case WahlWahlartEnum.Srw:
        return WahlDTOWahlartEnum.Srw;
      case WahlWahlartEnum.Svw:
        return WahlDTOWahlartEnum.Svw;
      case WahlWahlartEnum.Ve:
        return WahlDTOWahlartEnum.Ve;
    }
  }

  return {
    toModel,
    toDto,
  };
}
