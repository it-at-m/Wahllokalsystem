/**
 * A composable for managing broadcast messages in a Vue component.
 *
 * Provides reactive state and methods to fetch and post messages for a given electoral district.
 *
 * @returns {Ref<string>} message - The retrieved broadcast message.
 * @returns {Ref<string>} messageInput - The input message to be posted.
 * @returns {Ref<Object>} errors - Object containing error messages for each api call.
 *
 * @returns {Function} getMessage - Fetches the broadcast message for an electoral district.
 * @param {string} wahlbezirkID - The electoral district ID.
 *
 * @returns {Function} postMessage - Posts the input message to specified electoral district IDs.
 * @param {Array<string>} wahlbezirkIDs - The electoral district IDs for posting.
 */

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
