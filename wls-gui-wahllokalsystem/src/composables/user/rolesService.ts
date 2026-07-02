import type { RoleMapping } from "@/types/user/RoleMapping.ts";

import {
  Configuration,
  RolesControllerApi,
} from "@/api/wls-clients/generated-auth-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useRolesMapper } from "@/composables/user/rolesMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { AUTH_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useRolesService() {
  const { addNotification } = useUserNotificationService();
  const { axiosConfigWrapper } = useCommonApiUtils();
  const { toModel } = useRolesMapper();

  const rolesAPIController = new RolesControllerApi(
    new Configuration({
      basePath: AUTH_SERVICE_API_URL,
    })
  );

  async function getRoles() {
    try {
      const response = await rolesAPIController.getRoleMappings(
        axiosConfigWrapper().requestAsOnlineOnly()
      );
      return toModel(response.data);
    } catch (error) {
      addNotification(
        "Fehler beim Laden des Rollenmappings.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  function createEmptyMapping(): RoleMapping {
    return {
      schriftfuehrung: "",
      erfassungsteam: "",
      admin: "",
    };
  }

  return {
    createEmptyMapping,
    getRoles,
  };
}
