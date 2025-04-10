import type { AxiosResponse } from "axios";

export function useApiUtils() {
  function returnUndefinedOnStatus204OrElseDate<T>(
    axiosResponse: AxiosResponse<T>
  ) {
    if (axiosResponse.status === 204) {
      return undefined;
    } else {
      return axiosResponse.data;
    }
  }

  return {
    returnUndefinedOnStatus204OrElseDate,
  };
}
