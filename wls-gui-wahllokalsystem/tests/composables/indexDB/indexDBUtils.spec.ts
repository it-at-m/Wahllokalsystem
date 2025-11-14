import { useIndexDBValueTestDataFactory } from "@tests/utils/indexDB/IndexDBValueTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useIndexDBUtils } from "@/composables/indexDB/indexDBUtils.ts";

const { prepareIndexDBValue } = useIndexDBValueTestDataFactory();

describe("indexDBUtils", () => {
  let unitUnderTest: ReturnType<typeof useIndexDBUtils>;

  beforeEach(() => {
    unitUnderTest = useIndexDBUtils();
  });

  describe("compareByTimestamp", () => {
    it("should_returnEqual_when_bothTimestampsAreUndefined", () => {
      const a = prepareIndexDBValue().timestamp(undefined).build();
      const b = prepareIndexDBValue().timestamp(undefined).build();

      expect(unitUnderTest.compareByTimestamp(a, b)).toStrictEqual(0);
    });

    it("should_returnEqual_when_bothTimestampsHaveSameValue", () => {
      const timestamp = 23;
      const a = prepareIndexDBValue().timestamp(timestamp).build();
      const b = prepareIndexDBValue().timestamp(timestamp).build();

      expect(unitUnderTest.compareByTimestamp(a, b)).toStrictEqual(0);
    });

    it("should_returnSmaller_when_aHasTimestampButBTimestampIsUndefined", () => {
      const a = prepareIndexDBValue().timestamp(10).build();
      const b = prepareIndexDBValue().timestamp(undefined).build();

      expect(unitUnderTest.compareByTimestamp(a, b)).toBeLessThan(0);
    });

    it("should_returnSmaller_when_aHasTimestampThatIsSmallerThanBTimestamp", () => {
      const a = prepareIndexDBValue().timestamp(10).build();
      const b = prepareIndexDBValue().timestamp(11).build();

      expect(unitUnderTest.compareByTimestamp(a, b)).toBeLessThan(0);
    });

    it("should_returnLarger_when_aHasNoTimestampButBHasTimestamp", () => {
      const a = prepareIndexDBValue().timestamp(undefined).build();
      const b = prepareIndexDBValue().timestamp(10).build();

      expect(unitUnderTest.compareByTimestamp(a, b)).toBeGreaterThan(0);
    });

    it("should_returnLarger_when_aHasTimestampThatIsLargerThanBTimestamp", () => {
      const a = prepareIndexDBValue().timestamp(12).build();
      const b = prepareIndexDBValue().timestamp(11).build();

      expect(unitUnderTest.compareByTimestamp(a, b)).toBeGreaterThan(0);
    });
  });
});
