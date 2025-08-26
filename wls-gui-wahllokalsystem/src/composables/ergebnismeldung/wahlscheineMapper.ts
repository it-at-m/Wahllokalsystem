import type { WahlscheineDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlID as BezirkUndWahlIdDTO } from "@/api/wls-clients/generated-monitoring-api";
import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { Wahlscheine } from "@/types/ergebnismeldung/Wahlscheine.ts";

export function useWahlscheineMapper() {
  function toModel(dto: WahlscheineDTO): Wahlscheine {
    return {
      bezirkUndWahlID: _toBezirkUndWahlIDModel(dto.bezirkUndWahlID),
      stimmabgabevermerke: dto.stimmabgabevermerke,
    };
  }

  function _toBezirkUndWahlIDModel(dto: BezirkUndWahlIdDTO): BezirkUndWahlID {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
    };
  }

  return {
    toModel,
  };
}
