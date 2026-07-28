import type { StimmzettelerfassungTeamStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { StimmzettelerfassungTeamStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const STATUS_DTO_ENUM_TO_MODEL_ENUM: Record<
  StimmzettelerfassungTeamStatusDTOStatusEnum,
  StimmzettelerfassungTeamStatusEnum
> = {
  ABGESCHLOSSEN: StimmzettelerfassungTeamStatusDTOStatusEnum.Abgeschlossen,
  IN_BEARBEITUNG: StimmzettelerfassungTeamStatusDTOStatusEnum.InBearbeitung,
  REGISTRIERT: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
  UNTERBROCHEN: StimmzettelerfassungTeamStatusDTOStatusEnum.Unterbrochen,
};

const STATUS_MODEL_ENUM_TO_DTO_ENUM: Record<
  StimmzettelerfassungTeamStatusEnum,
  StimmzettelerfassungTeamStatusDTOStatusEnum
> = {
  ABGESCHLOSSEN: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
  IN_BEARBEITUNG: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
  REGISTRIERT: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
  UNTERBROCHEN: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
};

export function useStimmzettelerfassungTeamStatusMapper() {
  function dtoToModel(
    dto: StimmzettelerfassungTeamStatusDTO
  ): StimmzettelerfassungTeamStatus {
    return {
      status: STATUS_DTO_ENUM_TO_MODEL_ENUM[dto.status],
    };
  }

  function modelToDto(
    model: StimmzettelerfassungTeamStatus
  ): StimmzettelerfassungTeamStatusDTO {
    if (!model) {
      throw new Error("Status nicht gesetzt");
    }
    return {
      status: STATUS_MODEL_ENUM_TO_DTO_ENUM[model.status],
    };
  }

  return {
    dtoToModel,
    modelToDto,
  };
}
