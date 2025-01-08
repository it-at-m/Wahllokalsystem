import type { BroadcastMessageToRead } from "@/api/wls-clients/broadcast-service/BroadcastMessageToRead";

import { ref } from "vue";

import {
  broadcastMessageRead,
  getBroadcastMessage,
  postBroadcastMessage,
} from "@/api/wls-clients/broadcast-service/broadcast-client";

export function useBroadcastMessage() {
  const message = ref("");
  const messageInput = ref("Broadcast Message");
  const errors = ref({ get: "", post: "", read: "" });
  let messageId = "";

  async function getMessage(wahlbezirkID: string) {
    errors.value.get = "";
    message.value = "";
    try {
      const response = await getBroadcastMessage(wahlbezirkID);
      const content: BroadcastMessageToRead = await response.json();
      message.value = content.nachricht;
      messageId = content.oid;

      await broadcastMessageRead(messageId).catch(() => {
        errors.value.read =
          "Es ist ein Fehler beim Lesen der Nachricht aufgetreten";
      });
    } catch (e) {
      errors.value.get = (e as Error).message;
    }
  }

  async function postMessage(wahlbezirkIDs: string[]) {
    try {
      errors.value.post = "";
      await postBroadcastMessage(wahlbezirkIDs, messageInput.value);
      messageInput.value = "";
    } catch (e) {
      errors.value.post = (e as Error).message;
    }
  }

  return {
    message,
    messageInput,
    errors,
    getMessage,
    postMessage,
  };
}
