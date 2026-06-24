import type {
  KonfigurationDTO,
  KonfigurationSetDTO,
} from "@/api/wls-clients/generated-infomanagement-api";
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { Builder } from "@tests/utils/common/Builder.ts";

import { proxyBuilder } from "@tests/utils/common/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString } = useCommonTestDataFactory();

export function useKonfigurationTestDataFactory() {
  function createKonfigurationDtoComplete(): KonfigurationDTO {
    return {
      schluessel: `WILLKOMMENSTEXT_${generateRandomString(5)}`,
      wert: `wert_${generateRandomString(5)}`,
      beschreibung: `beschreibung_${generateRandomString(5)}`,
      standardwert: `standardwert_${generateRandomString(5)}`,
    };
  }

  function createConfigParameterComplete(): InfomanagementConfigParameter {
    return {
      name: `WILLKOMMENSTEXT_${generateRandomString(5)}`,
      wert: `wert_${generateRandomString(5)}`,
      beschreibung: `beschreibung_${generateRandomString(5)}`,
      defaultValue: `standardwert_${generateRandomString(5)}`,
    };
  }

  function prepareKonfigurationDto(): Builder<KonfigurationDTO> {
    return proxyBuilder<KonfigurationDTO>(createKonfigurationDtoComplete());
  }

  function prepareKonfigurationSetDto(): Builder<KonfigurationSetDTO> {
    return proxyBuilder<KonfigurationSetDTO>({
      wert: `wert_${generateRandomString(5)}`,
      beschreibung: `beschreibung_${generateRandomString(5)}`,
      standardwert: `standardwert_${generateRandomString(5)}`,
    });
  }

  function prepareConfigParameter(): Builder<InfomanagementConfigParameter> {
    return proxyBuilder<InfomanagementConfigParameter>(
      createConfigParameterComplete()
    );
  }

  return {
    createKonfigurationDtoComplete,
    createConfigParameterComplete,
    prepareKonfigurationDto,
    prepareKonfigurationSetDto,
    prepareConfigParameter,
  };
}
