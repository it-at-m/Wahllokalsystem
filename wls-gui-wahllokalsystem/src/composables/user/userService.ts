import type { User } from "@/types/User.ts";

import {
  Configuration,
  UserControllerApi,
} from "@/api/wls-clients/generated-auth-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useUserMapper } from "@/composables/user/userMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { AUTH_SERVICE_API_URL, CONTACT_SUPPORT } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { validateDtoAndMapToModel } = useUserMapper();
const { addNotification } = useUserNotificationService();
const { axiosConfigWrapper } = useCommonApiUtils();

export function useUserService() {
  const userControllerApi = new UserControllerApi(
    new Configuration({
      basePath: AUTH_SERVICE_API_URL,
    })
  );

  async function getUser(): Promise<User> {
    try {
      const response = await userControllerApi.user(
        axiosConfigWrapper().requestAsOnlineOnly()
      );
      return validateDtoAndMapToModel(response.data);
    } catch (e) {
      addNotification(
        "Fehler beim Laden des Users. " + CONTACT_SUPPORT,
        UserNotificationCategoryEnum.ERROR
      );
      throw e;
    }
  }

  return { getUser };
}
