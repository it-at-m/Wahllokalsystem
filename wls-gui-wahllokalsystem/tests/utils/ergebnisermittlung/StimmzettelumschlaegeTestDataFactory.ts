import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useStimmzettelumschlaegeTestDataFactory() {
  function createStimmzettelumschlaegeDto(): StimmzettelumschlaegeDTO {
    return {
      bezirkUndWahlID: _createBezirkUndWahlIDDto(),
      anzahlWaehler: generateRandomNumber(3),
    };
  }

  function createStimmzettelumschlaege(): Stimmzettelumschlaege {
    return {
      anzahlWaehler: generateRandomNumber(3),
    };
  }

  function _createBezirkUndWahlIDDto() {
    const dto: BezirkUndWahlID = {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(10),
    };
    return dto;
  }

  return {
    createStimmzettelumschlaegeDto,
    createStimmzettelumschlaege,
  };
}
