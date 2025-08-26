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
      bezirkUndWahlID: createBezirkUndWahlIDDto(
        generateRandomString(10),
        generateRandomString(10)
      ),
      anzahlWaehler: generateRandomNumber(3),
    };
  }

  function createStimmzettelumschlaege(
    overrides: Partial<Stimmzettelumschlaege> = {}
  ): Stimmzettelumschlaege {
    return {
      anzahlWaehler: generateRandomNumber(3),
      ...overrides,
    };
  }

  function createBezirkUndWahlIDDto(wahlID: string, wahlbezirkID: string) {
    const dto: BezirkUndWahlID = {
      wahlID: wahlID,
      wahlbezirkID: wahlbezirkID,
    };
    return dto;
  }

  return {
    createStimmzettelumschlaegeDto,
    createStimmzettelumschlaege,
    createBezirkUndWahlIDDto,
  };
}
