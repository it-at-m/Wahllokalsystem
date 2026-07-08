import type {
  WahlbriefdatenDTO,
  WahlbriefdatenWriteDTO,
} from "@/api/wls-clients/generated-briefwahl-api";
import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

export function useBriefwahlMapper() {
  function toWahlbriefdatenModel(
    wahlbriefdatenDTO: WahlbriefdatenDTO
  ): Wahlbriefdaten {
    const parsedDate = wahlbriefdatenDTO.zeitNachtraeglichUeberbrachte
      ? new Date(wahlbriefdatenDTO.zeitNachtraeglichUeberbrachte)
      : undefined;
    return {
      wahlbriefe: wahlbriefdatenDTO.wahlbriefe,
      verzeichnisseUngueltige: wahlbriefdatenDTO.verzeichnisseUngueltige,
      nachtraege: wahlbriefdatenDTO.nachtraege,
      nachtraeglichUeberbrachte: wahlbriefdatenDTO.nachtraeglichUeberbrachte,
      zeitNachtraeglichUeberbrachte: parsedDate,
    };
  }

  function toWahlbriefdatenWriteDTO(
    wahlbriefdaten: Wahlbriefdaten
  ): WahlbriefdatenWriteDTO {
    let mappedUhrzeit;
    if (wahlbriefdaten.zeitNachtraeglichUeberbrachte) {
      mappedUhrzeit = toYyyyMmDdWithTimeWithoutTimezoneOffset(
        wahlbriefdaten.zeitNachtraeglichUeberbrachte
      );
    }
    return {
      wahlbriefe: wahlbriefdaten.wahlbriefe,
      verzeichnisseUngueltige: wahlbriefdaten.verzeichnisseUngueltige,
      nachtraege: wahlbriefdaten.nachtraege,
      nachtraeglichUeberbrachte: wahlbriefdaten.nachtraeglichUeberbrachte,
      zeitNachtraeglichUeberbrachte: mappedUhrzeit,
    };
  }

  return {
    toWahlbriefdatenModel,
    toWahlbriefdatenWriteDTO,
  };
}
