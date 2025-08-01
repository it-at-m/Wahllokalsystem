import type { RouteHandlerCallbackOptions } from "workbox-core";
import type { RouteHandlerCallback } from "workbox-core/src/types.ts";

import { HttpStatusCode } from "axios";

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
  httpStatus: number;
}

const { getItemFromIDB, storeItem } = useIndexDB();
const { log, logError } = useLogging("offlineStrategies");

export function useOfflineStrategies() {
  const DEFAULT_OFFLINE_STRATEGY = FetchStrategiesEnum.STRATEGY_ONLINE_ONLY;

  const offlineStrategiesHandlers: Record<
    FetchStrategiesEnum,
    RouteHandlerCallback
  > = {
    STRATEGY_OFFLINE_FIRST: _onlineFirstRequestHandler,
    STRATEGY_ONLINE_FIRST: _onlineFirstRequestHandler,
    STRATEGY_ONLINE_ONLY: _onlineFirstRequestHandler,
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
    return offlineStrategiesHandlers[fetchStrategy](options);
  }

  async function _onlineFirstRequestHandler(
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
