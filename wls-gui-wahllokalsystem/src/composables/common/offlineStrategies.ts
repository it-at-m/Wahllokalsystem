import type { RouteHandlerCallbackOptions } from "workbox-core";
import type { RouteHandlerCallback } from "workbox-core/src/types.ts";
import type { HTTPMethod } from "workbox-routing/utils/constants";

import { HttpStatusCode } from "axios";
import { defaultMethod, validMethods } from "workbox-routing/utils/constants";

import { useLogging } from "@/composables/common/logging.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import {
  HTTP_HEADER_CONTENT_TYPE,
  REQUEST_HEADER_OFFLINE_STRATEGY,
} from "@/constants.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

interface StoredResponse {
  data: string;
  contentType: string;
  httpStatus?: number;
  dirty?: boolean;
}

const { getItemFromIDB, storeItem } = useIndexDB();
const { log, logError } = useLogging("offlineStrategies");

export function useOfflineStrategies() {
  const DEFAULT_OFFLINE_STRATEGY = FetchStrategiesEnum.STRATEGY_ONLINE_ONLY;

  const offlineStrategiesHandlers: Record<
    FetchStrategiesEnum,
    Map<HTTPMethod, RouteHandlerCallback>
  > = {
    STRATEGY_OFFLINE_FIRST: new Map([["GET", _offlineFirstGetRequestHandler]]),
    STRATEGY_ONLINE_FIRST: new Map([
      ["GET", _onlineFirstGetRequestHandler],
      ["POST", _onlinePostFirstRequestHandler],
    ]),
    STRATEGY_ONLINE_ONLY: new Map([]),
  };

  function findStrategy(request: Request): FetchStrategiesEnum {
    const offlineStrategyAsString = request.headers.get(
      REQUEST_HEADER_OFFLINE_STRATEGY
    );
    if (!offlineStrategyAsString) {
      return DEFAULT_OFFLINE_STRATEGY;
    }

    const fetchStrategy = Object.values(FetchStrategiesEnum).find(
      (value) => value == offlineStrategyAsString
    );

    return fetchStrategy ?? DEFAULT_OFFLINE_STRATEGY;
  }

  async function handleRouteWithStrategy(
    options: RouteHandlerCallbackOptions,
    fetchStrategy: FetchStrategiesEnum
  ): Promise<Response> {
    const httpMethod =
      validMethods.find((method) => options.request.method === method) ??
      defaultMethod;
    const handler =
      offlineStrategiesHandlers[fetchStrategy].get(httpMethod) ??
      _unhandledFetch;
    return handler(options);
  }

  async function _unhandledFetch(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    return await fetch(options.request);
  }

  async function _offlineFirstGetRequestHandler(
    options: RouteHandlerCallbackOptions
  ) {
    log(`GET request identified - uri: ${options.url}`);

    const dbKey = options.request.url;
    const storedData = await getItemFromIDB<StoredResponse | null>(dbKey);
    if (storedData) {
      return _createResponse(storedData);
    } else {
      const fetchedResponse = await fetch(options.request);
      //TODO handling 302 -> ReLogin
      if (fetchedResponse.ok) {
        await _storeResponse(fetchedResponse, dbKey);
        return fetchedResponse;
      } else {
        return _createResponseNotFound();
      }
    }
  }

  async function _onlineFirstGetRequestHandler(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    log(`GET request identified - uri: ${options.url}`);

    const dbKey = options.request.url;
    try {
      const response = await fetch(options.request);

      //TODO handling 302 -> ReLogin
      if (response.ok) {
        //Store successful response in case we got trouble (offline or failure)
        await _storeResponse(response, dbKey);
        return response;
      } else {
        try {
          return await _getStoredResponseOrNotFound(dbKey);
        } catch (error) {
          logError("Error fetching idb data:", error);
          return response; //TODO Maybe the internal server error response?
        }
      }
    } catch (error) {
      logError("Error fetching remote data:", error);
      return _getStoredResponseOrInternalServerError(dbKey);
    }
  }

  async function _onlinePostFirstRequestHandler(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    log(`POST request identified - uri: ${options.url}`);

    const dbKey = options.request.url;
    try {
      const response = await fetch(options.request);

      //TODO handling 302 -> ReLogin
      if (response.ok) {
        //Store successful response in case we got trouble (offline or failure)
        await _storeResponse(response, dbKey);
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

  function _createResponse(storedData: StoredResponse) {
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

  async function _getStoredResponseOrNotFound(
    dbKey: string
  ): Promise<Response> {
    const storedData = await getItemFromIDB<StoredResponse | null>(dbKey);
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
    const storedData = await getItemFromIDB<StoredResponse>(dbKey);
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
    const requestToStore: StoredResponse = {
      data: await clonedRequest.text(),
      contentType: clonedRequest.headers.get(HTTP_HEADER_CONTENT_TYPE) ?? "",
      dirty: dirty,
    };
    await storeItem(dbKey, requestToStore);
  }

  async function _storeResponse(response: Response, dbKey: string) {
    const clonedResponse = response.clone();
    const responseToStore: StoredResponse = {
      data: await clonedResponse.text(),
      contentType: clonedResponse.headers.get(HTTP_HEADER_CONTENT_TYPE) ?? "",
      httpStatus: clonedResponse.status,
    };
    try {
      await storeItem(dbKey, responseToStore);
    } catch (error) {
      logError("error stsoring idb data", error);
    }
  }

  return {
    findStrategy,
    handleRouteWithStrategy,
  };
}
