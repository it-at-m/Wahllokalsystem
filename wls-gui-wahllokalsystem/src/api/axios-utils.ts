import type WLSException from "@/types/WLSException";
import type { AxiosError, AxiosResponse } from "axios";

import { ApiError } from "@/api/ApiError";
import { STATUS_INDICATORS } from "@/constants";

export function responseHandler(response: AxiosResponse) {
  if (response.status === 204 || response.status >= 300) {
    return Promise.reject(response);
  } else {
    return Promise.resolve(response.data);
  }
}

export function catchHandler(error: AxiosError) {
  if (error.response) {
    if (error.status === 204) {
      throw new ApiError({
        level: STATUS_INDICATORS.INFO,
        message: "Es konnten keine Daten gefunden werden",
      });
    }
    if (error.status === 400) {
      const wlsException = error.response.data as WLSException;
      return Promise.reject(
        new ApiError({
          level: STATUS_INDICATORS.ERROR,
          message: wlsException.message,
        })
      );
    } else {
      return Promise.reject(
        new ApiError({
          level: STATUS_INDICATORS.ERROR,
          message: "unbekannter fehler", // zb wenn broadcast nicht gestartet ist
        })
      );
    }
  }
}
