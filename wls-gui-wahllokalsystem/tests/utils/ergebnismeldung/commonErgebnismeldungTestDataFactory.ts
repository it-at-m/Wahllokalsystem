import type { BezirkUndWahlIDStapelartDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { BezirkUndWahlIDStapelartDTOStapelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { generateRandomString, getRandomItem } = useCommonTestDataFactory();

export function useCommonErgebnismeldungTestDataFactory() {
  function createBezirkUndWahlIDStapelartDTO(
    stapelArt: BezirkUndWahlIDStapelartDTOStapelartEnum
  ): BezirkUndWahlIDStapelartDTO {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
      stapelart: stapelArt,
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

  function prepareBezirkUndWahlIDStapelart(): Builder<BezirkUndWahlIDStapelArt> {
    return proxyBuilder<BezirkUndWahlIDStapelArt>(
      createBezirkUndWahlIDStapelart()
    );
  }

  return {
    createBezirkUndWahlIDStapelartDTO,
    createBezirkUndWahlIDStapelart,
    prepareBezirkUndWahlIDStapelart,
  };
}
