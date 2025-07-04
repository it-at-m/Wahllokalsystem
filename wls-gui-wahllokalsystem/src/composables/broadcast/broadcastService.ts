import type { BroadcastMessage } from "@/types/broadcast/broadcastMessage.ts";

import {
  BroadcastControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-broadcast-api";
import { useBroadcastMapper } from "@/composables/broadcast/broadcastMapper.ts";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BROADCAST_SERVICE_API_URL } from "@/constants.ts";

const { dtoToModel } = useBroadcastMapper();
const { addNotification } = useUserNotificationService();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useBroadcastService() {
  const broadcastCA = new BroadcastControllerApi(
    new Configuration({
      basePath: BROADCAST_SERVICE_API_URL,
    })
  );

  async function getMessage(
    wahlbezirkID: string
  ): Promise<BroadcastMessage | null> {
    try {
      const response = await broadcastCA.getMessage(wahlbezirkID);

      const responseData = getNullOn204OrElseResponseData(response);
      return responseData !== null ? dtoToModel(responseData) : null;
    } catch {
      addNotification(
        "Abrufen der Broadcastnachricht ist fehlgeschlagen",
        "Error"
      );
      return null;
    }
  }

  async function deleteMessage(messageId: string) {
    try {
      await broadcastCA.deleteMessage(messageId);
    } catch {
      addNotification(
        "Löschen der Broadcastnachricht ist fehlgeschlagen",
        "Error"
      );
    }
  }

  return {
    getMessage,
    deleteMessage,
  };
}
