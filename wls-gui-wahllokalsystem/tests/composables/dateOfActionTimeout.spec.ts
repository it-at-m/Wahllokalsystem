import { flushPromises } from "@vue/test-utils";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useDateOfActionTimeout } from "@/composables/dateOfActionTimeout.ts";

const mockedNow = new Date();

const { logDebug } = useLogging("dateOfActionTimeout.spec.ts");

describe("dateOfActionTimeout", () => {
  const TITLE = "Test Titel";

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  const defaultCallback = function () {
    logDebug("defaultCallback was called during test");
  };

  it("should_notSetTimeout_when_dateOfActionIsUndefined", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    useDateOfActionTimeout(TITLE, ref(undefined), defaultCallback);

    expect(setTimeoutSpy).toHaveBeenCalledTimes(0);

    setTimeoutSpy.mockRestore();
  });

  it("should_notSetTimeout_when_dateOfActionIsInThePast", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    useDateOfActionTimeout(
      TITLE,
      ref(new Date(mockedNow.getTime() - 1)),
      defaultCallback
    );

    expect(setTimeoutSpy).toHaveBeenCalledTimes(0);

    setTimeoutSpy.mockRestore();
  });

  it("should_setTimeout_when_dateOfActionIsInTheFuture", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = defaultCallback;
    const { setupTimer } = useDateOfActionTimeout(
      TITLE,
      ref(new Date(mockedNow.getTime() + 1)),
      callback
    );
    setupTimer();

    expect(setTimeoutSpy).toHaveBeenCalledWith(callback, 1);

    setTimeoutSpy.mockRestore();
  });

  it("should_setTimeout_when_dateOfActionIsRightNow", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = defaultCallback;
    const { setupTimer } = useDateOfActionTimeout(
      TITLE,
      ref(new Date(mockedNow.getTime())),
      callback
    );
    setupTimer();

    expect(setTimeoutSpy).toHaveBeenCalledWith(callback, 0);

    setTimeoutSpy.mockRestore();
  });

  it("should_callCallback_when_timeOutHasExceeded", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = vi.fn();
    const { setupTimer } = useDateOfActionTimeout(
      TITLE,
      ref(new Date(mockedNow.getTime() + 1)),
      callback
    );
    setupTimer();

    vi.advanceTimersByTime(1);

    expect(callback).toHaveBeenCalledTimes(1);

    setTimeoutSpy.mockRestore();
  });

  it("should_notSetTimeout_when_delayIsLargerThanMaxDelay", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    useDateOfActionTimeout(
      TITLE,
      ref(new Date(mockedNow.getTime() + 0x80000000)),
      defaultCallback
    );

    expect(setTimeoutSpy).toHaveBeenCalledTimes(0);

    setTimeoutSpy.mockRestore();
  });

  it("should_setTimeout_when_delayIsEqualMaxDelay", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = defaultCallback;
    const { setupTimer } = useDateOfActionTimeout(
      TITLE,
      ref(new Date(mockedNow.getTime() + 0x7fffffff)),
      callback
    );
    setupTimer();

    expect(setTimeoutSpy).toHaveBeenCalledWith(callback, 0x7fffffff);

    setTimeoutSpy.mockRestore();
  });

  it("should_clearOldTimeOut_when_newTimeoutIsSetAfterDateOfActionChanged", async () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");
    const clearTimeoutSpy = vi.spyOn(window, "clearTimeout");

    const callback = defaultCallback;
    const dateOfAction = ref(new Date(mockedNow.getTime() + 1));

    const mockedTimeoutNumber = 0;
    // @ts-expect-error: error TS2322: Type 'number' is not assignable to type 'Timeout'
    setTimeoutSpy.mockImplementation(() => mockedTimeoutNumber);

    const { setupTimer } = useDateOfActionTimeout(
      TITLE,
      dateOfAction,
      callback
    );
    setupTimer();

    dateOfAction.value = new Date(mockedNow.getTime() + 2);
    await flushPromises();

    expect(setTimeoutSpy.mock.calls).toStrictEqual([
      [callback, 1],
      [callback, 2],
    ]);
    expect(clearTimeoutSpy).toHaveBeenCalledWith(mockedTimeoutNumber);

    setTimeoutSpy.mockRestore();
    clearTimeoutSpy.mockRestore();
  });

  it("should_clearOldTimeOut_when_clearFunctionIsCalled", async () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");
    const clearTimeoutSpy = vi.spyOn(window, "clearTimeout");

    const dateOfAction = ref(new Date(mockedNow.getTime() + 1));

    const callback = defaultCallback;
    const { setupTimer, clearTimer } = useDateOfActionTimeout(
      TITLE,
      ref(dateOfAction),
      callback
    );
    setupTimer();

    await flushPromises();
    clearTimer();

    expect(clearTimeoutSpy).toHaveBeenCalledOnce();

    setTimeoutSpy.mockRestore();
    clearTimeoutSpy.mockRestore();
  });
});
