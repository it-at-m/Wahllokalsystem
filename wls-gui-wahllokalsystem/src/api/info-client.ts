import { defaultResponseHandler, fetchConfig } from "@/api/fetch-utils";

export interface Info {
  application: Application;
}

export interface Application {
  name: string;
  version: string;
}

export function getInfo(): Promise<Info> {
  return fetch("actuator/info", fetchConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
