import type { KopfdatenDTO } from "@/api/wls-clients/generated-basisdaten-api";
import type { Kopfdaten } from "@/types/kopfdaten/kopfdaten.ts";

import { KopfdatenDTOStimmzettelgebietsartEnum } from "@/api/wls-clients/generated-basisdaten-api";
import { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

export function useKopfdatenMapper() {
  function toModel(dto: KopfdatenDTO): Kopfdaten {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
      gemeinde: dto.gemeinde,
      stimmzettelgebietsart: _dtoEnumToModelEnum(dto.stimmzettelgebietsart),
      stimmzettelgebietsnummer: dto.stimmzettelgebietsnummer,
      stimmzettelgebietsname: dto.stimmzettelgebietsname,
      wahlname: dto.wahlname,
      wahlbezirknummer: dto.wahlbezirknummer,
      maximalErlaubteStimmenProWaehler:
        dto.maximalErlaubteStimmenProWaehler ?? null,
    };
  }

  function _dtoEnumToModelEnum(
    dtoEnum: KopfdatenDTOStimmzettelgebietsartEnum
  ): KopfdatenStimmzettelgebietsartEnum {
    switch (dtoEnum) {
      case KopfdatenDTOStimmzettelgebietsartEnum.Sb:
        return KopfdatenStimmzettelgebietsartEnum.Sb;
      case KopfdatenDTOStimmzettelgebietsartEnum.Sg:
        return KopfdatenStimmzettelgebietsartEnum.Sg;
      case KopfdatenDTOStimmzettelgebietsartEnum.Sk:
        return KopfdatenStimmzettelgebietsartEnum.Sk;
      case KopfdatenDTOStimmzettelgebietsartEnum.Wk:
        return KopfdatenStimmzettelgebietsartEnum.Wk;
    }
  }

  return {
    toModel,
  };
}
