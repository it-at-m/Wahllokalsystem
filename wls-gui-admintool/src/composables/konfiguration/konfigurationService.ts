import type {
  KonfigurationDTO,
  PostKonfigurationKeyEnum,
} from "@/api/wls-clients/generated-infomanagement-api";
import type { InfomanagementConfigParameter } from "@/types/config/InfomanagementConfigParameter.ts";
import type { Ref } from "vue";

import {
  Configuration,
  KonfigurationControllerApi,
} from "@/api/wls-clients/generated-infomanagement-api";
import { useApiUtils } from "@/composables/common/apiUtils.ts";
import { useKonfigurationMapper } from "@/composables/konfiguration/konfigurationMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { INFOMANAGEMENT_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useKonfigurationService() {
  const konfigurationAPI = new KonfigurationControllerApi(
    new Configuration({
      basePath: INFOMANAGEMENT_SERVICE_API_URL,
    })
  );
  const { returnUndefinedOnStatus204OrElseResponseData } = useApiUtils();
  const {
    mapKonfigurationDtosToConfigParameters,
    mapConfigParameterToKonfigurationSetDto,
  } = useKonfigurationMapper();
  const { addNotification } = useUserNotificationService();

  async function getKonfigurations(
    isLoading?: Ref<boolean>
  ): Promise<InfomanagementConfigParameter[]> {
    updateLoading(true, isLoading);

    const result: InfomanagementConfigParameter[] = [];
    try {
      const konfigurationDtos = await konfigurationAPI
        .getKonfigurations()
        .then(
          (response) =>
            returnUndefinedOnStatus204OrElseResponseData(response) as
              | KonfigurationDTO[]
              | undefined
        );

      if (konfigurationDtos) {
        result.push(
          ...mapKonfigurationDtosToConfigParameters(konfigurationDtos)
        );
      }
    } catch {
      addNotification(
        "Konfigurationsparameter konnten nicht geladen werden",
        UserNotificationCategoryEnum.ERROR
      );
    }

    updateLoading(false, isLoading);

    return result;
  }

  async function saveKonfiguration(
    configParameter: InfomanagementConfigParameter
  ): Promise<boolean> {
    try {
      await konfigurationAPI.postKonfiguration(
        configParameter.name as PostKonfigurationKeyEnum,
        mapConfigParameterToKonfigurationSetDto(configParameter)
      );

      addNotification(
        `Konfigurationsparameter '${configParameter.name}' wurde gespeichert`,
        UserNotificationCategoryEnum.SUCCESS
      );

      return true;
    } catch {
      addNotification(
        `Konfigurationsparameter '${configParameter.name}' konnte nicht gespeichert werden`,
        UserNotificationCategoryEnum.ERROR
      );

      return false;
    }
  }

  return {
    getKonfigurations,
    saveKonfiguration,
  };
}

function updateLoading(loadingState: boolean, loadingRef?: Ref<boolean>) {
  if (loadingRef) {
    loadingRef.value = loadingState;
  }
}
