import type { WahlbriefdatenDTO } from "@/api/wls-clients/generated-briefwahl-api";
import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString, generateRandomNumber, generateRandomDate } =
  useCommonTestDataFactory();

export function useWahlbriefdatenTestDataFactory() {
  function createWahlbriefdaten(): Wahlbriefdaten {
    return {
      wahlbriefe: generateRandomNumber(2),
      verzeichnisseUngueltige: generateRandomNumber(2),
      nachtraege: generateRandomNumber(2),
      nachtraeglichUeberbrachte: generateRandomNumber(2),
      zeitNachtraeglichUeberbrachte: generateRandomDate(),
    };
  }

  function createWahlbriefdatenDTO(): WahlbriefdatenDTO {
    return {
      wahlbezirkID: generateRandomString(10),
      wahlbriefe: generateRandomNumber(2),
      verzeichnisseUngueltige: generateRandomNumber(2),
      nachtraege: generateRandomNumber(2),
      nachtraeglichUeberbrachte: generateRandomNumber(2),
      zeitNachtraeglichUeberbrachte: generateRandomDate().toISOString(),
    };
  }

  function prepareWahlbriefdaten(): Builder<Wahlbriefdaten> {
    return proxyBuilder<Wahlbriefdaten>(createWahlbriefdaten());
  }

  function prepareWahlbriefdatenDTO(): Builder<WahlbriefdatenDTO> {
    return proxyBuilder<WahlbriefdatenDTO>(createWahlbriefdatenDTO());
  }

  return {
    createWahlbriefdaten,
    createWahlbriefdatenDTO,
    prepareWahlbriefdaten,
    prepareWahlbriefdatenDTO,
  };
}
