import { describe, expect, it, vi } from "vitest";

import { useCurrentTime } from "@/composables/useCurrentTime.ts";

describe("useCurrentTime", () => {
  it("should_updateCurrentTime_when_timeIncrementsForOneSecond", () => {
    vi.useFakeTimers(); // Fake Timer aktivieren
    const { currentTime } = useCurrentTime();

    // initial date
    const initialTime = new Date(currentTime.value);

    // increment time for 1 second
    vi.advanceTimersByTime(1000);

    expect(currentTime.value).not.toEqual(initialTime);
    expect(currentTime.value.getSeconds()).toBe(
      (initialTime.getSeconds() + 1) % 60
    );
  });
});
