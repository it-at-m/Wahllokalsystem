import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";
import type { RouteHandlerCallbackOptions } from "workbox-core";

import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { HTTP_HEADER_CONTENT_TYPE } from "@/constants.ts";

const { getItemFromIDB, storeItem } = useIndexDB();
const {
  createResponseOkWithoutResponseBody,
  createResponseNotFoundWithoutResponseBody,
  createResponseOfIndexDBValue,
} = useCommonApiUtils();
const { log, logDebug, logError } = useLogging("requestStrategies");

export function useRequestStrategies() {
  async function unhandledFetch(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    logDebug(`using unhandledFetch`);
    return await fetch(options.request);
  }

  async function offlineFirstGetRequestHandler(
    options: RouteHandlerCallbackOptions
  ) {
    log(
      `offlineFirstGetRequestHandler - ${options.request.method} request identified - uri: ${options.url}`
    );

    const dbKey = options.request.url;
    const storedData = await getItemFromIDB(dbKey);
    if (storedData) {
      return createResponseOfIndexDBValue(storedData);
    } else {
      return await _fetchAndStoreResponse(options, dbKey);
    }
  }

  async function onlineFirstGetRequestHandler(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    log(
      `onlineFirstGetRequestHandler - ${options.request.method} request identified - uri: ${options.url}`
    );

    const dbKey = options.request.url;
    return await _fetchAndStoreResponse(options, dbKey);
  }

  function postRequestHandler(options: RouteHandlerCallbackOptions) {
    return _onlineFirstPostRequestHandler(options);
  }

  async function _fetchAndStoreResponse(
    options: RouteHandlerCallbackOptions,
    dbKey: string
  ) {
    try {
      const fetchedResponse = await fetch(options.request);
      //TODO handling 302 -> ReLogin
      if (fetchedResponse.ok) {
        await _storeResponse(fetchedResponse, dbKey);
        return fetchedResponse;
      } else {
        return createResponseNotFoundWithoutResponseBody();
      }
    } catch (error) {
      logError("error while fetching", error);
      return createResponseNotFoundWithoutResponseBody();
    }
  }

  async function _onlineFirstPostRequestHandler(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    log(
      `onlineFirstPostRequestHandler - ${options.request.method} request identified - uri: ${options.url}`
    );

    const dbKey = options.request.url;
    try {
      const response = await fetch(options.request.clone());
      logDebug(`response has status ${response.status}`);

      //TODO handling 302 -> ReLogin
      if (response.ok) {
        await _storeRequest(dbKey, options.request, false);
        return response;
      } else {
        await _storeRequest(dbKey, options.request, true);
        return createResponseOkWithoutResponseBody();
      }
    } catch (error) {
      logError("Error fetching remote data:", error);
      await _storeRequest(dbKey, options.request, true);
      return createResponseNotFoundWithoutResponseBody();
    }
  }

  async function _storeRequest(
    dbKey: string,
    request: Request,
    dirty: boolean | undefined = undefined
  ) {
    const clonedRequest = request.clone();
    const requestToStore: IndexDBValue = {
      data: await clonedRequest.text(),
      contentType: clonedRequest.headers.get(HTTP_HEADER_CONTENT_TYPE) ?? "",
      dirty: dirty,
    };
    await storeItem(dbKey, requestToStore);
  }

  async function _storeResponse(response: Response, dbKey: string) {
    const clonedResponse = response.clone();
    const responseToStore: IndexDBValue = {
      data: await clonedResponse.text(),
      contentType: clonedResponse.headers.get(HTTP_HEADER_CONTENT_TYPE) ?? "",
      httpStatus: clonedResponse.status,
    };
    try {
      logDebug(`storing response with key ${dbKey}`, responseToStore);
      await storeItem(dbKey, responseToStore);
    } catch (error) {
      logError("error storing idb data", error);
    }
  }

  return {
    unhandledFetch,
    offlineFirstGetRequestHandler,
    onlineFirstGetRequestHandler,
    postRequestHandler,
  };
}
