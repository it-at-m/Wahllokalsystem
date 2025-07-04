import type { AxiosResponse } from "axios";

export function useCommonApiUtils() {
  function getNullOn204OrElseResponseData<T>(
    response: AxiosResponse<T>
  ): T | null {
    return response.status === 204 ? null : response.data;
  }

  return {
    getNullOn204OrElseResponseData,
  };
}
