import {
  getConfig,
  postConfig,
  wlsCatchHandler,
  wlsResponseHandler,
} from "@/api/fetch-utils";
import BroadcastMessageToSend from "@/types/BroadcastMessageToSend";

export const BROADCAST_API_URL = "/api/broadcast-service/businessActions/";

export function getBroadcastMessage(wahlbezirkID: string): Promise<Response> {
  return fetch(
    BROADCAST_API_URL + "getMessage/" + wahlbezirkID,
    getConfig()
  ).then(wlsResponseHandler);
}

export function postBroadcastMessage(
  wahlbezirkIDs: string[],
  message: string
): Promise<Response> {
  return fetch(
    BROADCAST_API_URL + "broadcast",
    postConfig(new BroadcastMessageToSend(wahlbezirkIDs, message))
  )
    .then(wlsResponseHandler)
    .catch(wlsCatchHandler);
}

export function broadcastMessageRead(nachrichtID: string): Promise<Response> {
  return fetch(
    BROADCAST_API_URL + "messageRead/" + nachrichtID,
    postConfig(nachrichtID)
  )
    .then(wlsResponseHandler)
    .catch(wlsCatchHandler);
}
