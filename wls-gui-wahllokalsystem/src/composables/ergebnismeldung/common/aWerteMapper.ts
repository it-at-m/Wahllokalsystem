import type { AWerteDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { AWerte } from "@/types/ergebnisermittlung/AWerte.ts";

export function useAWerteMapper() {
  function toModel(aWerteDto: AWerteDTO): AWerte {
    return {
      bezirkUndWahlID: {
        wahlID: aWerteDto.bezirkUndWahlID.wahlID,
        wahlbezirkID: aWerteDto.bezirkUndWahlID.wahlbezirkID,
      },
      a1: aWerteDto.a1,
      a2: aWerteDto.a2 ?? null,
    };
  }

  return {
    toModel,
  };
}
