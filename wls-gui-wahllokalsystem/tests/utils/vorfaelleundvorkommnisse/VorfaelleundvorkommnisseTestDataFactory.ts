import type {
  EreignisDTO,
  WahlbezirkEreignisseDTO,
} from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

const {
  generateRandomDate,
  generateRandomNumber,
  generateRandomString,
  generateRandomBoolean,
  getRandomItem,
} = useCommonTestDataFactory();

export function useVorfaelleundvorkommnisseTestDataFactory() {
  function createEreignis(): Ereignis {
    return {
      beschreibung: `beschreibung${generateRandomNumber(4)}`,
      uhrzeit: generateRandomDate(),
      ereignisart: getRandomItem(Object.values(EreignisartEnum)),
    };
  }

  function createEreignisDTO(): EreignisDTO {
    return {
      beschreibung: `beschreibung${generateRandomNumber(4)}`,
      uhrzeit: generateRandomDate().toISOString(),
      ereignisart: getRandomItem(Object.values(EreignisartEnum)),
    };
  }

  function createWahlbezirkEreignisse(
    countEreignisse = 3
  ): WahlbezirkEreignisse {
    const ereigniseintraege: Ereignis[] = [];
    for (let i = 0; i < countEreignisse; i++) {
      ereigniseintraege.push(createEreignis());
    }
    return {
      wahlbezirkID: generateRandomString(4),
      keineVorfaelle: generateRandomBoolean(),
      keineVorkommnisse: generateRandomBoolean(),
      ereigniseintraege: ereigniseintraege,
    };
  }

  function createWahlbezirkEreignisseDTO(
    countEreignisse = 3
  ): WahlbezirkEreignisseDTO {
    const ereigniseintraege: EreignisDTO[] = [];
    for (let i = 0; i < countEreignisse; i++) {
      ereigniseintraege.push(createEreignisDTO());
    }
    return {
      wahlbezirkID: generateRandomString(4),
      keineVorfaelle: generateRandomBoolean(),
      keineVorkommnisse: generateRandomBoolean(),
      ereigniseintraege: ereigniseintraege,
    };
  }

  function prepareEreignis(): Builder<Ereignis> {
    return proxyBuilder<Ereignis>(createEreignis());
  }

  function prepareEreignisDTO(): Builder<EreignisDTO> {
    return proxyBuilder<EreignisDTO>(createEreignisDTO());
  }

  function prepareWahlbezirkEreignisse(): Builder<WahlbezirkEreignisse> {
    return proxyBuilder<WahlbezirkEreignisse>(createWahlbezirkEreignisse());
  }

  function prepareWahlbezirkEreignisseDTO(): Builder<WahlbezirkEreignisseDTO> {
    return proxyBuilder<WahlbezirkEreignisseDTO>(
      createWahlbezirkEreignisseDTO()
    );
  }

  return {
    createEreignis,
    prepareEreignis,
    prepareEreignisDTO,
    prepareWahlbezirkEreignisse,
    prepareWahlbezirkEreignisseDTO,
  };
}
