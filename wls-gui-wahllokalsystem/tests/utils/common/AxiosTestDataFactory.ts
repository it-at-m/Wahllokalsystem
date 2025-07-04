import type { AxiosResponse } from "axios";

import { AxiosHeaders } from "axios";

export function useAxiosTestDataFactory() {
  function createAxiosResponse(response: {
    status: number;
    data?: unknown;
  }): AxiosResponse {
    return {
      config: {
        headers: new AxiosHeaders(),
      },
      data: response.data,
      headers: {},
      statusText: "",
      status: response.status,
    };
  }

  return {
    createAxiosResponse,
  };
}
