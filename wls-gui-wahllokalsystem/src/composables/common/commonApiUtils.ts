import type { AxiosResponse } from "axios";

export function useCommonApiUtils() {
  function getNullOn204OrElseResponseData<dataType>(
    response: AxiosResponse<dataType>
  ): dataType | null {
    return response.status === 204 ? null : response.data;
  }

  return {
    getNullOn204OrElseResponseData,
  };
}
