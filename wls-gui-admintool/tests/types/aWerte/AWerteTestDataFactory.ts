import type { AsyncProgressDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { AWerteInitProgress } from "@/types/aWerte/AWerteInitProgress.ts";
import type { Builder } from "@tests/utils/common/Builder.ts";

import { proxyBuilder } from "@tests/utils/common/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const {
  generateRandomBoolean,
  generateRandomDateTimeAsString,
  generateRandomNumber,
} = useCommonTestDataFactory();

export function useAWerteTestDataFactory() {
  function createAsyncProgressDTOComplete(): AsyncProgressDTO {
    return {
      aWerteFinished: generateRandomNumber(4),
      aWerteNext: `next ${generateRandomNumber(4)}`,
      aWerteTotal: generateRandomNumber(4),
      aWerteLoadingActive: generateRandomBoolean(),
      lastFinishTime: generateRandomDateTimeAsString(),
      lastStartTime: generateRandomDateTimeAsString(),
    };
  }

  function createAWerteInitProgressComplete(): AWerteInitProgress {
    return {
      total: generateRandomNumber(4),
      active: generateRandomBoolean(),
      lastFinishTime: generateRandomDateTimeAsString(),
      lastStartTime: generateRandomDateTimeAsString(),
      finished: generateRandomNumber(4),
      next: `${generateRandomNumber(10)}`,
    };
  }

  function prepareAWerteInitProgress(): Builder<AWerteInitProgress> {
    return proxyBuilder<AWerteInitProgress>(createAWerteInitProgressComplete());
  }

  return {
    createAsyncProgressDTOComplete,
    createAWerteInitProgressComplete,
    prepareAWerteInitProgress,
  };
}
