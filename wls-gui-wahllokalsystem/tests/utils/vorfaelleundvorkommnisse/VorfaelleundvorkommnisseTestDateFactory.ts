import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

const { generateRandomDate, generateRandomNumber } = useCommonTestDataFactory();

export function useVorfaelleundvorkommnisseTestDateFactory() {
  function createEreignis(): Ereignis {
    return {
      beschreibung: `beschreibung${generateRandomNumber(4)}`,
      uhrzeit: generateRandomDate(),
      ereignisart: _generateRandomEreignisart(),
    };
  }

  function _generateRandomEreignisart(): EreignisartEnum {
    const ereignisarten = Object.values(EreignisartEnum);
    const zufaelligerIndex = Math.floor(Math.random() * ereignisarten.length);
    return ereignisarten[zufaelligerIndex];
  }

  return {
    createEreignis,
  };
}
