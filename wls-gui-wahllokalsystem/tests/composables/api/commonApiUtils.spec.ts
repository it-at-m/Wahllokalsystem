import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { useIndexDBValueTestDataFactory } from "@tests/utils/indexDB/IndexDBValueTestDataFactory.ts";
import { HttpStatusCode } from "axios";
import { describe, expect, it } from "vitest";

import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { HTTP_HEADER_CONTENT_TYPE } from "@/constants.ts";

const { createIndexDBValue, prepareIndexDBValue } =
  useIndexDBValueTestDataFactory();

describe("commonApiUtils.ts", () => {
  const { createAxiosResponse } = useAxiosTestDataFactory();
  const {
    isTextContext,
    isPdfContext,
    getNullOn204OrElseResponseData,
    createResponseInternalServerErrorWithoutResponseBody,
    createResponseOfIndexDBValue,
    createResponseOkWithoutResponseBody,
    createResponseNotFoundWithoutResponseBody,
  } = useCommonApiUtils();

  const alwaysInvalidContentTypes = [
    "application/msword",
    "application/xml",
    "application/zip",
    "image/",
    "audio/",
    "video/",
  ];

  describe("getNullOn204OrElseResponseData", () => {
    it.each([205, 203])(
      "should_returnResponseData_when_responseStatusIsNot204BecauseItIs%i",
      (statusCode) => {
        const responseData = "responseData";
        const result = getNullOn204OrElseResponseData(
          createAxiosResponse({ status: statusCode, data: responseData })
        );

        expect(result).toStrictEqual(responseData);
      }
    );

    it.each([
      { responseData: "text", descriptionSuffix: "AndDataIsText" },
      { responseData: null, descriptionSuffix: "AndDataIsNull" },
      { responseData: {}, descriptionSuffix: "AndDataIsEmptyObject" },
      { responseData: undefined, descriptionSuffix: "AndDataIsUndefined" },
    ])(
      "should_returnNull_when_statusCodeIs204$descriptionSuffix",
      (testCaseParameter) => {
        const responseData = testCaseParameter.responseData;
        const result = getNullOn204OrElseResponseData(
          createAxiosResponse({ status: 204, data: responseData })
        );

        expect(result).toStrictEqual(null);
      }
    );
  });

  describe("createResponseOfIndexDBValue", () => {
    it("should_createResponseWithIndexDBValueHttpStatus_when_indexDBValuesHasHttpStatusCode", async () => {
      const indexDBValue = createIndexDBValue();

      const result = createResponseOfIndexDBValue(indexDBValue);

      //have to compare field by field cause strictEqual won't work with response
      expect(result.status).toEqual(indexDBValue.httpStatus);
      expect(result.statusText).toEqual("fetched from idb");
      expect(result.headers.get(HTTP_HEADER_CONTENT_TYPE)).toEqual(
        indexDBValue.contentType
      );
      expect(await result.text()).toEqual(indexDBValue.data);
    });

    it("should_createResponseWithFallbackHttpStatusCode_when_indexDBValuesHasNoHttpStatusCode", async () => {
      const indexDBValue = prepareIndexDBValue().httpStatus(undefined).build();

      const fallbackHttpStatusCode = 401;
      const result = createResponseOfIndexDBValue(
        indexDBValue,
        fallbackHttpStatusCode
      );

      //have to compare field by field cause strictEqual won't work with response
      expect(result.status).toEqual(fallbackHttpStatusCode);
      expect(result.statusText).toEqual("fetched from idb");
      expect(result.headers.get(HTTP_HEADER_CONTENT_TYPE)).toEqual(
        indexDBValue.contentType
      );
      expect(await result.text()).toEqual(indexDBValue.data);
    });

    it.each(["", "    ", "more data"])(
      "should_throwError_when_httpStatusCodeIs204AndDataIs'%s'",
      async (testcaseArgument) => {
        const indexDBValue = prepareIndexDBValue()
          .httpStatus(204)
          .data(testcaseArgument)
          .build();

        expect(() => createResponseOfIndexDBValue(indexDBValue)).toThrowError();
      }
    );
  });

  describe("createResponseInternalServerErrorWithoutResponseBody", () => {
    it("should_createResponseWithoutBodyAndWithStatusInternalServerError_when_called", async () => {
      const result = createResponseInternalServerErrorWithoutResponseBody();
      expect(result).toStrictEqual(
        new Response(null, { status: HttpStatusCode.InternalServerError })
      );
    });
  });

  describe("createResponseOkWithoutResponseBody", () => {
    it("should_createResponseWithoutBodyAndWithStatusOk_when_called", async () => {
      const result = createResponseOkWithoutResponseBody();
      expect(result).toStrictEqual(
        new Response(null, { status: HttpStatusCode.Ok })
      );
    });
  });

  describe("createResponseNotFoundWithoutResponseBody", () => {
    it("should_createResponseWithoutBodyAndWithStatusNotFound_when_called", async () => {
      const result = createResponseNotFoundWithoutResponseBody();
      expect(result).toStrictEqual(
        new Response(null, { status: HttpStatusCode.NotFound })
      );
    });
  });

  describe("isTextContext", () => {
    it.each(["application/json", "text/"])(
      "should_returnTrue_whenGivenValidContentType'%s'",
      (contentType) => {
        expect(isTextContext(contentType)).toStrictEqual(true);
      }
    );

    it.each(["application/pdf", ...alwaysInvalidContentTypes])(
      "should_returnFalse_whenGivenInvalidContentType'%s'",
      (contentType) => {
        expect(isTextContext(contentType)).toStrictEqual(false);
      }
    );
  });

  describe("isPdfContext", () => {
    it.each(["application/pdf"])(
      "should_returnTrue_whenGivenValidContentType'%s'",
      (contentType) => {
        expect(isPdfContext(contentType)).toStrictEqual(true);
      }
    );

    it.each(["application/json", "text/", ...alwaysInvalidContentTypes])(
      "should_returnFalse_whenGivenInvalidContentType'%s'",
      (contentType) => {
        expect(isPdfContext(contentType)).toStrictEqual(false);
      }
    );
  });
});
