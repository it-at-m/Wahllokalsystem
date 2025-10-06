import type {
  ErgebnisDTO,
  ErgebnisseDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/commonErgebnismeldungTestDataFactory.ts";

import { BezirkUndWahlIDStapelartDTOStapelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { generateRandomString, generateRandomNumber, getRandomItem } =
  useCommonTestDataFactory();
const { createBezirkUndWahlIDStapelart, createBezirkUndWahlIDStapelartDTO } =
    useCommonErgebnismeldungTestDataFactory();

export function useErgebnisseTestDataFactory() {
  function createBezirkUndWahlIDStapelart(
    stapelArt?: StapelArtEnum
  ): BezirkUndWahlIDStapelArt {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
      stapelArt: stapelArt ?? getRandomItem(Object.values(StapelArtEnum)),
    };
  }

  function createErgebnisseDTO(): ErgebnisseDTO {
    return {
      bezirkUndWahlIDStapelart: createBezirkUndWahlIDStapelartDTO(
        BezirkUndWahlIDStapelartDTOStapelartEnum.ObwA
      ),
      ergebnisse: [createErgebnisDTO(), createErgebnisDTO()],
    };
  }

  function createErgebnisDTO(
    overrides: Partial<ErgebnisDTO> = {}
  ): ErgebnisDTO {
    return {
      ergebnis: generateRandomNumber(2),
      ...overrides,
    };
  }

  function createErgebnisse(): Ergebnisse {
    return {
      bezirkUndWahlIDStapelart: createBezirkUndWahlIDStapelart(
        StapelArtEnum.ObwA
      ),
      ergebnisse: [createErgebnis(), createErgebnis()],
    };
  }

  function createErgebnis(): Ergebnis {
    return {
      wahlvorschlagID: null,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl: null,
      ergebnis: generateRandomNumber(2),
      numIndex: null,
    };
  }

  function prepareBezirkUndWahlIDStapelart(): Builder<BezirkUndWahlIDStapelArt> {
    return proxyBuilder<BezirkUndWahlIDStapelArt>(
      createBezirkUndWahlIDStapelart()
    );
  }

  function prepareErgebnisseDTO(): Builder<ErgebnisseDTO> {
    return proxyBuilder<ErgebnisseDTO>(createErgebnisseDTO());
  }

  function prepareErgebnisDTO(): Builder<ErgebnisDTO> {
    return proxyBuilder<ErgebnisDTO>(createErgebnisDTO());
  }

  function prepareErgebnisse(): Builder<Ergebnisse> {
    return proxyBuilder<Ergebnisse>(createErgebnisse());
  }

  function prepareErgebnis(): Builder<Ergebnis> {
    return proxyBuilder<Ergebnis>(createErgebnis());
  }

  function _createBezirkUndWahlIDStapelartDTO(
    stapelArt: BezirkUndWahlIDStapelartDTOStapelartEnum
  ): BezirkUndWahlIDStapelartDTO {
    return {
      wahlID: generateRandomString(10),
      wahlbezirkID: generateRandomString(5),
      stapelart: stapelArt,
    };
  }

  return {
    createBezirkUndWahlIDStapelart,
    prepareBezirkUndWahlIDStapelart,
    createErgebnisseDTO,
    prepareErgebnisseDTO,
    createErgebnisDTO,
    prepareErgebnisDTO,
    createErgebnisse,
    prepareErgebnisse,
    createErgebnis,
    prepareErgebnis,
  };
}
