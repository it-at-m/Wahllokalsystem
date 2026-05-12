import type { BroadcastMessage } from "@/types/broadcast/broadcastMessage.ts";

import {
  BroadcastControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-broadcast-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useBroadcastMapper } from "@/composables/broadcast/broadcastMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BROADCAST_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { dtoToModel } = useBroadcastMapper();
const { addNotification } = useUserNotificationService();
const { axiosConfigWrapper, getNullOn204OrElseResponseData } =
  useCommonApiUtils();

export function useBroadcastService() {
  const broadcastCA = new BroadcastControllerApi(
    new Configuration({
      basePath: BROADCAST_SERVICE_API_URL,
    })
  );

  async function getMessage(
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<BroadcastMessage | null> {
    try {
      const response = await broadcastCA.getMessage(
        wahlbezirkID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );

      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? dtoToModel(responseData) : null;
    } catch {
      if (sendNotification) {
        addNotification(
          "Abrufen der Broadcastnachricht ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      return null;
    }
  }

  async function deleteMessage(messageId: string) {
    try {
      await broadcastCA.deleteMessage(
        messageId,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
    } catch {
      addNotification(
        "Löschen der Broadcastnachricht ist fehlgeschlagen",
        UserNotificationCategoryEnum.ERROR
      );
    }
  }

  return {
    getMessage,
    deleteMessage,
  };
}
