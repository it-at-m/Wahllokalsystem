import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";
import type { RouteHandlerCallbackOptions } from "workbox-core/src/types.ts";
import type { HTTPMethod } from "workbox-routing/utils/constants";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useIndexDBValueTestDataFactory } from "@tests/utils/indexDB/IndexDBValueTestDataFactory.ts";
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

import { useRequestStrategies } from "@/composables/api/requestStrategies.ts";
import { HTTP_HEADER_CONTENT_TYPE } from "@/constants.ts";

const mockDefinitions = vi.hoisted(() => ({
  getItemFromIDB: vi.fn(),
  storeItem: vi.fn(),
  fetch: vi.fn(),
}));

vi.mock("@/composables/indexDB/indexDB.ts", () => ({
  useIndexDB: vi.fn().mockImplementation(() => ({
    getItemFromIDB: mockDefinitions.getItemFromIDB,
    storeItem: mockDefinitions.storeItem,
    setKey: vi.fn(),
  })),
}));

global.fetch = mockDefinitions.fetch;

const { generateRandomString } = useCommonTestDataFactory();
const { createIndexDBValue, prepareIndexDBValue } =
  useIndexDBValueTestDataFactory();

const mockedNow = new Date();

describe("requestStrategies.ts", () => {
  let unitUnderTest: ReturnType<typeof useRequestStrategies>;

  beforeAll(() => {
    vi.useFakeTimers({
      now: mockedNow,
    });
  });

  beforeEach(() => {
    unitUnderTest = useRequestStrategies();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
    vi.useRealTimers();
  });

  describe("unhandledFetch", () => {
    it("should_callFetchWithRequestAndReturnsItsResponse_when_calledWithCallbackOptions", async () => {
      const callbackOptions = createRouteHandlerCallbackOptions();

      const mockedResolvedValue = "mockedValue";
      mockDefinitions.fetch.mockResolvedValue(mockedResolvedValue);

      const result = await unitUnderTest.unhandledFetch(callbackOptions);

      expect(result).toBe(mockedResolvedValue);
    });
  });

  describe("offlineFirstGetRequestHandler", () => {
    it("should_returnResponseFromIndexDB_when_indexDBHasData", async () => {
      const callbackOptions = createRouteHandlerCallbackOptions();

      const mockedIndexDBItem = createIndexDBValue();
      mockDefinitions.getItemFromIDB.mockResolvedValue(mockedIndexDBItem);

      const result =
        await unitUnderTest.offlineFirstGetRequestHandler(callbackOptions);

      await verifyThatResponseMatchesIndexDBValue(result, mockedIndexDBItem);

      expect(mockDefinitions.getItemFromIDB.mock.calls).toStrictEqual([
        [callbackOptions.request.url],
      ]);
    });

    it.each(["response body as string", null])(
      "should_fetchDataAndStoreInIndexDB_when_indexDBHasNoDataAndResponseBodyIs'%s'",
      async (responseBody) => {
        await verifyThatFetchedResponseIsStored(
          responseBody,
          unitUnderTest.offlineFirstGetRequestHandler
        );
      }
    );

    it("should_returnResponseWithNotFound_when_fetchResponseWasNotOk", async () => {
      await verifyThatNotOkResponseDoesNotStoreAnythingAndReturnNotFoundResponse(
        unitUnderTest.offlineFirstGetRequestHandler
      );
    });

    it("should_returnResponseWithNotFound_when_fetchThrowsError", async () => {
      const callbackOptions = createRouteHandlerCallbackOptions();

      mockDefinitions.getItemFromIDB.mockResolvedValue(null);

      mockDefinitions.fetch.mockRejectedValue(
        new Error("mocked failed api call")
      );

      const result =
        await unitUnderTest.offlineFirstGetRequestHandler(callbackOptions);

      const expectedNotFoundResponse = new Response(null, {
        status: 404,
      });
      expect(result).toStrictEqual(expectedNotFoundResponse);
      expect(mockDefinitions.storeItem.mock.calls.length).toStrictEqual(0);
    });
  });

  describe("onlineFirstGetRequestHandler", () => {
    it.each(["response body as string", null])(
      "should_fetchAndStoreResponseIndexDB_when_fetchWasSuccessful",
      async (responseBody) => {
        await verifyThatFetchedResponseIsStored(
          responseBody,
          unitUnderTest.onlineFirstGetRequestHandler
        );
      }
    );

    it("should_returnIndexDBData_when_fetchWasNotOkAndIndexDBHasData", async () => {
      const callbackOptions = createRouteHandlerCallbackOptions();

      const mockedIndexDBItem = createIndexDBValue();
      mockDefinitions.getItemFromIDB.mockResolvedValue(mockedIndexDBItem);

      mockDefinitions.fetch.mockResolvedValue(
        new Response(null, { status: 400 })
      );

      const result =
        await unitUnderTest.onlineFirstGetRequestHandler(callbackOptions);

      await verifyThatResponseMatchesIndexDBValue(result, mockedIndexDBItem);

      expect(mockDefinitions.getItemFromIDB.mock.calls).toStrictEqual([
        [callbackOptions.request.url],
      ]);
    });

    it("should_returnIndexDBData_when_fetchFailedAndIndexDBHasData", async () => {
      const callbackOptions = createRouteHandlerCallbackOptions();

      const mockedIndexDBItem = createIndexDBValue();
      mockDefinitions.getItemFromIDB.mockResolvedValue(mockedIndexDBItem);

      mockDefinitions.fetch.mockRejectedValue(
        new Error("mocked failed api call")
      );

      const result =
        await unitUnderTest.onlineFirstGetRequestHandler(callbackOptions);

      await verifyThatResponseMatchesIndexDBValue(result, mockedIndexDBItem);

      expect(mockDefinitions.getItemFromIDB.mock.calls).toStrictEqual([
        [callbackOptions.request.url],
      ]);
    });

    it("should_returnNotFoundResponse_when_fetchWasNotOkAndIndexDBHasNoData", async () => {
      const callbackOptions = createRouteHandlerCallbackOptions();

      mockDefinitions.getItemFromIDB.mockResolvedValue(null);

      mockDefinitions.fetch.mockResolvedValue(
        new Response(null, { status: 400 })
      );

      const result =
        await unitUnderTest.onlineFirstGetRequestHandler(callbackOptions);

      const expectedNotFoundResponse = new Response(null, {
        status: 404,
      });
      expect(result).toStrictEqual(expectedNotFoundResponse);
      expect(mockDefinitions.storeItem.mock.calls.length).toStrictEqual(0);
    });
  });

  describe("postRequestHandler", () => {
    it("should_fetchRequestAndStoreRequestInIndexDB_when_fetchWasSuccessful", async () => {
      const requestBody = generateRandomString(50);
      const requestContentType = generateRandomString(10);
      const callbackOptions = createRouteHandlerCallbackOptions(
        "POST",
        requestBody,
        new Headers({
          "Content-Type": requestContentType,
        })
      );

      const mockedResponseBody = generateRandomString(40);
      const mockedContentType = generateRandomString(10);
      const mockedResponse = new Response(mockedResponseBody, {
        status: 200,
        headers: { "Content-Type": mockedContentType },
      });
      mockDefinitions.fetch.mockReturnValue(mockedResponse);

      const result = await unitUnderTest.postRequestHandler(callbackOptions);

      expect(result.status).toStrictEqual(200);
      expect(await result.text()).toStrictEqual(mockedResponseBody);
      expect(result.headers.get(HTTP_HEADER_CONTENT_TYPE)).toStrictEqual(
        mockedContentType
      );

      const expectedIndexDBValue = prepareIndexDBValue()
        .data(requestBody)
        .contentType(requestContentType)
        .dirty(false)
        .timestamp(mockedNow.getTime())
        .build();
      delete expectedIndexDBValue.httpStatus;
      expect(mockDefinitions.storeItem.mock.calls).toStrictEqual([
        [callbackOptions.request.url, expectedIndexDBValue],
      ]);
    });

    it("should_storeRequestAsDirtyInIndexDB_when_fetchFailed", async () => {
      const requestBody = generateRandomString(50);
      const requestContentType = generateRandomString(10);
      const callbackOptions = createRouteHandlerCallbackOptions(
        "POST",
        requestBody,
        new Headers({
          "Content-Type": requestContentType,
        })
      );

      mockDefinitions.fetch.mockRejectedValue(
        new Error("mocked api call failed")
      );

      const result = await unitUnderTest.postRequestHandler(callbackOptions);

      expect(result.status).toStrictEqual(404);
      expect(await result.text()).toStrictEqual("");

      const expectedIndexDBValue = prepareIndexDBValue()
        .data(requestBody)
        .contentType(requestContentType)
        .dirty(true)
        .timestamp(mockedNow.getTime())
        .build();
      delete expectedIndexDBValue.httpStatus;
      expect(mockDefinitions.storeItem.mock.calls).toStrictEqual([
        [callbackOptions.request.url, expectedIndexDBValue],
      ]);
    });

    it("should_storeRequestAsDirtyInIndexDB_when_fetchResponsesWithNotOk", async () => {
      const requestBody = generateRandomString(50);
      const requestContentType = generateRandomString(10);
      const callbackOptions = createRouteHandlerCallbackOptions(
        "POST",
        requestBody,
        new Headers({
          "Content-Type": requestContentType,
        })
      );

      mockDefinitions.fetch.mockResolvedValue(
        new Response(null, { status: 500 })
      );

      const result = await unitUnderTest.postRequestHandler(callbackOptions);

      expect(result.status).toStrictEqual(200);
      expect(await result.text()).toStrictEqual("");

      const expectedIndexDBValue = prepareIndexDBValue()
        .data(requestBody)
        .contentType(requestContentType)
        .dirty(true)
        .timestamp(mockedNow.getTime())
        .build();
      delete expectedIndexDBValue.httpStatus;
      expect(mockDefinitions.storeItem.mock.calls).toStrictEqual([
        [callbackOptions.request.url, expectedIndexDBValue],
      ]);
    });
  });

  function createRouteHandlerCallbackOptions(
    httpMethod: HTTPMethod = "GET",
    requestBody?: string,
    headers?: Headers
  ) {
    const url = new URL(`http://localhost/${generateRandomString(18)}`);
    const initHeaders = headers ?? new Headers();
    return {
      request: new Request(url, {
        method: httpMethod,
        headers: initHeaders,
        body: requestBody,
      }),
      url: url,
    } as RouteHandlerCallbackOptions;
  }

  async function verifyThatFetchedResponseIsStored(
    responseBody: string | null,
    functionUnderTest: (
      options: RouteHandlerCallbackOptions
    ) => Promise<Response>
  ) {
    const callbackOptions = createRouteHandlerCallbackOptions();

    mockDefinitions.getItemFromIDB.mockResolvedValue(null);

    const mockedResponseBody = responseBody;
    const mockedHttpStatus = 200;
    const mockedContentTypeHeader = "application/json";
    const mockedResponse = new Response(mockedResponseBody, {
      status: mockedHttpStatus,
      headers: new Headers({
        "Content-Type": mockedContentTypeHeader,
      }),
    });
    mockDefinitions.fetch.mockResolvedValue(mockedResponse);

    const result = await functionUnderTest(callbackOptions);

    expect(result).toBe(mockedResponse);

    const expectedIndexDBValue = prepareIndexDBValue()
      .httpStatus(mockedHttpStatus)
      .contentType(mockedContentTypeHeader)
      .data(mockedResponseBody)
      .build();
    //required cause builder creates dirty and timestamp with value or undefined but stored item does not have this property
    delete expectedIndexDBValue.dirty;
    delete expectedIndexDBValue.timestamp;
    expect(mockDefinitions.storeItem.mock.calls).toStrictEqual([
      [callbackOptions.request.url, expectedIndexDBValue],
    ]);
  }

  async function verifyThatNotOkResponseDoesNotStoreAnythingAndReturnNotFoundResponse(
    functionUnderTest: (
      options: RouteHandlerCallbackOptions
    ) => Promise<Response>
  ) {
    const callbackOptions = createRouteHandlerCallbackOptions();

    mockDefinitions.getItemFromIDB.mockResolvedValue(null);

    const mockedResponseBody = "response was not ok";
    const mockedHttpStatus = 400;
    const mockedContentTypeHeader = "application/json";
    const mockedResponse = new Response(mockedResponseBody, {
      status: mockedHttpStatus,
      headers: new Headers({
        "Content-Type": mockedContentTypeHeader,
      }),
    });
    mockDefinitions.fetch.mockResolvedValue(mockedResponse);

    const result = await functionUnderTest(callbackOptions);

    const expectedNotFoundResponse = new Response(null, {
      status: 404,
    });
    expect(result).toStrictEqual(expectedNotFoundResponse);
    expect(mockDefinitions.storeItem.mock.calls.length).toStrictEqual(0);
  }

  async function verifyThatResponseMatchesIndexDBValue(
    response: Response,
    indexDBValue: IndexDBValue
  ) {
    expect(response.status).toStrictEqual(indexDBValue.httpStatus);
    expect(response.statusText).toStrictEqual("fetched from idb");
    expect(response.headers.get(HTTP_HEADER_CONTENT_TYPE)).toStrictEqual(
      indexDBValue.contentType
    );
    expect(await response.text()).toStrictEqual(indexDBValue.data);
  }
});
