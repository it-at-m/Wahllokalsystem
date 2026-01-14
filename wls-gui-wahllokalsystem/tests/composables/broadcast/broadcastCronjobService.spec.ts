import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadLatestMessage: vi.fn(),
  setInterval: vi.fn(),
  clearInterval: vi.fn(),
}));

vi.mock("@/stores/broadcastStore.ts", () => ({
  useBroadcastStore: () => ({
    loadLatestMessage: mockDefinitions.loadLatestMessage,
  }),
}));

describe("broadcastCronjobService.ts", () => {
  let unitUnderTest: ReturnType<typeof useBroadcastCronjobService>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useBroadcastCronjobService();

    vi.spyOn(global, "setInterval").mockImplementation(
      mockDefinitions.setInterval
    );
    vi.spyOn(global, "clearInterval").mockImplementation(
      mockDefinitions.clearInterval
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.restoreAllMocks();
  });

  describe("startBroadcastMessageInterval", () => {
    it("should_setupIntervalWith5Minutes_when_functionIsCalled", () => {
      unitUnderTest.startBroadcastMessageInterval();

      expect(mockDefinitions.setInterval).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.setInterval.mock.calls[0]).toEqual([
        expect.anything(),
        300_000,
      ]);
    });

    it("should_instantlyCallLoadLatestMessage_when_functionIsCalled", () => {
      unitUnderTest.startBroadcastMessageInterval();

      expect(mockDefinitions.loadLatestMessage).toHaveBeenCalledTimes(1);
    });

    it("should_stopOldInterval_when_oldIntervalIsRunning", () => {
      const mockedIntervalNumber = 1;
      mockDefinitions.setInterval.mockReturnValue(mockedIntervalNumber);

      unitUnderTest.startBroadcastMessageInterval();
      unitUnderTest.startBroadcastMessageInterval();

      expect(mockDefinitions.clearInterval.mock.calls).toStrictEqual([
        [mockedIntervalNumber],
      ]);
    });
  });

  describe("stopBroadcastMessageInterval", () => {
    it("should_callClearInterval_when_intervalWasCreatedBefore", () => {
      const mockedIntervalNumber = 1;
      mockDefinitions.setInterval.mockReturnValue(mockedIntervalNumber);

      unitUnderTest.startBroadcastMessageInterval();

      unitUnderTest.stopBroadcastMessageInterval();

      expect(mockDefinitions.clearInterval.mock.calls).toStrictEqual([
        [mockedIntervalNumber],
      ]);
    });

    it("should_notCallClearInterval_when_intervalWasNotCreatedBefore", () => {
      unitUnderTest.stopBroadcastMessageInterval();

      expect(mockDefinitions.clearInterval).toHaveBeenCalledTimes(0);
    });
  });
});
