import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";
import type { Builder } from "@tests/common/Builder.ts";

import { proxyBuilder } from "@tests/common/Builder.ts";
import { useCommonTestDataFactory } from "@tests/common/CommonTestDataFactory.ts";

const { generateDateRandomAsString, generateNumberRandom } =
  useCommonTestDataFactory();

export function useWahltagTestDataFactory() {
  function createWahltagComplete(countEvents = 3): Wahltag {
    const wahltagEvents: WahltagEvent[] = [];
    for (let i = 0; i < countEvents; i++) {
      wahltagEvents.push(createWahltagEventComplete());
    }
    return {
      wahltag: generateDateRandomAsString(),
      events: wahltagEvents,
    };
  }

  function prepareWahltagDtoComplete(): Builder<WahltagDTO> {
    return proxyBuilder<WahltagDTO>({
      wahltag: generateDateRandomAsString(),
      wahltagID: `wahltagID${generateNumberRandom(3)}`,
      nummer: `${generateNumberRandom(2)}`,
      beschreibung: `beschreibung${generateNumberRandom(3)}`,
    });
  }

  function prepareWahltagEvent(): Builder<WahltagEvent> {
    return proxyBuilder<WahltagEvent>({
      wahltagID: `wahltagID${generateNumberRandom(3)}`,
      nummer: `${generateNumberRandom(2)}`,
      beschreibung: `beschreibung${generateNumberRandom(3)}`,
    });
  }

  /* internal only */
  function createWahltagEventComplete(): WahltagEvent {
    return {
      nummer: `${generateNumberRandom(2)}`,
      wahltagID: `wahltagID${generateNumberRandom(3)}`,
      beschreibung: `beschreibung${generateNumberRandom(3)}`,
    };
  }

  return {
    createWahltagComplete,
    prepareWahltagDtoComplete,
    prepareWahltagEvent,
  };
}
