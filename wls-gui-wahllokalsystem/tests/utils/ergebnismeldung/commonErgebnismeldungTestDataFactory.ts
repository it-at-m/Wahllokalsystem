import type { BezirkUndWahlIDStapelartDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { BezirkUndWahlIDStapelartDTOStapelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();

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
    stapelArt: StapelArtEnum
  ): BezirkUndWahlIDStapelArt {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
      stapelArt: stapelArt,
    };
  }

  return {
    createBezirkUndWahlIDStapelartDTO,
    createBezirkUndWahlIDStapelart,
  };
}
