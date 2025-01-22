import type {
  BroadcastMessageDTO,
  BroadcastRequest,
  DeleteMessageRequest,
  GetMessageRequest,
} from "@/api/wls-clients/generated-broadcast-api";

import { postConfig } from "@/api/fetch-utils";
import {
  BroadcastControllerApi,
  Configuration,
  WLSError,
} from "@/api/wls-clients/generated-broadcast-api";
import { BROADCAST_SERVICE_API_URL } from "@/constants";

export function useBroadcastService() {
  const broadcastCA = new BroadcastControllerApi(
    new Configuration({
      basePath: BROADCAST_SERVICE_API_URL,
    })
  );

  async function getMessage(wahlbezirkID: string) {
    const getParams: GetMessageRequest = { wahlbezirkID };

    try {
      const response = await broadcastCA.getMessage(getParams);

      const nachrichtID = response.oid;
      const deleteParams: DeleteMessageRequest = { nachrichtID };

      await broadcastCA.deleteMessage(deleteParams, postConfig()).catch(() => {
        return {
          message: "",
          error: "Es ist ein Fehler beim Lesen der Nachricht aufgetreten",
        };
      });

      return {
        message: response.nachricht,
        error: "",
      };
    } catch (e) {
      return {
        message: "",
        error: (e as Error).message,
      };
    }
  }

  async function postMessage(nachricht: string, wahlbezirkIDs: string[]) {
    const broadcastMessageDTO = {
      wahlbezirkIDs,
      nachricht,
    } as BroadcastMessageDTO;
    const postParams: BroadcastRequest = { broadcastMessageDTO };

    try {
      await broadcastCA.broadcast(postParams, postConfig());
      return { error: "" };
    } catch (e) {
      const error = e as WLSError;
      const errorMessage =
        error.service + " - " + error.message + " (Code: " + error.code + ")";
      return { error: errorMessage };
    }
  }

  return {
    getMessage,
    postMessage,
  };
}
