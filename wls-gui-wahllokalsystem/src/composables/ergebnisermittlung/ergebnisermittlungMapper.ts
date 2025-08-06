import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

export function useErgebnisermittlungMapper() {

  function toDto(
    model: Stimmzettelumschlaege,
    wahlID: string,
    wahlbezirkID: string
  ): StimmzettelumschlaegeDTO {
    {
      return {
        bezirkUndWahlID: _wahlIDAndWahlbezirkIDToBezirkUndWahlID(
          wahlID,
          wahlbezirkID
        ),
        anzahlWaehler: model.anzahlWaehler != null ? model.anzahlWaehler : 0,
      };
    }
  }

  function _wahlIDAndWahlbezirkIDToBezirkUndWahlID(
    wahlID: string,
    wahlbezirkID: string
  ): BezirkUndWahlID {
    return {
      wahlID: wahlID,
      wahlbezirkID: wahlbezirkID,
    };
  }

  return {
    toModel,
    toDto,
  };
}
