import { useAxiosTestDataFactory } from "@tests/utils/common/AxiosTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";

describe("commonApiUtils.ts", () => {
  const { createAxiosResponse } = useAxiosTestDataFactory();
  const { getNullOn204OrElseResponseData } = useCommonApiUtils();

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
});
