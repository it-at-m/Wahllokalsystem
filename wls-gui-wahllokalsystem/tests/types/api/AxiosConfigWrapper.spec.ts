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
});
