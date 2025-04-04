import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";
import type { Builder } from "@tests/utils/common/Builder.ts";

import { proxyBuilder } from "@tests/utils/common/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomDateAsString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useWahltagTestDataFactory() {
  function createWahltagComplete(countEvents = 3): Wahltag {
    const wahltagEvents: WahltagEvent[] = [];
    for (let i = 0; i < countEvents; i++) {
      wahltagEvents.push(createWahltagEventComplete());
    }
    return {
      wahltag: generateRandomDateAsString(),
      events: wahltagEvents,
    };
  }

  function prepareWahltagDtoComplete(): Builder<WahltagDTO> {
    return proxyBuilder<WahltagDTO>({
      wahltag: generateRandomDateAsString(),
      wahltagID: `wahltagID${generateRandomNumber(3)}`,
      nummer: `${generateRandomNumber(2)}`,
      beschreibung: `beschreibung${generateRandomNumber(3)}`,
    });
  }

  function prepareWahltagEvent(): Builder<WahltagEvent> {
    return proxyBuilder<WahltagEvent>({
      wahltagID: `wahltagID${generateRandomNumber(3)}`,
      nummer: `${generateRandomNumber(2)}`,
      beschreibung: `beschreibung${generateRandomNumber(3)}`,
    });
  }

  /* internal only */
  function createWahltagEventComplete(): WahltagEvent {
    return {
      nummer: `${generateRandomNumber(2)}`,
      wahltagID: `wahltagID${generateRandomNumber(3)}`,
      beschreibung: `beschreibung${generateRandomNumber(3)}`,
    };
  }

  return {
    createWahltagComplete,
    prepareWahltagDtoComplete,
    prepareWahltagEvent,
  };
}
