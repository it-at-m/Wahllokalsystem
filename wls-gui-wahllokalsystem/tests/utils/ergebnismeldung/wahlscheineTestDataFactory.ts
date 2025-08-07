import type {
  BezirkUndWahlID,
  WahlscheineDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlIDModel } from "@/types/ereignismeldung/BezirkUndWahlIDModel.ts";
import type { Wahlscheine } from "@/types/ereignismeldung/Wahlscheine.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useWahlscheineTestDataFactory() {
  function createWahlscheine(): Wahlscheine {
    return {
      bezirkUndWahlID: createBezirkUndWahlIDModel(),
      stimmabgabevermerke: generateRandomNumber(10),
    };
  }

  function createBezirkUndWahlIDModel(): BezirkUndWahlIDModel {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
    };
  }

  function createWahlscheineDTO(): WahlscheineDTO {
    return {
      bezirkUndWahlID: createBezirkUndWahlIDModel(),
      stimmabgabevermerke: generateRandomNumber(10),
    };
  }

  function createBezirkUndWahlIDDTO(): BezirkUndWahlID {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
    };
  }

  function prepareWahlscheine(): Builder<Wahlscheine> {
    return proxyBuilder<Wahlscheine>(createWahlscheine());
  }

  function prepareBezirkUndWahlIDModel(): Builder<BezirkUndWahlIDModel> {
    return proxyBuilder<BezirkUndWahlIDModel>(createBezirkUndWahlIDModel());
  }

  function prepareWahlscheineDTO(): Builder<WahlscheineDTO> {
    return proxyBuilder<Wahlscheine>(createWahlscheine());
  }

  function prepareBezirkUndWahlIDDTO(): Builder<BezirkUndWahlID> {
    return proxyBuilder<BezirkUndWahlIDModel>(createBezirkUndWahlIDModel());
  }

  return {
    createWahlscheine,
    prepareWahlscheine,
    prepareBezirkUndWahlIDModel,
    createBezirkUndWahlIDModel,
    createBezirkUndWahlIDDTO,
    createWahlscheineDTO,
    prepareBezirkUndWahlIDDTO,
    prepareWahlscheineDTO,
  };
}
