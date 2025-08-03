import type {
  WaehlerverzeichnisDTO,
  WaehlerverzeichnisWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";

export function useWaehlerverzeichnisMapper() {
  function toWaehlerverzeichnisWriteDTO(
    pflegeWaehlerverzeichnis: PflegeWaehlerverzeichnis
  ): WaehlerverzeichnisWriteDTO {
    return {
      verzeichnisLagVor: pflegeWaehlerverzeichnis.waehlerverzeichnisUnchanged,
      berichtigungVorBeginnDerAbstimmung:
        !pflegeWaehlerverzeichnis.waehlerverzeichnisUnchanged,
      nachtraeglicheBerichtigung:
        pflegeWaehlerverzeichnis.nachtraeglicheBerichtigung,
      mitteilungUeberUngueltigeWahlscheineErhalten:
        pflegeWaehlerverzeichnis.mitteilungUeberUngueltigeWahlscheineErhalten,
    };
  }

  function toPflegeWaehlerverzeichnis(
    waehlerverzeichnisDTO: WaehlerverzeichnisDTO
  ): PflegeWaehlerverzeichnis {
    return {
      waehlerverzeichnisUnchanged:
        waehlerverzeichnisDTO.verzeichnisLagVor ?? false,
      nachtraeglicheBerichtigung:
        waehlerverzeichnisDTO.nachtraeglicheBerichtigung ?? false,
      mitteilungUeberUngueltigeWahlscheineErhalten:
        waehlerverzeichnisDTO.mitteilungUeberUngueltigeWahlscheineErhalten ??
        false,
    };
  }
  return { toPflegeWaehlerverzeichnis, toWaehlerverzeichnisWriteDTO };
}
