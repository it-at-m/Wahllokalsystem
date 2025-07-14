import type {
  WahlbriefdatenDTO,
  WahlbriefdatenWriteDTO,
} from "@/api/wls-clients/generated-briefwahl-api";
import type { Wahlbriefdaten } from "@/types/briefwahl/Wahlbriefdaten.ts";

export function useBriefwahlMapper() {
  function toWahlbriefdatenModel(
    wahlbriefdatenDTO: WahlbriefdatenDTO
  ): Wahlbriefdaten {
    return {
      wahlbriefe: wahlbriefdatenDTO.wahlbriefe,
      verzeichnisseUngueltige: wahlbriefdatenDTO.verzeichnisseUngueltige,
      nachtraege: wahlbriefdatenDTO.nachtraege,
      nachtraeglichUeberbrachte: wahlbriefdatenDTO.nachtraeglichUeberbrachte,
      zeitNachtraeglichUeberbrachte:
        wahlbriefdatenDTO.zeitNachtraeglichUeberbrachte,
    };
  }

  function toWahlbriefdatenWriteDTO(
    wahlbriefdaten: Wahlbriefdaten
  ): WahlbriefdatenWriteDTO {
    return {
      wahlbriefe: wahlbriefdaten.wahlbriefe,
      verzeichnisseUngueltige: wahlbriefdaten.verzeichnisseUngueltige,
      nachtraege: wahlbriefdaten.nachtraege,
      nachtraeglichUeberbrachte: wahlbriefdaten.nachtraeglichUeberbrachte,
      zeitNachtraeglichUeberbrachte:
        wahlbriefdaten.zeitNachtraeglichUeberbrachte,
    };
  }

  return {
    toWahlbriefdatenModel,
    toWahlbriefdatenWriteDTO,
  };
}
