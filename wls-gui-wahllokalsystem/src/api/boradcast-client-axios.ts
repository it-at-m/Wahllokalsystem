import axios from "axios";

import { catchHandler, responseHandler } from "@/api/axios-utils";
import { wlsCatchHandler } from "@/api/fetch-utils";
import BroadcastMessageSendAxios from "@/types/BroadcastMessageToSend";

export const BROADCAST_API_URL = "/api/broadcast-service/businessActions/";

export async function getBroadcastMessageAxios(wahlbezirkID: string) {
  const url = `${BROADCAST_API_URL}getMessage/${wahlbezirkID}`;
  return await axios.get(url).then(responseHandler).catch(wlsCatchHandler);
}

export async function broadcastMessageReadAxios(nachrichtID: string) {
  const url = `${BROADCAST_API_URL}messageRead/${nachrichtID}`;
  return axios.post(url).then(responseHandler).catch(wlsCatchHandler);
}

export async function postBroadcastMessageAxios(
  wahlbezirkIDs: string[],
  nachricht: string
) {
  const url = `${BROADCAST_API_URL}broadcast`;
  return axios
    .post(url, new BroadcastMessageSendAxios(wahlbezirkIDs, nachricht))
    .then(responseHandler)
    .catch(catchHandler);
}
