import type {
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlvorbereitungDTO,
  UrnenwahlvorbereitungWriteDTO,
  WahlurneDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";
import type { Wahlurne } from "@/types/wahlvorbereitung/Wahlurne.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomDateTimeAsString } = useCommonTestDataFactory();

export function useWahlvorbereitungTestDataFactory() {
  function createEroeffnungsUhrzeitWriteDTO(): EroeffnungsUhrzeitWriteDTO {
    return {
      eroeffnungsuhrzeit: generateRandomDateTimeAsString(),
    };
  }

  function createUrnenwahlvorbereitungWriteDTO(): UrnenwahlvorbereitungWriteDTO {
    return {
      anzahlWahlkabinen: 1,
      anzahlWahltische: 1,
      anzahlNebenraeume: 1,
      urnenAnzahl: _generateWahlurneDTOArray(),
    };
  }

  function createUrnenwahlvorbereitungDTO(): UrnenwahlvorbereitungDTO {
    return {
      wahlbezirkID: "wahlbezirkID1",
      anzahlWahlkabinen: 1,
      anzahlWahltische: 1,
      anzahlNebenraeume: 1,
      urnenAnzahl: _generateWahlurneDTOArray(),
    };
  }

  function createUrnenwahlvorbereitung(): Urnenwahlvorbereitung {
    return {
      wahlbezirkID: "wahlbezirkID1",
      anzahlWahlkabinen: 1,
      anzahlWahltische: 1,
      anzahlNebenraeume: 1,
      urneVersiegelt: true,
      urnenAnzahl: _generateWahlurneArray(),
    };
  }

  function _generateWahlurneArray(): Wahlurne[] {
    return [
      {
        wahlID: "wahlID1",
        anzahl: 1,
      },
    ];
  }

  function _generateWahlurneDTOArray(): WahlurneDTO[] {
    return [
      {
        wahlID: "wahlID1",
        anzahl: 1,
        urneVersiegelt: true,
      },
    ];
  }

  return {
    createEroeffnungsUhrzeitWriteDTO,
    createUrnenwahlvorbereitungWriteDTO,
    createUrnenwahlvorbereitung,
    createUrnenwahlvorbereitungDTO,
  };
}
