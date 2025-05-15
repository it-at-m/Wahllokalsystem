import type { WaehleranzahlDTO } from "@/api/wls-clients/generated-monitoring-api";
import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

const { applyLocalTimezoneOffset } = useDateTimeFormatter();
const { isValidDate } = useDateTimeUtils();

export function useWahlbeteiligungMapper() {
  function toDto(wahlbeteiligung: Waehleranzahl): WaehleranzahlDTO {
    let mappedUhrzeit = new Date();
    if (wahlbeteiligung.uhrzeit) {
      mappedUhrzeit = applyLocalTimezoneOffset(wahlbeteiligung.uhrzeit);
    }
    return {
      anzahlWaehler: wahlbeteiligung.anzahlWaehler,
      uhrzeit: mappedUhrzeit.toJSON(),
    };
  }

  function toModel(wahlbeteiligungDTO: WaehleranzahlDTO): Waehleranzahl {
    const parsedDate = new Date(wahlbeteiligungDTO.uhrzeit);
    return {
      anzahlWaehler: wahlbeteiligungDTO.anzahlWaehler,
      uhrzeit: isValidDate(parsedDate) ? parsedDate : undefined,
    };
  }

  return { toDto, toModel };
}
