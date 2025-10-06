import type {
  BezirkUndWahlIDStapelartDTO,
  BezirkUndWahlID as BezirkUndWahlIDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { BezirkUndWahlIDStapelartDTOStapelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { generateRandomString, getRandomItem } = useCommonTestDataFactory();

export function useCommonErgebnismeldungTestDataFactory() {
  function createBezirkUndWahlID(): BezirkUndWahlID {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
    };
  }

  function createBezirkUndWahlIDDTO(): BezirkUndWahlIDTO {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
    };
  }

  function createBezirkUndWahlIDStapelartDTO(
    stapelArt?: BezirkUndWahlIDStapelartDTOStapelartEnum
  ): BezirkUndWahlIDStapelartDTO {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
      stapelart: stapelArt ?? getRandomItem(Object.values(StapelArtEnum)),
    };
  }

  function createBezirkUndWahlIDStapelart(
    stapelArt?: StapelArtEnum
  ): BezirkUndWahlIDStapelArt {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
      stapelArt: stapelArt ?? getRandomItem(Object.values(StapelArtEnum)),
    };
  }

  function prepareBezirkUndWahlID(): Builder<BezirkUndWahlID> {
    return proxyBuilder<BezirkUndWahlID>(createBezirkUndWahlID());
  }

  function prepareBezirkUndWahlIDDTO(): Builder<BezirkUndWahlIDTO> {
    return proxyBuilder<BezirkUndWahlIDTO>(createBezirkUndWahlIDDTO());
  }

  function prepareBezirkUndWahlIDStapelart(): Builder<BezirkUndWahlIDStapelArt> {
    return proxyBuilder<BezirkUndWahlIDStapelArt>(
      createBezirkUndWahlIDStapelart()
    );
  }

  function prepareBezirkUndWahlIDStapelartDTO(): Builder<BezirkUndWahlIDStapelartDTO> {
    return proxyBuilder<BezirkUndWahlIDStapelartDTO>(
      createBezirkUndWahlIDStapelartDTO()
    );
  }

  return {
    createBezirkUndWahlID,
    createBezirkUndWahlIDDTO,
    createBezirkUndWahlIDStapelartDTO,
    createBezirkUndWahlIDStapelart,
    prepareBezirkUndWahlID,
    prepareBezirkUndWahlIDDTO,
    prepareBezirkUndWahlIDStapelart,
    prepareBezirkUndWahlIDStapelartDTO,
  };
}
