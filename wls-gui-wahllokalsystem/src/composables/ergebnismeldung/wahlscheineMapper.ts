import type {
  BezirkUndWahlID as BezirkUndWahlIdDTO,
  WahlscheineDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { Wahlscheine } from "@/types/ergebnismeldung/Wahlscheine.ts";

export function useWahlscheineMapper() {
  function toModel(dto: WahlscheineDTO): Wahlscheine {
    return {
      bezirkUndWahlID: _toBezirkUndWahlIDModel(dto.bezirkUndWahlID),
      stimmabgabevermerke: dto.stimmabgabevermerke,
    };
  }

  function toDto(model: Wahlscheine): WahlscheineDTO {
    return {
      bezirkUndWahlID: _toBezirkUndWahlIDDTO(model.bezirkUndWahlID),
      stimmabgabevermerke: model.stimmabgabevermerke,
    };
  }

  function _toBezirkUndWahlIDModel(dto: BezirkUndWahlIdDTO): BezirkUndWahlID {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
    };
  }

  function _toBezirkUndWahlIDDTO(model: BezirkUndWahlID): BezirkUndWahlIdDTO {
    return {
      wahlID: model.wahlID,
      wahlbezirkID: model.wahlbezirkID,
    };
  }

  return {
    toModel,
    toDto,
  };
}
