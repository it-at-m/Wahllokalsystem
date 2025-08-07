import type { App } from "vue";

import { withSetup } from "@tests/utils/testutils.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useCurrentTime } from "@/composables/useCurrentTime.ts";

describe("useCurrentTime", () => {
  let unitUnderTest: ReturnType<typeof useCurrentTime>;
  let app: App;

  beforeEach(() => {
    vi.useFakeTimers(); // Fake Timer aktivieren
    [unitUnderTest, app] = withSetup(useCurrentTime);
  });

  afterEach(() => {
    app.unmount();
    vi.useRealTimers();
  });

  it("should_updateCurrentTime_when_timeIncrementsForOneSecond", () => {
    // initial date
    const initialTime = new Date(unitUnderTest.currentTime.value);

    // increment time for 1 second
    vi.advanceTimersByTime(1000);

    expect(unitUnderTest.currentTime.value).not.toEqual(initialTime);
    expect(unitUnderTest.currentTime.value.getSeconds()).toBe(
      (initialTime.getSeconds() + 1) % 60
    );
  });
});
