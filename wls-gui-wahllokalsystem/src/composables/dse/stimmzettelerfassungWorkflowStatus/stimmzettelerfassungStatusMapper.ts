import type { StimmzettelerfassungStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatus.ts";

import { StimmzettelerfassungStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

const STATUS_DTO_ENUM_TO_MODEL_ENUM: Record<
  StimmzettelerfassungStatusDTOStatusEnum,
  StimmzettelerfassungStatusEnum
> = {
  STE_BEARBEITUNG: StimmzettelerfassungStatusEnum.SteBearbeitung,
  STE_ABGESCHLOSSEN: StimmzettelerfassungStatusEnum.SteAbgeschlossen,
  BE_ABGESCHLOSSEN: StimmzettelerfassungStatusEnum.BeAbgeschlossen,
};

const STATUS_MODEL_ENUM_TO_DTO_ENUM: Record<
  StimmzettelerfassungStatusEnum,
  StimmzettelerfassungStatusDTOStatusEnum
> = {
  STE_BEARBEITUNG: StimmzettelerfassungStatusDTOStatusEnum.SteBearbeitung,
  STE_ABGESCHLOSSEN: StimmzettelerfassungStatusDTOStatusEnum.SteAbgeschlossen,
  BE_ABGESCHLOSSEN: StimmzettelerfassungStatusDTOStatusEnum.BeAbgeschlossen,
};

export function useStimmzettelerfassungStatusMapper() {
  function dtoToModel(
    dto: StimmzettelerfassungStatusDTO
  ): StimmzettelerfassungStatus {
    return {
      status: STATUS_DTO_ENUM_TO_MODEL_ENUM[dto.status],
    };
  }

  function modelToDto(
    model: StimmzettelerfassungStatus
  ): StimmzettelerfassungStatusDTO {
    return {
      status: STATUS_MODEL_ENUM_TO_DTO_ENUM[model.status],
    };
  }

  return {
    dtoToModel,
    modelToDto,
  };
}
