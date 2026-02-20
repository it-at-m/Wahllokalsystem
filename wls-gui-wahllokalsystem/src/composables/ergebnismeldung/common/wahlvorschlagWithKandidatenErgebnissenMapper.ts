import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnismeldung/common/WahlvorschlagWithKandidatenErgebnissen.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export function useWahlvorschlagWithKandidatenErgebnissenMapper() {
  function toWahlvorschlagWithKandidatenErgebnissen(
    wahlvorschlag: Wahlvorschlag,
    ergebnisse: Ergebnisse | null
  ): WahlvorschlagWithKandidatenErgebnissen {
    const wahlvorschlagWithKandidatenErgebnissen = _initResult(wahlvorschlag);

    if (wahlvorschlag.kandidaten) {
      wahlvorschlagWithKandidatenErgebnissen.kandidatenErgebnisse = [
        ...wahlvorschlag.kandidaten,
      ].map((kandidat) => {
        const ergebnisForKandidat =
          ergebnisse?.ergebnisse.find(
            _withKandidatId(kandidat.identifikator)
          ) || _createEmptyErgebnis(wahlvorschlag, kandidat);
        return {
          ergebnis: ergebnisForKandidat,
          kandidat,
        };
      });
    }
    return wahlvorschlagWithKandidatenErgebnissen;
  }

  function toErgebnisse(
    ergebnisse: Ergebnis[],
    wahlbezirkID: string,
    wahlID: string,
    stapelArt: StapelArtEnum
  ): Ergebnisse {
    return {
      bezirkUndWahlIDStapelart: {
        wahlID,
        wahlbezirkID,
        stapelArt,
      },
      ergebnisse,
    };
  }

  function _createEmptyErgebnis(
    wahlvorschlag: Wahlvorschlag,
    kandidat: Kandidat
  ): Ergebnis {
    return {
      wahlvorschlagID: wahlvorschlag.identifikator,
      kandidatID: kandidat.identifikator,
      wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
      ergebnis: null,
      numIndex: null,
    };
  }

  function _initResult(
    wahlvorschlag: Wahlvorschlag
  ): WahlvorschlagWithKandidatenErgebnissen {
    return {
      identifikator: wahlvorschlag.identifikator,
      kandidatenErgebnisse: [],
      kurzname: wahlvorschlag.kurzname,
      ordnungszahl: wahlvorschlag.ordnungszahl,
    };
  }

  function _withKandidatId(kandidatID: string) {
    return (ergebnis: Ergebnis) => ergebnis.kandidatID === kandidatID;
  }

  return {
    toErgebnisse,
    toWahlvorschlagWithKandidatenErgebnissen,
  };
}
