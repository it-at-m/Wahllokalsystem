import type {
  KonfigurierterWahltagDTO,
  WahltagDTO,
} from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";
import type { Builder } from "@tests/utils/common/Builder.ts";

import { proxyBuilder } from "@tests/utils/common/Builder.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomDate, generateRandomDateAsString, generateRandomNumber } =
  useCommonTestDataFactory();

export function useWahltagTestDataFactory() {
  function createKonfigurierterWahltagDTOComplete(): KonfigurierterWahltagDTO {
    return {
      nummer: `${generateRandomNumber(2)}`,
      wahltag: generateRandomDateAsString(),
      wahltagID: `wahltagID${generateRandomNumber(3)}`,
      wahltagStatusDTO: "INAKTIV",
    };
  }

  function createWahltagComplete(countEvents = 3): Wahltag {
    const wahltagEvents: WahltagEvent[] = [];
    for (let i = 0; i < countEvents; i++) {
      wahltagEvents.push(createWahltagEventComplete());
    }
    return {
      wahltag: generateRandomDate(),
      events: wahltagEvents,
    };
  }

  function prepareKonfigurierterWahltagDTO(): Builder<KonfigurierterWahltagDTO> {
    return proxyBuilder<KonfigurierterWahltagDTO>(
      createKonfigurierterWahltagDTOComplete()
    );
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

  return {
    createKonfigurierterWahltagDTOComplete,
    createWahltagComplete,
    prepareKonfigurierterWahltagDTO,
    prepareWahltagDtoComplete,
    prepareWahltagEvent,
  };
}

function createWahltagEventComplete(): WahltagEvent {
  return {
    nummer: `${generateRandomNumber(2)}`,
    wahltagID: `wahltagID${generateRandomNumber(3)}`,
    beschreibung: `beschreibung${generateRandomNumber(3)}`,
  };
}
