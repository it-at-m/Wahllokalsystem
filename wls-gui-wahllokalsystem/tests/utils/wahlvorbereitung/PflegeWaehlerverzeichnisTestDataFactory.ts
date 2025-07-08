import type { WaehlerverzeichnisDTO } from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomBoolean, generateRandomNumber, generateRandomString } =
  useCommonTestDataFactory();

export function usePflegeWaehlerverzeichnisTestDataFactory() {
  function createPflegeWaehlerverzeichnis(): PflegeWaehlerverzeichnis {
    return {
      nachtraeglicheBerichtigung: generateRandomBoolean(),
      waehlerverzeichnisUnchanged: generateRandomBoolean(),
      mitteilungUeberUngueltigeWahlscheineErhalten: generateRandomBoolean(),
    };
  }

  function createWaehlerverzeichnisWriteDTO(): WaehlerverzeichnisDTO {
    return {
      nachtraeglicheBerichtigung: generateRandomBoolean(),
      verzeichnisLagVor: generateRandomBoolean(),
      berichtigungVorBeginnDerAbstimmung: generateRandomBoolean(),
      bezirkIDUndWaehlerverzeichnisNummer: {
        waehlerverzeichnisNummer: generateRandomNumber(3),
        wahlbezirkID: generateRandomString(10),
      },
      mitteilungUeberUngueltigeWahlscheineErhalten: generateRandomBoolean(),
    };
  }

  return { createPflegeWaehlerverzeichnis, createWaehlerverzeichnisWriteDTO };
}
