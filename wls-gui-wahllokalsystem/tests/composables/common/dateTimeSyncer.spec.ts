import type { Ref } from "vue";

import { beforeEach, describe, expect, it } from "vitest";
import { computed, nextTick, ref } from "vue";

import { useDateTimeSyncer } from "@/composables/common/dateTimeSyncer.ts";

describe("dateTimeSyncer.ts", () => {
  let unitUnderTest: ReturnType<typeof useDateTimeSyncer>;
  let initialState: Ref<Date | undefined>;

  beforeEach(() => {
    initialState = ref(new Date());
    unitUnderTest = useDateTimeSyncer(computed(() => initialState.value));
  });

  describe("dateAndTimeCombined", () => {
    it("should_returnDateOfDateOnlyAndTimeOfTimeOnly_when_bothValuesAreSet", async () => {
      unitUnderTest.dateOnly.value = new Date("2025-07-29T01:00:00");
      unitUnderTest.timeOnly.value = new Date("2025-07-30T01:23:45.678");

      await nextTick();

      const expectedValue = new Date("2025-07-29T01:23:45.678");
      expect(unitUnderTest.dateAndTimeCombined.value?.getTime()).toStrictEqual(
        expectedValue.getTime()
      );
    });

    it("should_returnNull_when_dateOnlyIsUndefined", async () => {
      unitUnderTest.dateOnly.value = undefined;
      unitUnderTest.timeOnly.value = new Date("2025-07-30T01:23:45.678");

      await nextTick();

      expect(unitUnderTest.dateAndTimeCombined.value).toStrictEqual(null);
    });

    it("should_returnNull_when_timeOnlyIsUndefined", async () => {
      unitUnderTest.dateOnly.value = new Date("2025-07-29T01:00:00");
      unitUnderTest.timeOnly.value = undefined;

      await nextTick();

      expect(unitUnderTest.dateAndTimeCombined.value).toStrictEqual(null);
    });
  });

  describe("dateOnly", () => {
    it("should_returnSameObjectAsInitialState_when_initialStateIsADate", () => {
      expect(initialState.value?.getTime()).not.toBeUndefined();

      expect(unitUnderTest.dateOnly.value).toBe(initialState.value);
      expect(unitUnderTest.dateOnly.value?.getTime()).toStrictEqual(
        initialState.value?.getTime()
      );
    });
  });

  describe("timeOnly", () => {
    it("should_returnSameObjectAsInitialState_when_initialStateIsADate", () => {
      expect(initialState.value?.getTime()).not.toBeUndefined();

      expect(unitUnderTest.timeOnly.value).toBe(initialState.value);
      expect(unitUnderTest.timeOnly.value?.getTime()).toStrictEqual(
        initialState.value?.getTime()
      );
    });
  });

  describe("watcher", () => {
    it.each([undefined, new Date("2025-07-09T12:09:42.312")])(
      "should_updateDateOnlyAndTimeOnly_when_initStateChangedAndIsNotDateAndTimeCombined",
      async (newStateValue) => {
        initialState.value = newStateValue;

        await nextTick();

        expect(unitUnderTest.timeOnly.value).toStrictEqual(newStateValue);
        expect(unitUnderTest.dateOnly.value).toStrictEqual(newStateValue);
      }
    );
  });
});
