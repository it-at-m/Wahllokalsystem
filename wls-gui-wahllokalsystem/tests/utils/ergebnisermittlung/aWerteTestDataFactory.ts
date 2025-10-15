import type { AWerteDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { AWerte } from "@/types/ergebnisermittlung/AWerte.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useAWerteTestDataFactory() {
  function createAWerte(): AWerte {
    return {
      bezirkUndWahlID: {
        wahlID: generateRandomString(10),
        wahlbezirkID: generateRandomString(10),
      },
      a1: generateRandomNumber(4),
      a2: generateRandomNumber(4),
    };
  }

  function createAWerteDTO(): AWerteDTO {
    return {
      bezirkUndWahlID: {
        wahlID: generateRandomString(10),
        wahlbezirkID: generateRandomString(10),
      },
      a1: generateRandomNumber(4),
      a2: generateRandomNumber(4),
    };
  }

  function prepareAWerte() {
    return proxyBuilder<AWerte>(createAWerte());
  }

  function prepareAWerteDTO() {
    return proxyBuilder<AWerteDTO>(createAWerteDTO());
  }

  return {
    createAWerte,
    createAWerteDTO,
    prepareAWerte,
    prepareAWerteDTO,
  };
}
