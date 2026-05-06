import { createTestingPinia } from "@pinia/testing";
import { flushPromises } from "@vue/test-utils";
import { setActivePinia } from "pinia";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useLogoutOnInactivity } from "@/composables/user/logoutOnInactivity.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  logout: vi.fn(),
}));

vi.mock("@/composables/user/logoutService.ts", () => ({
  useLogoutService: () => ({
    logout: mockDefinitions.logout,
  }),
}));

const mockedNow = new Date();
const INACTIVE_TIMEOUT_MS = 1000;

describe("logoutOnInactivity.ts", () => {
  let testPinia: ReturnType<typeof createTestingPinia>;
  let unitUnderTest: ReturnType<typeof useLogoutOnInactivity>;

  beforeEach(() => {
    testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    setActivePinia(testPinia);
    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  afterAll(() => {
    setActivePinia(undefined);
  });

  describe("useLogoutOnInactivity", () => {
    it("should_callLogout_when_noActivityWasDone", async () => {
      // @ts-expect-error: cannot set readonly
      useInfomanagementStore().delayBeforeInactiveLogoutInMilliseconds =
        INACTIVE_TIMEOUT_MS;

      useLogoutOnInactivity();

      vi.advanceTimersByTime(INACTIVE_TIMEOUT_MS);
      await flushPromises();

      expect(mockDefinitions.logout).toHaveBeenCalledOnce();
    });

    it("should_notCallLogout_when_userWasActive", async () => {
      // @ts-expect-error: cannot set readonly
      useInfomanagementStore().delayBeforeInactiveLogoutInMilliseconds =
        INACTIVE_TIMEOUT_MS;

      useLogoutOnInactivity();

      vi.advanceTimersByTime(1);
      window.dispatchEvent(new Event("load"));
      vi.advanceTimersByTime(INACTIVE_TIMEOUT_MS - 1);
      await flushPromises();

      expect(mockDefinitions.logout).not.toHaveBeenCalled();
    });

    it.each([
      "load",
      "mousemove",
      "mousedown",
      "touchstart",
      "click",
      "keypress",
      "scroll",
    ])(
      "should_callLogoutLater_when_userWasActiveWithAction'%s'",
      async (windowEventName) => {
        // @ts-expect-error: cannot set readonly
        useInfomanagementStore().delayBeforeInactiveLogoutInMilliseconds =
          INACTIVE_TIMEOUT_MS;

        useLogoutOnInactivity();

        const timerWhenUserDidSth = 100;
        vi.advanceTimersByTime(timerWhenUserDidSth);
        window.dispatchEvent(new Event(windowEventName));
        vi.advanceTimersByTime(INACTIVE_TIMEOUT_MS - timerWhenUserDidSth);
        await flushPromises();

        expect(mockDefinitions.logout).not.toHaveBeenCalled();

        vi.advanceTimersByTime(INACTIVE_TIMEOUT_MS - timerWhenUserDidSth);
        await flushPromises();
        expect(mockDefinitions.logout).toHaveBeenCalledOnce();
      }
    );
  });
});
