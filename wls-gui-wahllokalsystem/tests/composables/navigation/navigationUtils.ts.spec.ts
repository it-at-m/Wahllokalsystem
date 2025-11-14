import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";

const { generateRandomString } = useCommonTestDataFactory();

describe("navigationUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useNavigationUtils>;

  beforeEach(() => {
    unitUnderTest = useNavigationUtils();
  });

  describe("routeWithName", () => {
    it("should_setNameInObject_when_routeNameIsGiven", () => {
      const routeName = generateRandomString(20);

      const result = unitUnderTest.routeWithName(routeName);

      const expectedResult = {
        name: routeName,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("routeWithNameAndParams", () => {
    it.each([
      {
        params: {},
        additionalTestCaseDescription: "AndParameterisEmptyObject",
      },
      {
        params: { [generateRandomString(4)]: generateRandomString(10) },
        additionalTestCaseDescription: "AndParameterisHasOneProperty",
      },
      {
        params: {
          [generateRandomString(4)]: generateRandomString(10),
          [generateRandomString(5)]: generateRandomString(10),
        },
        additionalTestCaseDescription: "AndParameterisHasTwoProperties",
      },
    ])(
      "should_setNameAndParamsInObject_when_routeNameAndParamsAreGiven$additionalTestCaseDescription",
      (args) => {
        const routeName = generateRandomString(20);

        const result = unitUnderTest.routeWithNameAndParams(
          routeName,
          args.params
        );

        const expectedResult = {
          name: routeName,
          params: args.params,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });
});
