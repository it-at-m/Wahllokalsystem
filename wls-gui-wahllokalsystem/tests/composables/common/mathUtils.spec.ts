import { beforeEach, describe, expect, it } from "vitest";

import { useMathUtils } from "@/composables/common/mathUtils.ts";

describe("mathUtils", () => {
  let unitUnderTest: ReturnType<typeof useMathUtils>;

  beforeEach(() => {
    unitUnderTest = useMathUtils();
  });
  describe("maxOfOptionalNumbers", () => {
    it.each([
      { values: [9, 2, 3, 15, 4, 2], description: "WithoutNullValues" },
      {
        values: [9, 2, 3, 15, null, 4, 2],
        description: "WithAtLeastOneNullValue",
      },
    ])(
      "should_returnMaxValue_when_atLeastOneNonNullValueIsGiven$description",
      (testCaseArg) => {
        const result = unitUnderTest.maxOfOptionalNumbers(testCaseArg.values);
        expect(result).toStrictEqual(15);
      }
    );
  });

  it("should_returnNull_when_anEmptyArrayIsGiven", () => {
    expect(unitUnderTest.maxOfOptionalNumbers([])).toBeNull();
  });

  it("should_returnNull_when_allNullValuesAreGiven", () => {
    expect(
      unitUnderTest.maxOfOptionalNumbers([null, null, null, null])
    ).toBeNull();
  });
});
