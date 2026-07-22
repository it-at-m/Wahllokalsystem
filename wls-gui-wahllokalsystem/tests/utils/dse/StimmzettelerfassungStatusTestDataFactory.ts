import type { StimmzettelerfassungStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungStatus.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { StimmzettelerfassungStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

const { getRandomItem } = useCommonTestDataFactory();

export function useStimmzettelerfassungStatusTestDataFactory() {
  function createStimmzettelerfassungStatusDTO(): StimmzettelerfassungStatusDTO {
    return {
      status: getRandomItem(
        Object.values(StimmzettelerfassungStatusDTOStatusEnum)
      ),
    };
  }

  function createStimmzettelerfassungStatus(): StimmzettelerfassungStatus {
    return {
      status: getRandomItem(Object.values(StimmzettelerfassungStatusEnum)),
    };
  }

  function prepareStimmzettelerfassungStatusDTO(): Builder<StimmzettelerfassungStatusDTO> {
    return proxyBuilder<StimmzettelerfassungStatusDTO>(
      createStimmzettelerfassungStatusDTO()
    );
  }

  function prepareStimmzettelerfassungStatus(): Builder<StimmzettelerfassungStatus> {
    return proxyBuilder<StimmzettelerfassungStatus>(
      createStimmzettelerfassungStatus()
    );
  }

  return {
    createStimmzettelerfassungStatusDTO,
    createStimmzettelerfassungStatus,
    prepareStimmzettelerfassungStatusDTO,
    prepareStimmzettelerfassungStatus,
  };
}
