import type { BroadcastMessage } from "@/types/broadcast/broadcastMessage.ts";

import {
  BroadcastControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-broadcast-api";
import { useBroadcastMapper } from "@/composables/broadcast/broadcastMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BROADCAST_SERVICE_API_URL } from "@/constants.ts";

const { dtoToModel } = useBroadcastMapper();
const { addNotification } = useUserNotificationService();

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

      if (response.status === 200) {
        return dtoToModel(response.data);
      } else {
        return null;
      }
    } catch (e) {
      addNotification(
        "Abrufen der Broadcastnachricht ist fehlgeschlagen",
        "Error"
      );
      return null;
    }
  }

  async function deleteMessage(messageId: string) {
    await broadcastCA.deleteMessage(messageId);
  }

  return {
    getMessage,
    deleteMessage,
    postMessage,
  };
}
