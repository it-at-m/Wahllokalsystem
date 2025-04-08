import type { AsyncProgressDTO } from "@/api/wls-clients/generated-basisdaten-api";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const {
  generateRandomDateAsString,
  generateRandomDateTimeAsString,
  generateRandomNumber,
  generateRandomBoolean,
} = useCommonTestDataFactory();

export default function useBasisdatenTestDataFactory() {
  function createCompleteAsyncProgressDTO(): AsyncProgressDTO {
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

  return {
    createCompleteAsyncProgressDTO,
  };
}
