import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useBWerteTestDataFactory() {
  function createBWerte(): BWerte {
    const b1 = generateRandomNumber(4);
    const b2 = generateRandomNumber(4);
    return {
      bezirkUndWahlID: {
        wahlID: generateRandomString(10),
        wahlbezirkID: generateRandomString(10),
      },
      b: b1 + b2,
      b1: b1,
      b2: b2,
    };
  }

  function prepareBWerte() {
    return proxyBuilder<BWerte>(createBWerte());
  }

  return {
    createBWerte,
    prepareBWerte,
  };
}
