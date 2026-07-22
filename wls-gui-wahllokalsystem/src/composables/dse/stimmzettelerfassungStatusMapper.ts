import type { StimmzettelerfassungStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungStatus.ts";

import { StimmzettelerfassungStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

export function useStimmzettelerfassungStatusMapper() {
  function dtoToModel(
    dto: StimmzettelerfassungStatusDTO
  ): StimmzettelerfassungStatus {
    return {
      status: _dtoStatusEnumToToModel(dto.status),
    };
  }

  function _dtoStatusEnumToToModel(
    dto: StimmzettelerfassungStatusDTOStatusEnum
  ): StimmzettelerfassungStatusEnum {
    switch (dto) {
      case StimmzettelerfassungStatusDTOStatusEnum.SteBearbeitung:
        return StimmzettelerfassungStatusEnum.SteBearbeitung;
      case StimmzettelerfassungStatusDTOStatusEnum.SteAbgeschlossen:
        return StimmzettelerfassungStatusEnum.SteAbgeschlossen;
      case StimmzettelerfassungStatusDTOStatusEnum.BeAbgeschlossen:
        return StimmzettelerfassungStatusEnum.BeAbgeschlossen;
      default:
        throw new Error("StimmzettelerfassungStatus nicht gefunden");
    }
  }

  return {
    dtoToModel,
  };
}
