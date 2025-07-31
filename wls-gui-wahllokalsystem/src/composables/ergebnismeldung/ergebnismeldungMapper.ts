import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

export function useErgebnismeldungMapper() {
  function toModel(dto: StimmzettelumschlaegeDTO): Stimmzettelumschlaege {
    {
      return {
        wahlID: dto.bezirkUndWahlID.wahlID,
        wahlbezirkID: dto.bezirkUndWahlID.wahlbezirkID,
        urneneroeffnungsUhrzeit: dto.urneneroeffnungsUhrzeit,
        anzahlWaehler: dto.anzahlWaehler,
      };
    }
  }

  function toDto(model: Stimmzettelumschlaege): StimmzettelumschlaegeDTO {
    {
      return {
        bezirkUndWahlID: _wahlIDAndWahlbezirkIDToBezirkUndWahlID(
          model.wahlID,
          model.wahlbezirkID
        ),
        urneneroeffnungsUhrzeit: model.urneneroeffnungsUhrzeit,
        anzahlWaehler: model.anzahlWaehler,
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
