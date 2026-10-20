import type { SyncronizeDataResult } from "@/types/indexDB/SyncronizeDataResult.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomNumber } = useCommonTestDataFactory();

export function useSynchronizeDataResultTestDataFactory() {
  function createSynchronizeDataResult(): SyncronizeDataResult {
    return {
      numberOfTasksRan: generateRandomNumber(3),
      numberOfTasksFailed: generateRandomNumber(3),
      numberOfTasksSucceeded: generateRandomNumber(3),
      numberOfDirtyTasksRemaining: generateRandomNumber(3),
    };
  }

  function prepareSynchronizeDataResult() {
    return proxyBuilder<SyncronizeDataResult>(createSynchronizeDataResult());
  }

  return {
    createSynchronizeDataResult,
    prepareSynchronizeDataResult,
  };
}
