import type { BroadcastMessageDTO } from "@/api/wls-clients/generated-broadcast-api";

import {
  getConfig,
  postConfig,
  wlsCatchHandler,
  wlsResponseHandler,
} from "@/api/fetch-utils";

export const BROADCAST_API_URL = new URL(
  "/api/broadcast-service/businessActions/",
  window.location.origin
).toString();

export function getBroadcastMessage(wahlbezirkID: string): Promise<Response> {
  return fetch(`${BROADCAST_API_URL}getMessage/` + wahlbezirkID, getConfig())
    .then(wlsResponseHandler)
    .catch(wlsCatchHandler);
}

export function postBroadcastMessage(
  wahlbezirkIDs: string[],
  nachricht: string
): Promise<Response> {
  return fetch(
    `${BROADCAST_API_URL}broadcast`,
    postConfig({ wahlbezirkIDs, nachricht } as BroadcastMessageDTO)
  )
    .then(wlsResponseHandler)
    .catch(wlsCatchHandler);
}

export function broadcastMessageRead(nachrichtID: string): Promise<Response> {
  return fetch(
    `${BROADCAST_API_URL}messageRead/` + nachrichtID,
    postConfig(nachrichtID)
  )
    .then(wlsResponseHandler)
    .catch(wlsCatchHandler);
}
