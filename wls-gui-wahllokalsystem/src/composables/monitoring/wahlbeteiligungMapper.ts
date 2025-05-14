import type { WaehleranzahlDTO } from "@/api/wls-clients/generated-monitoring-api";
import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const { applyLocalTimezoneOffset } = useDateTimeFormatter();

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
      uhrzeit: _isUhrzeitValid(parsedDate) ? parsedDate : undefined,
    };
  }

  return { toDto, toModel };
}

function _isUhrzeitValid(date: Date): boolean {
  return isNaN(date.getTime());
}
