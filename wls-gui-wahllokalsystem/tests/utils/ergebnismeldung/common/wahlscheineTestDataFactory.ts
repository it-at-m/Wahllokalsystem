import type { WahlscheineDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Wahlscheine } from "@/types/ergebnismeldung/common/Wahlscheine.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";

const { generateRandomNumber } = useCommonTestDataFactory();
const { createBezirkUndWahlID, createBezirkUndWahlIDDTO } =
  useCommonErgebnismeldungTestDataFactory();

export function useWahlscheineTestDataFactory() {
  function createWahlscheine(): Wahlscheine {
    return {
      bezirkUndWahlID: createBezirkUndWahlID(),
      stimmabgabevermerke: generateRandomNumber(10),
    };
  }

  function createWahlscheineDTO(): WahlscheineDTO {
    return {
      bezirkUndWahlID: createBezirkUndWahlIDDTO(),
      stimmabgabevermerke: generateRandomNumber(10),
    };
  }

  function prepareWahlscheine(): Builder<Wahlscheine> {
    return proxyBuilder<Wahlscheine>(createWahlscheine());
  }

  function prepareWahlscheineDTO(): Builder<WahlscheineDTO> {
    return proxyBuilder<WahlscheineDTO>(createWahlscheineDTO());
  }

  return {
    createWahlscheine,
    prepareWahlscheine,
    createBezirkUndWahlID,
    createBezirkUndWahlIDDTO,
    createWahlscheineDTO,
    prepareWahlscheineDTO,
  };
}
