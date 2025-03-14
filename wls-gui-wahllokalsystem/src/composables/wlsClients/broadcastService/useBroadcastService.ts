import type { BroadcastMessageDTO } from "@/api/wls-clients/generated-broadcast-api";

import axios from "axios";

import {
  BroadcastControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-broadcast-api";
import { WLSError } from "@/api/WLSError";
import { BROADCAST_SERVICE_API_URL } from "@/constants";

export function useBroadcastService() {
  const broadcastCA = new BroadcastControllerApi(
    new Configuration({
      basePath: BROADCAST_SERVICE_API_URL,
    })
  );

  async function getMessage(wahlbezirkID: string) {
    try {
      const response = await broadcastCA.getMessage(wahlbezirkID);
      if (response.status == 204) {
        return {
          message: "",
          error: "Es konnten keine Daten gefunden werden",
        };
      }
      const messageDTO = response.data;
      const nachrichtID = messageDTO.oid;

      try {
        await broadcastCA.deleteMessage(nachrichtID);
      } catch {
        return {
          message: messageDTO.nachricht, // eg if message fetched from idb
          error: "Es ist ein Fehler beim Lesen der Nachricht aufgetreten",
        };
      }

      return {
        message: messageDTO.nachricht,
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

    try {
      await broadcastCA.broadcast(broadcastMessageDTO);
      return { error: "" };
    } catch (e) {
      if (axios.isAxiosError(e)) {
        if (e.response) {
          const error: WLSError = e.response.data;
          const errorMessage =
            error.service +
            " - " +
            error.message +
            " (Code: " +
            error.code +
            ")";
          return { error: errorMessage };
        } else {
          return { error: "Fehler beim Senden der Broadcast Nachricht" };
        }
      } else {
        return { error: "Fehler beim Senden der Broadcast Nachricht" };
      }
    }
  }

  return {
    getMessage,
    postMessage,
  };
}
