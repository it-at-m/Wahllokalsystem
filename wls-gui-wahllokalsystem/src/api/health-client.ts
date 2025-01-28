import { defaultResponseHandler, fetchConfig } from "@/api/fetch-utils";
import HealthState from "@/types/HealthState";

export function checkHealth(): Promise<HealthState> {
  return fetch("actuator/health", fetchConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
