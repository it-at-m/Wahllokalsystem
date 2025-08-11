import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";
import type { RouteHandlerCallbackOptions } from "workbox-core";

import { HttpStatusCode } from "axios";

import { useLogging } from "@/composables/common/logging.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { HTTP_HEADER_CONTENT_TYPE } from "@/constants.ts";

const { getItemFromIDB, markAsClean, storeItem } = useIndexDB();
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
      `offlineFirstGetRequestHandler - GET request identified - uri: ${options.url}`
    );

    const dbKey = options.request.url;
    const storedData = await getItemFromIDB(dbKey);
    if (storedData) {
      return _createResponse(storedData);
    } else {
      return await _fetchAndStoreResponse(options, dbKey);
    }
  }

  async function onlineFirstGetRequestHandler(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    log(
      `onlineFirstGetRequestHandler - GET request identified - uri: ${options.url}`
    );

    const dbKey = options.request.url;
    return await _fetchAndStoreResponse(options, dbKey);
  }

  async function onlineFirstPostRequestHandler(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    log(
      `onlineFirstPostRequestHandler - POST request identified - uri: ${options.url}`
    );

    const dbKey = options.request.url;
    try {
      const response = await fetch(options.request.clone());
      logDebug(`response has status ${response.status}`);

      //TODO handling 302 -> ReLogin
      if (response.ok) {
        //Store successful response in case we got trouble (offline or failure)
        await markAsClean(dbKey);
        return response;
      } else {
        try {
          await _storeRequest(dbKey, options.request, true);
          return await _getStoredResponseOrNotFound(dbKey);
        } catch (error) {
          logError("Error fetching idb data:", error);
          return response; //TODO Maybe the internal server error response?
        }
      }
    } catch (error) {
      logError("Error fetching remote data:", error);
      await _storeRequest(dbKey, options.request, true);
      return _createResponseNotFound();
    }
  }

  function _createResponse(storedData: IndexDBValue) {
    const response = new Response(storedData.data, {
      status: storedData.httpStatus,
      statusText: "fetched from idb",
    });
    response.headers.set(HTTP_HEADER_CONTENT_TYPE, storedData.contentType);
    return response;
  }

  function _createResponseNotFound() {
    return new Response(null, { status: HttpStatusCode.NotFound });
  }

  function _createResponseResponseInternalServerError(errorText: string) {
    return new Response(
      JSON.stringify({
        error: errorText,
        status: 500,
      }),
      { status: 500 }
    );
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
        return _createResponseNotFound();
      }
    } catch (error) {
      logError("Fehler beim fetch", error);
      return _createResponseNotFound();
    }
  }

  async function _getStoredResponseOrNotFound(
    dbKey: string
  ): Promise<Response> {
    logDebug(`looking up stored response for ${dbKey}`);
    const storedData = await getItemFromIDB(dbKey);
    if (storedData) {
      log("fetched from idb: " + JSON.stringify(storedData));
      return _createResponse(storedData);
    } else {
      return _createResponseNotFound();
    }
  }

  async function _getStoredResponseOrInternalServerError(
    dbKey: string
  ): Promise<Response> {
    const storedData = await getItemFromIDB(dbKey);
    if (storedData) {
      log("fetched from idb: " + JSON.stringify(storedData));
      return _createResponse(storedData);
    } else {
      return _createResponseResponseInternalServerError("no data found in idb");
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
      logError("error stsoring idb data", error);
    }
  }

  return {
    unhandledFetch,
    offlineFirstGetRequestHandler,
    onlineFirstGetRequestHandler,
    onlineFirstPostRequestHandler,
  };
}
