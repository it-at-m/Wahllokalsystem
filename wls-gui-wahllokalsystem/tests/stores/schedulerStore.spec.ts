import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useSchedulerStore } from "@/stores/schedulerStore.ts";

describe("schedulerStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useSchedulerStore>;

  const TITLE = "Test Titel";
  const DELAY = 100;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useSchedulerStore();
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("registerInterval", () => {
    it("should_executeAction_when_intervallIsSetWithRunAfterRegisterFlagTrue", () => {
      const action = vi.fn();
      const intervalConfig = {
        title: TITLE,
        action: action,
        delay: DELAY,
        runActionAfterRegister: true,
      };
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

    it("should_executeAction_when_intervallIsSetWithRunAfterRegisterFlagFalse", () => {
      const action = vi.fn();
      const intervalConfig = {
        title: TITLE,
        action: action,
        delay: DELAY,
        runActionAfterRegister: false,
      };
      const id = unitUnderTest.registerInterval(intervalConfig);

      return new Promise((resolve) => {
        setTimeout(() => {
          expect(action).toHaveBeenCalledTimes(1);
          unitUnderTest.stopInterval(id);
          resolve({});
        }, DELAY + 50);
      });
    });

    it("should_executeAction_when_intervallIsSetWithoutRunAfterRegisterFlag", () => {
      const action = vi.fn();
      const intervalConfig = {
        title: TITLE,
        action: action,
        delay: DELAY,
      };
      const id = unitUnderTest.registerInterval(intervalConfig);

      return new Promise((resolve) => {
        setTimeout(() => {
          expect(action).toHaveBeenCalledTimes(1);
          unitUnderTest.stopInterval(id);
          resolve({});
        }, DELAY + 50);
      });
    });
  });

  describe("registerTimeout", () => {
    it("should_executeAction_when_timeoutIsSetAndTimeHasPassed", () => {
      const action = vi.fn();
      const timeoutConfig = {
        title: TITLE,
        action: action,
        dateOfAction: new Date(Date.now() + DELAY),
      };

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
      const clearIntervalMock = vi.spyOn(global, "clearInterval");

      const action = vi.fn();
      const intervalConfig = {
        title: TITLE,
        action: action,
        delay: DELAY,
      };

      const id = unitUnderTest.registerInterval(intervalConfig);

      unitUnderTest.stopInterval(id);

      expect(clearIntervalMock).toHaveBeenCalledWith(id);

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
      const clearTimeoutMock = vi.spyOn(global, "clearTimeout");

      const action = vi.fn();
      const timeoutConfig = {
        title: TITLE,
        action: action,
        dateOfAction: new Date(Date.now() + DELAY),
      };

      const id = unitUnderTest.registerTimeout(timeoutConfig);

      unitUnderTest.stopTimeout(id);

      expect(clearTimeoutMock).toHaveBeenCalledWith(id);

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
      const clearIntervalMock = vi.spyOn(global, "clearInterval");
      const clearTimeoutMock = vi.spyOn(global, "clearTimeout");

      const intervalAction = vi.fn();
      const intervalConfig = {
        title: TITLE,
        action: intervalAction,
        delay: DELAY,
      };

      const timeoutAction = vi.fn();
      const timeoutConfig = {
        title: TITLE,
        action: timeoutAction,
        dateOfAction: new Date(Date.now() + DELAY),
      };

      unitUnderTest.registerInterval(intervalConfig);
      unitUnderTest.registerTimeout(timeoutConfig);

      unitUnderTest.stopAll();

      expect(clearIntervalMock).toHaveBeenCalled();
      expect(clearTimeoutMock).toHaveBeenCalled();

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
