import type { StimmzettelerfassungStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";

import { StimmzettelerfassungStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

const STATUS_DTO_ENUM_TO_MODEL_ENUM: Record<
  StimmzettelerfassungStatusDTOStatusEnum,
  StimmzettelerfassungStatusEnum
> = {
  STE_BEARBEITUNG: StimmzettelerfassungStatusEnum.SteBearbeitung,
  STE_ABGESCHLOSSEN: StimmzettelerfassungStatusEnum.SteAbgeschlossen,
  BE_ABGESCHLOSSEN: StimmzettelerfassungStatusEnum.BeAbgeschlossen,
};

export function useStimmzettelerfassungStatusMapper() {
  function dtoToModel(
    dto: StimmzettelerfassungStatusDTO
  ): StimmzettelerfassungStatus {
    return {
      status: STATUS_DTO_ENUM_TO_MODEL_ENUM[dto.status],
    };
  }

  return {
    dtoToModel,
  };
}
