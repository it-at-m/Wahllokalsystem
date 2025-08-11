import type { AxiosResponse } from "axios";

import { AxiosConfigWrapper } from "@/types/api/AxiosConfigWrapper.ts";

export function useCommonApiUtils() {
  function getNullOn204OrElseResponseData<T>(
    response: AxiosResponse<T>
  ): T | null {
    return response.status === 204 ? null : response.data;
  }

  return {
    getNullOn204OrElseResponseData,
    axiosConfigWrapper: () => new AxiosConfigWrapper(),
  };
}
