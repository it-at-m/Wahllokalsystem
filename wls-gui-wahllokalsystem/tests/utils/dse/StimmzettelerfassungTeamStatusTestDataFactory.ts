import type { StimmzettelerfassungTeamStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelerfassungTeamStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

const { getRandomItem, generateRandomString } = useCommonTestDataFactory();

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

  function createStimmzettelerfassungTeamStatusEntry(): StimmzettelerfassungTeamStatusEntry {
    return {
      status: getRandomItem(Object.values(StimmzettelerfassungTeamStatusEnum)),
      teamID: generateRandomString(10),
    };
  }

  function createStimmzettelerfassungTeamStatusModel(
    status: StimmzettelerfassungTeamStatusEnum = StimmzettelerfassungTeamStatusEnum.REGISTRIERT
  ): StimmzettelerfassungTeamStatus {
    return { status };
  }

  function createStimmzettelerfassungTeamStatusListe(): StimmzettelerfassungTeamStatusEntry[] {
    return [
      {
        teamID: generateRandomString(1),
        status: getRandomItem(
          Object.values(StimmzettelerfassungTeamStatusDTOStatusEnum)
        ),
      },
      {
        teamID: generateRandomString(1),
        status: getRandomItem(
          Object.values(StimmzettelerfassungTeamStatusDTOStatusEnum)
        ),
      },
      {
        teamID: generateRandomString(1),
        status: getRandomItem(
          Object.values(StimmzettelerfassungTeamStatusDTOStatusEnum)
        ),
      },
    ];
  }

  function prepareStimmzettelerfassungTeamStatusDTO(): Builder<StimmzettelerfassungTeamStatusDTO> {
    return proxyBuilder<StimmzettelerfassungTeamStatusDTO>(
      createStimmzettelerfassungTeamStatusDTOData()
    );
  }

  function prepareStimmzettelerfassungTeamStatus(): Builder<StimmzettelerfassungTeamStatus> {
    return proxyBuilder<StimmzettelerfassungTeamStatus>(
      createStimmzettelerfassungTeamStatusModel()
    );
  }

  function prepareStimmzettelerfassungTeamStatusEntry(): Builder<StimmzettelerfassungTeamStatusEntry> {
    return proxyBuilder<StimmzettelerfassungTeamStatusEntry>(
      createStimmzettelerfassungTeamStatusEntry()
    );
  }

  return {
    createStimmzettelerfassungTeamStatusDTOData,
    createStimmzettelerfassungTeamStatusDtoEnumValue,
    createStimmzettelerfassungTeamStatusEntry,
    createStimmzettelerfassungTeamStatusModel,
    createStimmzettelerfassungTeamStatusListe,
    prepareStimmzettelerfassungTeamStatusDTO,
    prepareStimmzettelerfassungTeamStatus,
    prepareStimmzettelerfassungTeamStatusEntry,
  };
}
