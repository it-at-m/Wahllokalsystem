import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

export function useErgebnisermittlungMapper() {
  function toDto(
    model: Stimmzettelumschlaege,
    wahlID: string,
    wahlbezirkID: string
  ): StimmzettelumschlaegeDTO {
    const dto: StimmzettelumschlaegeDTO = {
      bezirkUndWahlID: _wahlIDAndWahlbezirkIDToBezirkUndWahlID(
        wahlID,
        wahlbezirkID
      ),
      anzahlWaehler: model.anzahlWaehler != null ? model.anzahlWaehler : 0,
    };

    if (model.urneneroeffnungsUhrzeit) {
      dto.urneneroeffnungsUhrzeit = toYyyyMmDdWithTimeWithoutTimezoneOffset(
        model.urneneroeffnungsUhrzeit
      );
    }

    return dto;
  }

  function toModel(dto: StimmzettelumschlaegeDTO): Stimmzettelumschlaege {
    const model: Stimmzettelumschlaege = {
      anzahlWaehler: dto.anzahlWaehler != null ? dto.anzahlWaehler : 0,
    };

    if (dto.urneneroeffnungsUhrzeit) {
      model.urneneroeffnungsUhrzeit = new Date(dto.urneneroeffnungsUhrzeit);
    }

    return model;
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
    toDto,
    toModel,
  };
}
