import type { AsyncProgressDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { AWerteInitProgress } from "@/types/aWerte/AWerteInitProgress.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const {
  generateRandomBoolean,
  generateRandomDateAsString,
  generateRandomNumber,
} = useCommonTestDataFactory();

export default function useAWerteTestDataFactory() {
  function generateAsyncProgressDTO(): AsyncProgressDTO {
    return {
      aWerteFinished: generateRandomNumber(4),
      aWerteNext: `next ${generateRandomNumber(4)}`,
      aWerteTotal: generateRandomNumber(4),
      aWerteLoadingActive: generateRandomBoolean(),
      lastFinishTime: generateRandomDateAsString(),
      lastStartTime: generateRandomDateAsString(),
    };
  }

  function generateAWerteInitProgress(): AWerteInitProgress {
    return {
      total: generateRandomNumber(4),
      active: generateRandomBoolean(),
      lastFinishTime: generateRandomDateAsString(),
      lastStartTime: generateRandomDateAsString(),
    };
  }

  return {
    generateAsyncProgressDTO,
    generateAWerteInitProgress,
  };
}
