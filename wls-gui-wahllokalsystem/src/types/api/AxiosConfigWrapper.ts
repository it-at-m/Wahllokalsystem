import type { AxiosRequestConfig } from "axios";

import { AxiosHeaders } from "axios";

import { REQUEST_HEADER_OFFLINE_STRATEGY } from "@/constants.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

export class AxiosConfigWrapper implements AxiosRequestConfig {
  headers?: AxiosHeaders;

  public requestAsOnlineOnly() {
    this.withFetchStrategy(FetchStrategiesEnum.STRATEGY_ONLINE_ONLY);
    return this;
  }

  public requestAsOnlineFirst() {
    this.withFetchStrategy(FetchStrategiesEnum.STRATEGY_ONLINE_FIRST);
    return this;
  }

  public requestAsPostOnlineOnlyButDirtyOnFail() {
    this.withFetchStrategy(
      FetchStrategiesEnum.STRATEGY_POST_ONLINE_ONLY_BUT_DIRTY_ON_FAIL
    );
    return this;
  }

  public requestAsPostOnlineOnlyAndStoreOnSuccess() {
    this.withFetchStrategy(
      FetchStrategiesEnum.STRATEGY_POST_ONLINE_ONLY_AND_STORE_ON_SUCCESS
    );
    return this;
  }

  public requestAsSaveOfflineOnly() {
    this.withFetchStrategy(FetchStrategiesEnum.STRATEGY_SAVE_OFFLINE_ONLY);
    return this;
  }

  public requestAsOfflineFirst() {
    this.withFetchStrategy(FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST);
    return this;
  }

  public requestAsForceRefresh() {
    this.withFetchStrategy(FetchStrategiesEnum.STRATEGY_REFRESH_OFFLINE_DATA);
    return this;
  }

  private withFetchStrategy(strategy: FetchStrategiesEnum) {
    this.addHeader(REQUEST_HEADER_OFFLINE_STRATEGY, strategy);
  }

  private addHeader(key: string, value: string) {
    if (!this.headers) {
      this.headers = new AxiosHeaders();
    }

    this.headers.set(key, value);
  }
}
