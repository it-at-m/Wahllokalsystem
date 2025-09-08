import type { RouteHandlerCallbackOptions } from "workbox-core";
import type { RouteHandlerCallback } from "workbox-core/src/types.ts";
import type { HTTPMethod } from "workbox-routing/utils/constants";

import { defaultMethod, validMethods } from "workbox-routing/utils/constants";

import { useRequestStrategies } from "@/composables/api/requestStrategies.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { REQUEST_HEADER_OFFLINE_STRATEGY } from "@/constants.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const { logDebug } = useLogging("requestStrategyManager");
const {
  onlineFirstGetRequestHandler,
  postRequestHandler,
  offlineFirstGetRequestHandler,
  unhandledFetch,
} = useRequestStrategies();

export function useRequestStrategyManager() {
  const DEFAULT_OFFLINE_STRATEGY = FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST;

  const offlineStrategiesHandlers: Record<
    FetchStrategiesEnum,
    Map<HTTPMethod, RouteHandlerCallback>
  > = {
    STRATEGY_OFFLINE_FIRST: new Map([
      ["GET", offlineFirstGetRequestHandler],
      ["POST", postRequestHandler],
    ]),
    STRATEGY_ONLINE_FIRST: new Map([
      ["GET", onlineFirstGetRequestHandler],
      ["POST", postRequestHandler],
    ]),
    STRATEGY_ONLINE_ONLY: new Map([]),
  };

  function _findStrategy(request: Request): FetchStrategiesEnum {
    const offlineStrategyAsString = request.headers.get(
      REQUEST_HEADER_OFFLINE_STRATEGY
    );
    if (!offlineStrategyAsString) {
      return DEFAULT_OFFLINE_STRATEGY;
    }

    const fetchStrategy = Object.values(FetchStrategiesEnum).find(
      (value) => value === offlineStrategyAsString
    );

    logDebug(
      `strategy for request ${request.method} ${request.url} is ${fetchStrategy}`
    );
    return fetchStrategy ?? DEFAULT_OFFLINE_STRATEGY;
  }

  async function handleRequestWithStrategy(
    options: RouteHandlerCallbackOptions
  ): Promise<Response> {
    const requestStrategy = _findStrategy(options.request);

    const httpMethod =
      validMethods.find((method) => options.request.method === method) ??
      defaultMethod;
    let handler = offlineStrategiesHandlers[requestStrategy].get(httpMethod);
    if (!handler) {
      logDebug(
        `no defined handler found for ${httpMethod} - using default handler`
      );
      handler = unhandledFetch;
    }
    return handler(options);
  }

  return {
    handleRequestWithStrategy,
  };
}
