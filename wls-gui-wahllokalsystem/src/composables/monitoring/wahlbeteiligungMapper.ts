import type { WaehleranzahlDTO } from "@/api/wls-clients/generated-monitoring-api";
import type { Waehleranzahl } from "@/types/monitoring/Waehleranzahl.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

const { isValidDate } = useDateTimeUtils();
const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

export function useWahlbeteiligungMapper() {
  function toDto(wahlbeteiligung: Waehleranzahl): WaehleranzahlDTO {
    return {
      anzahlWaehler: wahlbeteiligung.anzahlWaehler,
      uhrzeit: wahlbeteiligung.uhrzeit
        ? toYyyyMmDdWithTimeWithoutTimezoneOffset(wahlbeteiligung.uhrzeit)
        : "",
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
