import {
  catchHandler,
  getConfig,
  postConfig,
  responseHandler,
} from "@/api/fetch-utils";
import BroadcastMessageToSend from "@/types/BroadcastMessageToSend";

export const BROADCAST_API_URL = "/api/broadcast-service/businessActions/";

export function getBroadcastMessage(wahlbezirkID: string): Promise<Response> {
  return fetch(
    BROADCAST_API_URL + "getMessage/" + wahlbezirkID,
    getConfig()
  ).then(responseHandler);
}

export function postBroadcastMessage(
  wahlbezirkIDs: string[],
  message: string
): Promise<void> {
  return fetch(
    BROADCAST_API_URL + "broadcast",
    postConfig(new BroadcastMessageToSend(wahlbezirkIDs, message))
  )
    .then(responseHandler)
    .catch(catchHandler);
}

export function broadcastMessageRead(nachrichtID: string): Promise<void> {
  return fetch(
    BROADCAST_API_URL + "messageRead/" + nachrichtID,
    postConfig(nachrichtID)
  )
    .then(responseHandler)
    .catch(catchHandler);
}
