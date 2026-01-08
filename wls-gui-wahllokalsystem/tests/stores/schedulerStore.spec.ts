import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useSchedulerStore } from "@/stores/schedulerStore.ts";
import { IntervalConfiguration } from "@/types/scheduler/IntervalConfiguration.ts";
import { TimeoutConfiguration } from "@/types/scheduler/TimeoutConfiguration.ts";

describe("schedulerStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useSchedulerStore>;

  const TITLE = "Test Titel";
  const DELAY = 100;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useSchedulerStore();
  });

  describe("registerInterval", () => {
    it("should_executeAction_when_intervallIsSet", () => {
      const action = vi.fn();
      const intervalConfig = new IntervalConfiguration(
        TITLE,
        action,
        DELAY,
        true
      );
      const id = unitUnderTest.registerInterval(intervalConfig);

      expect(action).toHaveBeenCalledTimes(1);

      return new Promise((resolve) => {
        setTimeout(() => {
          expect(action).toHaveBeenCalledTimes(2);
          unitUnderTest.stopInterval(id);
          resolve({});
        }, DELAY + 50);
      });
    });
  });

  describe("registerTimeout", () => {
    it("should_executeAction_when_timeoutIsSet", () => {
      const action = vi.fn();
      const timeoutConfig = new TimeoutConfiguration(
        TITLE,
        action,
        new Date(Date.now() + DELAY)
      );

      const id = unitUnderTest.registerTimeout(timeoutConfig);

      return new Promise((resolve) => {
        setTimeout(() => {
          expect(action).toHaveBeenCalledTimes(1);
          unitUnderTest.stopTimeout(id);
          resolve({});
        }, DELAY + 50);
      });
    });
  });

  describe("stopInterval", () => {
    it("should_stopInterval_when_called", () => {
      const action = vi.fn();
      const intervalConfig = new IntervalConfiguration(TITLE, action, DELAY);

      const id = unitUnderTest.registerInterval(intervalConfig);

      unitUnderTest.stopInterval(id);

      return new Promise((resolve) => {
        setTimeout(() => {
          expect(action).toHaveBeenCalledTimes(0);
          resolve({});
        }, DELAY + 50);
      });
    });
  });

  describe("stopTimeout", () => {
    it("should_stopTimeout_when_called", () => {
      const action = vi.fn();
      const timeoutConfig = new TimeoutConfiguration(
        TITLE,
        action,
        new Date(Date.now() + DELAY)
      );

      const id = unitUnderTest.registerTimeout(timeoutConfig);

      unitUnderTest.stopTimeout(id);

      return new Promise((resolve) => {
        setTimeout(() => {
          expect(action).toHaveBeenCalledTimes(0);
          resolve({});
        }, DELAY + 50);
      });
    });
  });

  describe("stopAll", () => {
    it("should_stopAllIntervalsAndTimeouts_when_called", () => {
      const intervalAction = vi.fn();
      const intervalConfig = new IntervalConfiguration(
        TITLE,
        intervalAction,
        DELAY
      );
      const timeoutAction = vi.fn();
      const timeoutConfig = new TimeoutConfiguration(
        TITLE,
        timeoutAction,
        new Date(Date.now() + DELAY)
      );

      unitUnderTest.registerInterval(intervalConfig);
      unitUnderTest.registerTimeout(timeoutConfig);

      unitUnderTest.stopAll();

      return new Promise((resolve) => {
        setTimeout(() => {
          expect(intervalAction).toHaveBeenCalledTimes(0);
          expect(timeoutAction).toHaveBeenCalledTimes(0);
          resolve({});
        }, DELAY + 50);
      });
    });
  });
});
