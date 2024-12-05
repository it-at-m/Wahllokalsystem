import {
  defaultCatchHandler,
  defaultResponseHandler,
  getConfig,
  postConfig,
} from "@/api/fetch-utils";
import BroadcastMessageToRead from "@/types/BroadcastMessageToRead";
import BroadcastMessageToSend from "@/types/BroadcastMessageToSend";

export const BROADCAST_API_URL = "/api/broadcast-service/businessActions/";

export function getBroadcastMessage(
  wahlbezirkID: string
): Promise<BroadcastMessageToRead> {
  return fetch(BROADCAST_API_URL + "getMessage/" + wahlbezirkID, getConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch(defaultCatchHandler);
}

export function postBroadcastMessage(
  wahlbezirkIDs: string[],
  message: string
): Promise<void> {
  return fetch(
    BROADCAST_API_URL + "broadcast",
    postConfig(new BroadcastMessageToSend(wahlbezirkIDs, message))
  )
    .then((response) => {
      defaultResponseHandler(response);
    })
    .catch(defaultCatchHandler);
}

export function broadcastMessageRead(nachrichtID: string) {
  return fetch(
    BROADCAST_API_URL + "messageRead/" + nachrichtID,
    postConfig(nachrichtID)
  )
    .then((response) => {
      defaultResponseHandler(response);
      console.log("message read ausgeführt");
      return response.json();
    })
    .catch(defaultCatchHandler);
}
