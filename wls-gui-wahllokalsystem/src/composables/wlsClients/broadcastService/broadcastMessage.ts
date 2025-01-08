import type { BroadcastMessageToRead } from "@/api/wls-clients/broadcast-service/BroadcastMessageToRead";

import { ref } from "vue";

import {
  broadcastMessageRead,
  getBroadcastMessage,
  postBroadcastMessage,
} from "@/api/wls-clients/broadcast-service/broadcast-client";

export function useBroadcastService() {
  const messageInput = ref("Broadcast Message");

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

  async function postMessage(wahlbezirkIDs: string[]) {
    try {
      await postBroadcastMessage(wahlbezirkIDs, messageInput.value);
      messageInput.value = "";
      return { error: "" };
    } catch (e) {
      return { error: (e as Error).message };
    }
  }

  return {
    messageInput,
    getMessage,
    postMessage,
  };
}
