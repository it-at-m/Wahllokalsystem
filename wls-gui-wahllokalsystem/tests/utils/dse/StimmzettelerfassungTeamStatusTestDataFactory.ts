import type { StimmzettelerfassungTeamStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { StimmzettelerfassungTeamStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

export function useStimmzettelerfassungTeamStatusTestDataFactory() {
  function createStimmzettelerfassungTeamStatusDTOData(
    status: StimmzettelerfassungTeamStatusDTOStatusEnum = StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert
  ): StimmzettelerfassungTeamStatusDTO {
    return { status } as StimmzettelerfassungTeamStatusDTO;
  }

  function createStimmzettelerfassungTeamStatusDtoEnumValue(): StimmzettelerfassungTeamStatusDTOStatusEnum {
    return StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert;
  }

  function createStimmzettelerfassungTeamStatusModel(
    status: StimmzettelerfassungTeamStatusEnum = StimmzettelerfassungTeamStatusEnum.REGISTRIERT
  ): StimmzettelerfassungTeamStatus {
    // return a proper model object (typed)
    return { status } as StimmzettelerfassungTeamStatus;
  }

  function prepareStimmzettelerfassungTeamStatusResponse(
    status: StimmzettelerfassungTeamStatusDTOStatusEnum = StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
    httpStatus = 200
  ) {
    const data: StimmzettelerfassungTeamStatusDTO = {
      status,
    } as StimmzettelerfassungTeamStatusDTO;
    return { status: httpStatus, data };
  }

  return {
    createStimmzettelerfassungTeamStatusDTOData,
    createStimmzettelerfassungTeamStatusDtoEnumValue,
    createStimmzettelerfassungTeamStatusModel,
    prepareStimmzettelerfassungTeamStatusResponse,
  };
}
