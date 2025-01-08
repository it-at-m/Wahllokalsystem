import type { BroadcastMessageToRead } from "@/api/wls-clients/broadcast-service/BroadcastMessageToRead";

import {
  broadcastMessageRead,
  getBroadcastMessage,
  postBroadcastMessage,
} from "@/api/wls-clients/broadcast-service/broadcast-client";

export function useBroadcastService() {
  async function getMessage(wahlbezirkID: string) {
    let messageId = "";

    try {
      const response = await getBroadcastMessage(wahlbezirkID);
      const content: BroadcastMessageToRead = await response.json();
      messageId = content.oid;

      await broadcastMessageRead(messageId).catch(() => {
        return {
          message: "",
          error: "Es ist ein Fehler beim Lesen der Nachricht aufgetreten",
        };
      });

      return {
        message: content.nachricht,
        error: "",
      };
    } catch (e) {
      return {
        message: "",
        error: (e as Error).message,
      };
    }
  }

  async function postMessage(message: string, wahlbezirkIDs: string[]) {
    try {
      await postBroadcastMessage(wahlbezirkIDs, message);
      return { error: "" };
    } catch (e) {
      return { error: (e as Error).message };
    }
  }

  return {
    getMessage,
    postMessage,
  };
}
