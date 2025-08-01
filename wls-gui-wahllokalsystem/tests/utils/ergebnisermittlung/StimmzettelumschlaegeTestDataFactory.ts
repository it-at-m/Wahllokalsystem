import type {
  BezirkUndWahlID,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const {
  generateRandomString,
  generateRandomNumber,
  generateRandomDateTimeAsString,
} = useCommonTestDataFactory();

export function useStimmzettelumschlaegeTestDataFactory() {
  function createStimmzettelumschlaegeDto(): StimmzettelumschlaegeDTO {
    const dto: StimmzettelumschlaegeDTO = {
      bezirkUndWahlID: _createBezirkUndWahlIDDto(),
      urneneroeffnungsUhrzeit: generateRandomDateTimeAsString(),
      anzahlWaehler: generateRandomNumber(3),
      anzahlWaehler2: generateRandomNumber(3),
    };
    return dto;
  }

  function createStimmzettelumschlaege(): Stimmzettelumschlaege {
    const model: Stimmzettelumschlaege = {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(10),
      urneneroeffnungsUhrzeit: generateRandomDateTimeAsString(),
      anzahlWaehler: generateRandomNumber(3),
      anzahlWaehler2: generateRandomNumber(3),
    };
    return model;
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
