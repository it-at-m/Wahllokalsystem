import type {
  WaehlerverzeichnisDTO,
  WaehlerverzeichnisWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
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

  function createWaehlerverzeichnisDTO(): WaehlerverzeichnisDTO {
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

  function createWaehlerverzeichnisWriteDTO(): WaehlerverzeichnisWriteDTO {
    return {
      nachtraeglicheBerichtigung: generateRandomBoolean(),
      verzeichnisLagVor: generateRandomBoolean(),
      berichtigungVorBeginnDerAbstimmung: generateRandomBoolean(),
      mitteilungUeberUngueltigeWahlscheineErhalten: generateRandomBoolean(),
    };
  }

  function preparePflegeWaehlerverzeichnis() {
    return proxyBuilder<PflegeWaehlerverzeichnis>(
      createPflegeWaehlerverzeichnis()
    );
  }

  return {
    createPflegeWaehlerverzeichnis,
    createWaehlerverzeichnisDTO,
    createWaehlerverzeichnisWriteDTO,
    preparePflegeWaehlerverzeichnis,
  };
}
