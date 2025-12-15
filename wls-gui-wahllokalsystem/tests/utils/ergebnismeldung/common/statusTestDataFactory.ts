import type {
  MeldungDTO,
  StatusDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Meldung } from "@/types/ergebnismeldung/Meldung.ts";
import type { Status } from "@/types/ergebnismeldung/Status.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";

import { MeldungDTOValidierungsstatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

const { createBezirkUndWahlIDDTO, createBezirkUndWahlID } =
  useCommonErgebnismeldungTestDataFactory();
const { generateRandomBoolean, getRandomItem, generateRandomString } =
  useCommonTestDataFactory();

export function useStatusTestDataFactory() {
  function createStatusDTO(): StatusDTO {
    return {
      bezirkUndWahlID: createBezirkUndWahlIDDTO(),
      schnellmeldung: createMeldungDTO(),
      niederschrift: createMeldungDTO(),
    };
  }

  function createMeldungDTO(): MeldungDTO {
    return {
      validierungsstatus: getRandomItem(
        Object.values(MeldungDTOValidierungsstatusEnum)
      ),
      gedruckt: generateRandomBoolean(),
      uebermittelt: generateRandomBoolean(),
      sendeuhrzeit: generateRandomString(10),
    };
  }

  function createStatus(): Status {
    return {
      bezirkUndWahlID: createBezirkUndWahlID(),
      schnellmeldung: createMeldung(),
      niederschrift: createMeldung(),
    };
  }

  function createMeldung(): Meldung {
    return {
      validierungsstatus: getRandomItem(
        Object.values(MeldungValidierungsstatusEnum)
      ),
      gedruckt: generateRandomBoolean(),
    };
  }

  function prepareStatusDTO(): Builder<StatusDTO> {
    return proxyBuilder<StatusDTO>(createStatusDTO());
  }

  function prepareMeldungDTO(): Builder<MeldungDTO> {
    return proxyBuilder<MeldungDTO>(createMeldungDTO());
  }

  function prepareStatus(): Builder<Status> {
    return proxyBuilder<Status>(createStatus());
  }

  function prepareMeldung(): Builder<Meldung> {
    return proxyBuilder<Meldung>(createMeldung());
  }

  return {
    createStatusDTO,
    createMeldungDTO,
    createStatus,
    createMeldung,
    prepareStatusDTO,
    prepareMeldungDTO,
    prepareStatus,
    prepareMeldung,
  };
}
