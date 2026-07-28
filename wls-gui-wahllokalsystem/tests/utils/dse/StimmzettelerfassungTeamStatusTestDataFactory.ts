import type { StimmzettelerfassungTeamStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelerfassungTeamStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const { getRandomItem } = useCommonTestDataFactory();

export function useStimmzettelerfassungTeamStatusTestDataFactory() {
  function createStimmzettelerfassungTeamStatusDTOData(): StimmzettelerfassungTeamStatusDTO {
    return {
      status: getRandomItem(
        Object.values(StimmzettelerfassungTeamStatusDTOStatusEnum)
      ),
    };
  }

  function createStimmzettelerfassungTeamStatusDtoEnumValue(): StimmzettelerfassungTeamStatusDTOStatusEnum {
    return getRandomItem(
      Object.values(StimmzettelerfassungTeamStatusDTOStatusEnum)
    );
  }

  function createStimmzettelerfassungTeamStatusModel(
    status: StimmzettelerfassungTeamStatusEnum = StimmzettelerfassungTeamStatusEnum.REGISTRIERT
  ): StimmzettelerfassungTeamStatus {
    return { status };
  }

  function prepareStimmzettelerfassungTeamStatusDTO(): Builder<StimmzettelerfassungTeamStatusDTO> {
    return proxyBuilder<StimmzettelerfassungTeamStatusDTO>(
      createStimmzettelerfassungTeamStatusDTOData()
    );
  }

  return {
    createStimmzettelerfassungTeamStatusDTOData,
    createStimmzettelerfassungTeamStatusDtoEnumValue,
    createStimmzettelerfassungTeamStatusModel,
    prepareStimmzettelerfassungTeamStatusDTO,
  };
}
