import type {
  BezirkUndWahlID as BezirkUndWahlIDTO,
  WahlscheineDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { Wahlscheine } from "@/types/ergebnismeldung/Wahlscheine.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useWahlscheineTestDataFactory() {
  function createWahlscheine(): Wahlscheine {
    return {
      bezirkUndWahlID: createBezirkUndWahlID(),
      stimmabgabevermerke: generateRandomNumber(10),
    };
  }

  function createBezirkUndWahlID(): BezirkUndWahlID {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
    };
  }

  function createWahlscheineDTO(): WahlscheineDTO {
    return {
      bezirkUndWahlID: createBezirkUndWahlIDDTO(),
      stimmabgabevermerke: generateRandomNumber(10),
    };
  }

  function createBezirkUndWahlIDDTO(): BezirkUndWahlIDTO {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
    };
  }

  function prepareWahlscheine(): Builder<Wahlscheine> {
    return proxyBuilder<Wahlscheine>(createWahlscheine());
  }

  function prepareBezirkUndWahlID(): Builder<BezirkUndWahlID> {
    return proxyBuilder<BezirkUndWahlID>(createBezirkUndWahlID());
  }

  function prepareWahlscheineDTO(): Builder<WahlscheineDTO> {
    return proxyBuilder<WahlscheineDTO>(createWahlscheineDTO());
  }

  function prepareBezirkUndWahlIDDTO(): Builder<BezirkUndWahlIDTO> {
    return proxyBuilder<BezirkUndWahlIDTO>(createBezirkUndWahlIDDTO());
  }

  return {
    createWahlscheine,
    prepareWahlscheine,
    prepareBezirkUndWahlID,
    createBezirkUndWahlID,
    createBezirkUndWahlIDDTO,
    createWahlscheineDTO,
    prepareBezirkUndWahlIDDTO,
    prepareWahlscheineDTO,
  };
}
