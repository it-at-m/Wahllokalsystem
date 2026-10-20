import { describe, expect, it } from "vitest";

import { REQUEST_HEADER_OFFLINE_STRATEGY } from "@/constants.ts";
import { AxiosConfigWrapper } from "@/types/api/AxiosConfigWrapper.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

describe("AxiosConfigWrapper", () => {
  describe("requestAsOnlineOnly", () => {
    it("should_setFetchStrategyOnlineOnly_when_called", () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.requestAsOnlineOnly();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_ONLINE_ONLY);
    });

    it("should_replaceOldHeader_when_called", async () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.headers?.set(REQUEST_HEADER_OFFLINE_STRATEGY, "sthElse");

      wrapper.requestAsOnlineOnly();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_ONLINE_ONLY);
    });
  });

  describe("requestAsOnlineFirst", () => {
    it("should_setFetchStrategyOnlineOnly_when_called", () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.requestAsOnlineFirst();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_ONLINE_FIRST);
    });

    it("should_replaceOldHeader_when_called", async () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.headers?.set(REQUEST_HEADER_OFFLINE_STRATEGY, "sthElse");

      wrapper.requestAsOnlineFirst();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_ONLINE_FIRST);
    });
  });

  describe("requestAsPostOnlineOnlyAndStoreOnSuccess", () => {
    it("should_setFetchStrategyPostOnlineOnlyAndStoreOnSuccess_when_called", () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.requestAsPostOnlineOnlyAndStoreOnSuccess();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(
        FetchStrategiesEnum.STRATEGY_POST_ONLINE_ONLY_AND_STORE_ON_SUCCESS
      );
    });

    it("should_replaceOldHeader_when_called", async () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.headers?.set(REQUEST_HEADER_OFFLINE_STRATEGY, "sthElse");

      wrapper.requestAsPostOnlineOnlyAndStoreOnSuccess();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(
        FetchStrategiesEnum.STRATEGY_POST_ONLINE_ONLY_AND_STORE_ON_SUCCESS
      );
    });
  });

  describe("requestAsPostOnlineOnlyButDirtyOnFail", () => {
    it("should_setFetchStrategyPostOnlyButDirtyOnFail_when_called", () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.requestAsPostOnlineOnlyButDirtyOnFail();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(
        FetchStrategiesEnum.STRATEGY_POST_ONLINE_ONLY_BUT_DIRTY_ON_FAIL
      );
    });

    it("should_replaceOldHeader_when_called", async () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.headers?.set(REQUEST_HEADER_OFFLINE_STRATEGY, "sthElse");

      wrapper.requestAsPostOnlineOnlyButDirtyOnFail();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(
        FetchStrategiesEnum.STRATEGY_POST_ONLINE_ONLY_BUT_DIRTY_ON_FAIL
      );
    });
  });

  describe("requestAsOfflineFirst", () => {
    it("should_setFetchStrategyOnlineOnly_when_called", () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.requestAsOfflineFirst();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST);
    });

    it("should_replaceOldHeader_when_called", async () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.headers?.set(REQUEST_HEADER_OFFLINE_STRATEGY, "sthElse");

      wrapper.requestAsOfflineFirst();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST);
    });
  });

  describe("requestAsForceRefresh", () => {
    it("should_setFetchStrategyRefreshOfflineData_when_called", () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.requestAsForceRefresh();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_REFRESH_OFFLINE_DATA);
    });

    it("should_replaceOldHeader_when_called", async () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.headers?.set(REQUEST_HEADER_OFFLINE_STRATEGY, "sthElse");

      wrapper.requestAsForceRefresh();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_REFRESH_OFFLINE_DATA);
    });
  });

  describe("requestAsSaveOfflineOnly", () => {
    it("should_setFetchStrategySaveOfflineOnly_when_called", () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.requestAsSaveOfflineOnly();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_SAVE_OFFLINE_ONLY);
    });

    it("should_replaceOldHeader_when_called", async () => {
      const wrapper = new AxiosConfigWrapper();
      wrapper.headers?.set(REQUEST_HEADER_OFFLINE_STRATEGY, "sthElse");

      wrapper.requestAsSaveOfflineOnly();

      expect(
        wrapper.headers?.get(REQUEST_HEADER_OFFLINE_STRATEGY)
      ).toStrictEqual(FetchStrategiesEnum.STRATEGY_SAVE_OFFLINE_ONLY);
    });
  });
});
