import type { AxiosResponse } from "axios";

import { describe, expect, it } from "vitest";

import { useApiUtils } from "@/composables/common/apiUtils.ts";

const unitUnderTest = useApiUtils();

describe("apiUtils.ts", () => {
  describe("useApiUtils", () => {
    describe("returnUndefinedOnStatus204OrElseDate", () => {
      it("should_returnUndefined_when_statusIs204", () => {
        const result = unitUnderTest.returnUndefinedOnStatus204OrElseDate(
          createAxiosResponse(204, "hello world")
        );

        expect(result).toStrictEqual(undefined);
      });

      it("should_returnData_when_statusIsNot204", () => {
        const result = unitUnderTest.returnUndefinedOnStatus204OrElseDate(
          createAxiosResponse(200, "hello world")
        );

        expect(result).toStrictEqual("hello world");
      });
    });
  });
});

function createAxiosResponse<T>(status: number, data: T): AxiosResponse<T> {
  return {
    status: status,
    data: data,
  } as AxiosResponse<T>;
}
