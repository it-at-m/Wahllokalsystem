import type { AsyncProgressDTO } from "@/api/wls-clients/generated-basisdaten-api";
import type { BasisdatenInitProgress } from "@/types/basisdaten/BasisdatenInitProgress.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const {
  generateRandomDateAsString,
  generateRandomDateTimeAsString,
  generateRandomNumber,
  generateRandomBoolean,
} = useCommonTestDataFactory();

export default function useBasisdatenTestDataFactory() {
  function createAsyncProgressDTOComplete(): AsyncProgressDTO {
    return {
      lastStartTime: generateRandomDateTimeAsString(),
      lastFinishTime: generateRandomDateTimeAsString(),
      forWahltag: generateRandomDateAsString(),
      wahlNummer: `${generateRandomNumber(1)}`,
      referendumVorlagenFinished: generateRandomNumber(4),
      referendumVorlagenNext: `next ${generateRandomNumber(10)}`,
      referendumVorlagenTotal: generateRandomNumber(4),
      referendumLoadingActive: generateRandomBoolean(),
      wahlvorschlaegeLoadingActive: generateRandomBoolean(),
      wahlvorschlaegeNext: `next ${generateRandomNumber(10)}`,
      wahlvorschlaegeTotal: generateRandomNumber(4),
      wahlvorschlageFinished: generateRandomNumber(4),
    };
  }

  function createBasisdatenInitProgressComplete(): BasisdatenInitProgress {
    return {
      lastStartTime: generateRandomDateTimeAsString(),
      referendumvorlagen: {
        active: generateRandomBoolean(),
        total: generateRandomNumber(4),
        next: `next ${generateRandomNumber(10)}`,
        finished: generateRandomNumber(4),
      },
      forWahltag: generateRandomDateAsString(),
      wahlNummer: `${generateRandomNumber(1)}`,
      lastFinishTime: generateRandomDateTimeAsString(),
      wahlvorschlaege: {
        active: generateRandomBoolean(),
        total: generateRandomNumber(4),
        next: `next ${generateRandomNumber(10)}`,
        finished: generateRandomNumber(4),
      },
    };
  }

  return {
    createAsyncProgressDTOComplete,
    createBasisdatenInitProgressComplete,
  };
}
