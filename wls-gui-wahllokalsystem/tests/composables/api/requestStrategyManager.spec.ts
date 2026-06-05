import type { MockInstance } from "vitest";
import type { RouteHandlerCallbackOptions } from "workbox-core/src/types.ts";
import type { HTTPMethod } from "workbox-routing/utils/constants";

import {
  afterAll,
  afterEach,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useRequestStrategyManager } from "@/composables/api/RequestStrategyManager.ts";
import { REQUEST_HEADER_OFFLINE_STRATEGY } from "@/constants.ts";
import { FetchStrategiesEnum } from "@/types/api/FetchStrategiesEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  //names were added to have these logged instead of spy when an error in expect occurs
  onlineFirstGetRequestHandler: vi
    .fn()
    .mockName("onlineFirstGetRequestHandler"),
  postRequestHandler: vi.fn().mockName("onlineFirstPostRequestHandler"),
  offlineFirstGetRequestHandler: vi
    .fn()
    .mockName("offlineFirstGetRequestHandler"),
  unhandledFetch: vi.fn().mockName("unhandledFetch"),
}));

vi.mock("@/composables/api/requestStrategies.ts", () => ({
  useRequestStrategies: vi.fn().mockImplementation(() => ({
    onlineFirstGetRequestHandler: mockDefinitions.onlineFirstGetRequestHandler,
    postRequestHandler: mockDefinitions.postRequestHandler,
    offlineFirstGetRequestHandler:
      mockDefinitions.offlineFirstGetRequestHandler,
    unhandledFetch: mockDefinitions.unhandledFetch,
  })),
}));

describe("requestStrategyManager.ts", () => {
  let unitUnderTest: ReturnType<typeof useRequestStrategyManager>;

  beforeEach(() => {
    unitUnderTest = useRequestStrategyManager();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  afterAll(() => {
    vi.clearAllMocks();
  });

  describe("handleRequestWithStrategy", () => {
    let httpMethod: HTTPMethod;

    describe("http-Method is GET", () => {
      beforeAll(() => {
        httpMethod = "GET";
      });

      const testcases = [
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          expectedCalledMock: mockDefinitions.onlineFirstGetRequestHandler,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST,
          expectedCalledMock: mockDefinitions.offlineFirstGetRequestHandler,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_ONLY,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: undefined,
          expectedCalledMock: mockDefinitions.offlineFirstGetRequestHandler,
        },
      ];

      it.each(testcases)(
        "should_callCorrectHandler_when_strategyIs'$strategy'",
        (testcaseArguments) => {
          verifyThatCorrectHandlerWasCalledForStrategy(
            httpMethod,
            testcaseArguments
          );
        }
      );

      it("should_coverAllFetchStrategies_when_testcasesAreDefined", () => {
        verifyThatAllFetchStrategiesAreCoveredByTestcases(testcases);
      });
    });

    describe("http-Method is POST", () => {
      beforeAll(() => {
        httpMethod = "POST";
      });

      const testcases = [
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          expectedCalledMock: mockDefinitions.postRequestHandler,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST,
          expectedCalledMock: mockDefinitions.postRequestHandler,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_ONLY,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: undefined,
          expectedCalledMock: mockDefinitions.postRequestHandler,
        },
      ];

      it.each(testcases)(
        "should_callCorrectHandler_when_strategyIs'$strategy'",
        (testcaseArguments) => {
          verifyThatCorrectHandlerWasCalledForStrategy(
            httpMethod,
            testcaseArguments
          );
        }
      );

      it("should_coverAllFetchStrategies_when_testcasesAreDefined", () => {
        verifyThatAllFetchStrategiesAreCoveredByTestcases(testcases);
      });
    });

    describe("http-Method is PUT", () => {
      beforeAll(() => {
        httpMethod = "PUT";
      });

      const testcases = [
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_ONLY,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: undefined,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
      ];

      it.each(testcases)(
        "should_callCorrectHandler_when_strategyIs'$strategy'",
        (testcaseArguments) => {
          verifyThatCorrectHandlerWasCalledForStrategy(
            httpMethod,
            testcaseArguments
          );
        }
      );

      it("should_coverAllFetchStrategies_when_testcasesAreDefined", () => {
        verifyThatAllFetchStrategiesAreCoveredByTestcases(testcases);
      });
    });

    describe("http-Method is DELETE", () => {
      beforeAll(() => {
        httpMethod = "DELETE";
      });

      const testcases = [
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_ONLY,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: undefined,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
      ];

      it.each(testcases)(
        "should_callCorrectHandler_when_strategyIs'$strategy'",
        (testcaseArguments) => {
          verifyThatCorrectHandlerWasCalledForStrategy(
            httpMethod,
            testcaseArguments
          );
        }
      );

      it("should_coverAllFetchStrategies_when_testcasesAreDefined", () => {
        verifyThatAllFetchStrategiesAreCoveredByTestcases(testcases);
      });
    });

    describe("http-Method is PATCH", () => {
      beforeAll(() => {
        httpMethod = "PATCH";
      });

      const testcases = [
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_ONLY,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: undefined,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
      ];

      it.each(testcases)(
        "should_callCorrectHandler_when_strategyIs'$strategy'",
        (testcaseArguments) => {
          verifyThatCorrectHandlerWasCalledForStrategy(
            httpMethod,
            testcaseArguments
          );
        }
      );

      it("should_coverAllFetchStrategies_when_testcasesAreDefined", () => {
        verifyThatAllFetchStrategiesAreCoveredByTestcases(testcases);
      });
    });

    describe("http-Method is HEAD", () => {
      beforeAll(() => {
        httpMethod = "HEAD";
      });

      const testcases = [
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_OFFLINE_FIRST,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: FetchStrategiesEnum.STRATEGY_ONLINE_ONLY,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
        {
          strategy: undefined,
          expectedCalledMock: mockDefinitions.unhandledFetch,
        },
      ];

      it.each(testcases)(
        "should_callCorrectHandler_when_strategyIs'$strategy'",
        (testcaseArguments) => {
          verifyThatCorrectHandlerWasCalledForStrategy(
            httpMethod,
            testcaseArguments
          );
        }
      );

      it("should_coverAllFetchStrategies_when_testcasesAreDefined", () => {
        verifyThatAllFetchStrategiesAreCoveredByTestcases(testcases);
      });
    });
  });

  interface TestcaseArgument {
    strategy: FetchStrategiesEnum | undefined;
    expectedCalledMock: MockInstance;
  }

  function createRouteHandlerCallbackOptions(
    httpMethod: HTTPMethod,
    fetchStrategy?: FetchStrategiesEnum
  ) {
    const headers = new Headers();
    if (fetchStrategy) {
      headers.set(REQUEST_HEADER_OFFLINE_STRATEGY, fetchStrategy);
    }
    return {
      request: {
        method: httpMethod,
        headers: headers,
        url: "http://localhost",
      } as Request,
      url: new URL("http://localhost"),
    } as RouteHandlerCallbackOptions;
  }

  function verifyThatCorrectHandlerWasCalledForStrategy(
    httpMethod: HTTPMethod,
    testcaseArguments: TestcaseArgument
  ) {
    const options = createRouteHandlerCallbackOptions(
      httpMethod,
      testcaseArguments.strategy
    );
    unitUnderTest.handleRequestWithStrategy(options);

    expect(testcaseArguments.expectedCalledMock).toHaveBeenCalledWith(options);
  }

  function verifyThatAllFetchStrategiesAreCoveredByTestcases(
    testcases: TestcaseArgument[]
  ) {
    Object.values(FetchStrategiesEnum).forEach((fetchStrategy) =>
      expect(
        testcases.some((testcase) => testcase.strategy === fetchStrategy),
        `${fetchStrategy} not covered by testcases`
      ).toStrictEqual(true)
    );
    expect(
      testcases.some((testcase) => testcase.strategy === undefined),
      "undefined strategy is not covered by testcases"
    ).toStrictEqual(true);
  }
});
