import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useDateOfActionTimeout } from "@/composables/dateOfActionTimeout.ts";

const mockedNow = new Date();

describe("dateOfActionTimeout", () => {
  beforeEach(() => {
    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  const defaultCallback = function () {
    console.debug("defaultCallback was called during test");
  };

  it("should_notSetTimeout_when_dateOfActionIsUndefined", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    useDateOfActionTimeout(ref(undefined), defaultCallback);

    expect(setTimeoutSpy).toHaveBeenCalledTimes(0);

    setTimeoutSpy.mockRestore();
  });

  it("should_notSetTimeout_when_dateOfActionIsInThePast", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    useDateOfActionTimeout(
      ref(new Date(mockedNow.getTime() - 1)),
      defaultCallback
    );

    expect(setTimeoutSpy).toHaveBeenCalledTimes(0);

    setTimeoutSpy.mockRestore();
  });

  it("should_setTimeout_when_dateOfActionIsInTheFuture", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = defaultCallback;
    useDateOfActionTimeout(ref(new Date(mockedNow.getTime() + 1)), callback);

    expect(setTimeoutSpy).toHaveBeenCalledWith(callback, 1);

    setTimeoutSpy.mockRestore();
  });

  it("should_setTimeout_when_dateOfActionIsRightNow", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = defaultCallback;
    useDateOfActionTimeout(ref(new Date(mockedNow.getTime())), callback);

    expect(setTimeoutSpy).toHaveBeenCalledWith(callback, 0);

    setTimeoutSpy.mockRestore();
  });

  it("should_callCallback_when_timeOutHasExceeded", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = vi.fn();
    useDateOfActionTimeout(ref(new Date(mockedNow.getTime() + 1)), callback);

    vi.advanceTimersByTime(1);

    expect(callback).toHaveBeenCalledTimes(1);

    setTimeoutSpy.mockRestore();
  });

  it("should_notSetTimeout_when_delayIsLargerThanMaxDelay", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    useDateOfActionTimeout(
      ref(new Date(mockedNow.getTime() + 0x80000000)),
      defaultCallback
    );

    expect(setTimeoutSpy).toHaveBeenCalledTimes(0);

    setTimeoutSpy.mockRestore();
  });

  it("should_setTimeout_when_delayIsEqualMaxDelay", () => {
    const setTimeoutSpy = vi.spyOn(window, "setTimeout");

    const callback = defaultCallback;
    useDateOfActionTimeout(
      ref(new Date(mockedNow.getTime() + 0x7fffffff)),
      callback
    );

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

    useDateOfActionTimeout(dateOfAction, callback);

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
});
