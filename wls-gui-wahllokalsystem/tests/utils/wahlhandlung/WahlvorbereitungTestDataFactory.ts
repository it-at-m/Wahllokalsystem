import type {
  BriefwahlvorbereitungDTO,
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlvorbereitungDTO,
  UrnenwahlvorbereitungWriteDTO,
  WahlurneDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { Urnenwahlvorbereitung } from "@/types/wahlhandlung/Urnenwahlvorbereitung.ts";
import type { Wahlurne } from "@/types/wahlhandlung/Wahlurne.ts";
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const {
  generateRandomDateTimeAsString,
  generateRandomString,
  generateRandomNumberInRange,
  generateRandomBoolean,
} = useCommonTestDataFactory();

export function useWahlvorbereitungTestDataFactory() {
  function createEroeffnungsUhrzeitWriteDTO(): EroeffnungsUhrzeitWriteDTO {
    return {
      eroeffnungsuhrzeit: generateRandomDateTimeAsString(),
    };
  }

  function createUrnenwahlvorbereitungWriteDTO(): UrnenwahlvorbereitungWriteDTO {
    return {
      anzahlWahlkabinen: generateRandomNumberInRange(1, 10),
      anzahlWahltische: generateRandomNumberInRange(1, 10),
      anzahlNebenraeume: generateRandomNumberInRange(1, 10),
      urnenAnzahl: _generateWahlurneDTOArray(),
    };
  }

  function createUrnenwahlvorbereitungDTO(): UrnenwahlvorbereitungDTO {
    return {
      wahlbezirkID: generateRandomString(10),
      anzahlWahlkabinen: generateRandomNumberInRange(1, 10),
      anzahlWahltische: generateRandomNumberInRange(1, 10),
      anzahlNebenraeume: generateRandomNumberInRange(1, 10),
      urnenAnzahl: _generateWahlurneDTOArray(),
    };
  }

  function createUrnenwahlvorbereitung(): Urnenwahlvorbereitung {
    return {
      wahlbezirkID: generateRandomString(10),
      anzahlWahlkabinen: generateRandomNumberInRange(1, 10),
      anzahlWahltische: generateRandomNumberInRange(1, 10),
      anzahlNebenraeume: generateRandomNumberInRange(1, 10),
      urneVersiegelt: generateRandomBoolean(),
      urnenAnzahl: _generateWahlurneArray(),
    };
  }

  function createWahlvorbereitung(): Wahlvorbereitung {
    return {
      wahlbezirkID: generateRandomString(10),
      urneVersiegelt: generateRandomBoolean(),
      urnenAnzahl: _generateWahlurneArray(),
    };
  }

  function createBriefwahlvorbereitungDTO(): BriefwahlvorbereitungDTO {
    return {
      wahlbezirkID: generateRandomString(10),
      urnenAnzahl: _generateWahlurneDTOArray(),
    };
  }

  function prepareUrnenwahlvorbereitung(): Builder<Urnenwahlvorbereitung> {
    return proxyBuilder<Urnenwahlvorbereitung>(createUrnenwahlvorbereitung());
  }

  function _generateWahlurneArray(): Wahlurne[] {
    return [
      {
        wahlID: generateRandomString(10),
        anzahl: generateRandomNumberInRange(1, 10),
      },
    ];
  }

  function _generateWahlurneDTOArray(): WahlurneDTO[] {
    return [
      {
        wahlID: generateRandomString(10),
        anzahl: generateRandomNumberInRange(1, 10),
        urneVersiegelt: generateRandomBoolean(),
      },
    ];
  }

  return {
    createEroeffnungsUhrzeitWriteDTO,
    createUrnenwahlvorbereitungWriteDTO,
    createUrnenwahlvorbereitung,
    createUrnenwahlvorbereitungDTO,
    createWahlvorbereitung,
    createBriefwahlvorbereitungDTO,
    prepareUrnenwahlvorbereitung,
  };
}
