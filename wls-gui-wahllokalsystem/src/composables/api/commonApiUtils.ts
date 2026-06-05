import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";
import type { AxiosResponse } from "axios";

import { HttpStatusCode } from "axios";

import { HTTP_HEADER_CONTENT_TYPE } from "@/constants.ts";
import { AxiosConfigWrapper } from "@/types/api/AxiosConfigWrapper.ts";

export function useCommonApiUtils() {
  function getNullOn204OrElseResponseData<T>(
    response: AxiosResponse<T>
  ): T | null {
    return response.status === 204 ? null : response.data;
  }

  function createResponseOfIndexDBValue(
    storedData: IndexDBValue,
    httpStatusCodeWhenMissingInStoredValue = HttpStatusCode.Ok
  ) {
    const response = new Response(storedData.data, {
      status: storedData.httpStatus ?? httpStatusCodeWhenMissingInStoredValue,
      statusText: "fetched from idb",
    });
    if (storedData.contentType) {
      response.headers.set(HTTP_HEADER_CONTENT_TYPE, storedData.contentType);
    }
    return response;
  }

  function createResponseInternalServerErrorWithoutResponseBody() {
    return new Response(null, { status: HttpStatusCode.InternalServerError });
  }

  function createResponseOkWithoutResponseBody() {
    return new Response(null, { status: HttpStatusCode.Ok });
  }

  function createResponseNotFoundWithoutResponseBody() {
    return new Response(null, { status: HttpStatusCode.NotFound });
  }

  function isTextContext(contentType: string): boolean {
    return (
      contentType.includes("application/json") || contentType.includes("text/")
    );
  }

  function isPdfContext(contentType: string): boolean {
    return contentType.includes("application/pdf");
  }

  return {
    isTextContext,
    isPdfContext,
    createResponseInternalServerErrorWithoutResponseBody,
    createResponseOfIndexDBValue,
    createResponseOkWithoutResponseBody,
    createResponseNotFoundWithoutResponseBody,
    getNullOn204OrElseResponseData,
    axiosConfigWrapper: () => new AxiosConfigWrapper(),
  };
}
