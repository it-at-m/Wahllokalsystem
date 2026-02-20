import type {
  FarbeDTO,
  WahlDTO,
} from "@/api/wls-clients/generated-basisdaten-api";
import type { Farbe } from "@/types/wahl/Farbe.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { WahlDTOWahlartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { StimmzettelumschlaegeBuilder } from "@/types/ergebnismeldung/common/Stimmzettelumschlaege.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

export function useWahlMapper() {
  function toModel(dto: WahlDTO): Wahl {
    return {
      wahlID: dto.wahlID,
      name: dto.name,
      reihenfolge: dto.reihenfolge,
      waehlerverzeichnisNummer: dto.waehlerverzeichnisNummer,
      wahltag: dto.wahltag,
      wahlart: _dtoEnumToModelEnum(dto.wahlart),
      farbe: dto.farbe ? _mapFarbeDtoToModel(dto.farbe) : undefined,
      nummer: dto.nummer,
      beanstandeteWahlbriefe: [],
      stimmzettelumschlaege: StimmzettelumschlaegeBuilder.create(),
    };
  }

  function _mapFarbeDtoToModel(dto: FarbeDTO): Farbe {
    return {
      r: dto.r,
      g: dto.g,
      b: dto.b,
    };
  }

  function _dtoEnumToModelEnum(dtoEnum: WahlDTOWahlartEnum): WahlWahlartEnum {
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

  return {
    toModel,
  };
}
