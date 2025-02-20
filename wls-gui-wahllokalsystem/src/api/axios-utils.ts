import type { AxiosRequestConfig } from "axios";

import { AxiosHeaders } from "axios";

export function basicGetConfig(
  url: string,
  swStrategy?: string
): AxiosRequestConfig {
  return {
    url: url,
    method: "get",
    // "mode: 'cors'" from fetch is not necessary, because its included in axios
    withCredentials: true, // equivalent to "credentials: 'include'" in fetch
    // "redirect: 'manual'" from fetch is not necessary, because its icluded in axios
    headers: getHeaders(swStrategy),
  };
}

export function basicPostConfig(
  url: string,
  swStrategy?: string,
  data?: object
): AxiosRequestConfig {
  return {
    url: url,
    method: "post",
    withCredentials: true,
    headers: getHeaders(swStrategy),
    data: data,
  };
}

function getHeaders(strategy = "OFFLINE_FIRST"): AxiosHeaders {
  const headers = new AxiosHeaders();
  headers.set("X-XSRF-TOKEN", getCSRFToken());
  headers.set("content-type", "application/json");
  headers.set("X-WLS-SW-STRATEGY", strategy);

  return headers;
}

function getCSRFToken() {
  const token = document.cookie.match("(^|;)\\s*XSRF-TOKEN\\s*=\\s*([^;]+)");
  return token ? token.pop() : "";
}
