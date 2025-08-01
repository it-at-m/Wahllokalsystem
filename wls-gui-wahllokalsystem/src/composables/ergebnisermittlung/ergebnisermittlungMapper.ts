import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

export function useErgebnisermittlungMapper() {
  function toModel(dto: StimmzettelumschlaegeDTO): Stimmzettelumschlaege {
    {
      return {
        wahlID: dto.bezirkUndWahlID.wahlID,
        wahlbezirkID: dto.bezirkUndWahlID.wahlbezirkID,
        urneneroeffnungsUhrzeit: dto.urneneroeffnungsUhrzeit,
        anzahlWaehler: dto.anzahlWaehler,
        anzahlWaehler2: dto.anzahlWaehler2,
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
        anzahlWaehler2: model.anzahlWaehler2,
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
