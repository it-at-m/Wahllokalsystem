import type {
  StimmzettelerfassungTeamStatusDTO,
  StimmzettelerfassungTeamStatusEntryDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/StimmzettelerfassungTeamStatusEntry.ts";

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

const STATUS_MODEL_ENUM_TO_DISPLAY_STRING: Record<
  StimmzettelerfassungTeamStatusEnum,
  string
> = {
  ABGESCHLOSSEN: "abgeschlossen",
  IN_BEARBEITUNG: "in Bearbeitung",
  REGISTRIERT: "registriert",
  UNTERBROCHEN: "unterbrochen",
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
    return {
      status: STATUS_MODEL_ENUM_TO_DTO_ENUM[model.status],
    };
  }

  function dtoEntryToModelEntry(
    dto: StimmzettelerfassungTeamStatusEntryDTO
  ): StimmzettelerfassungTeamStatusEntry {
    return {
      teamID: dto.teamID,
      status: STATUS_DTO_ENUM_TO_MODEL_ENUM[dto.status],
    };
  }

  function statusModelEnumToDisplayString(
    status: StimmzettelerfassungTeamStatusEnum | null
  ): string {
    if (!status) return "";
    return STATUS_MODEL_ENUM_TO_DISPLAY_STRING[status];
  }

  return {
    dtoToModel,
    modelToDto,
    dtoEntryToModelEntry,
    statusModelEnumToDisplayString,
  };
}
