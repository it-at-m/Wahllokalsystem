import type { WahlscheineDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlID } from "@/api/wls-clients/generated-monitoring-api";
import type { BezirkUndWahlIDModel } from "@/types/ereignismeldung/BezirkUndWahlIDModel.ts";
import type { Wahlscheine } from "@/types/ereignismeldung/Wahlscheine.ts";

export function useErgebnismeldungMapper() {
  function toModel(dto: WahlscheineDTO): Wahlscheine {
    return {
      bezirkUndWahlID: _toBezirkUndWahlIDModel(dto.bezirkUndWahlID),
      stimmabgabevermerke: dto.stimmabgabevermerke,
    };
  }

  function _toBezirkUndWahlIDModel(dto: BezirkUndWahlID): BezirkUndWahlIDModel {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
    };
  }

  return {
    toModel,
  };
}
