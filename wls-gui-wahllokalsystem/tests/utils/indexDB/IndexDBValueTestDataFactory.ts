import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const {
  generateRandomString,
  generateRandomBoolean,
  generateRandomNumber,
  getRandomItem,
} = useCommonTestDataFactory();

export function useIndexDBValueTestDataFactory() {
  //response requires a range; but not all values in that range
  //some legal codes are removed, because they require empty body
  //init["status"] must be in the range of 200 to 599, inclusive.
  //     at initializeResponse (node:internal/deps/undici/undici:9338:15)
  const allowedHttpResponseStatusCodes = [
    200, 201, 202, 400, 401, 402, 403, 404, 409, 500,
  ];

  function createIndexDBValue(): IndexDBValue {
    return {
      data: generateRandomString(20),
      contentType: generateRandomString(10),
      dirty: generateRandomBoolean(),
      httpStatus: getRandomItem(allowedHttpResponseStatusCodes),
      timestamp: generateRandomNumber(6),
    };
  }

  function prepareIndexDBValue() {
    return proxyBuilder<IndexDBValue>(createIndexDBValue());
  }

  return {
    createIndexDBValue,
    prepareIndexDBValue,
  };
}
