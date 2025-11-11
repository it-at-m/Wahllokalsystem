import type { WahlvorschlagWithScorableKandidaten } from "@/types/ergebnisermittlung/WahlvorschlagWithScorableKandidaten.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";

export function useWahlvorschlagWithScorableKandidatenMapper() {
  function toWahlvorschlagWithScorableKandidaten(
    wahlvorschlag: Wahlvorschlag,
    ergebnisse: Ergebnisse | null
  ) {
    const wahlvorschlagWithScorableKandidaten = _initResult(wahlvorschlag);

    if (wahlvorschlag.kandidaten) {
      wahlvorschlagWithScorableKandidaten.scorableKandidaten = [
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

      return wahlvorschlagWithScorableKandidaten;
    }
  }

  function toErgebnisse(
    wahlvorschlagWithScorableKandidaten: WahlvorschlagWithScorableKandidaten[],
    wahlbezirkID: string,
    wahlID: string,
    stapelArt: StapelArtEnum
  ): Ergebnisse {
    const ergebnisValuesToSave = wahlvorschlagWithScorableKandidaten
      .flatMap((wahlvorschlag) => wahlvorschlag.scorableKandidaten)
      .map((scorableKandidat) => scorableKandidat.ergebnis)
      .filter((ergebnis) => ergebnis.ergebnis != null);

    return {
      bezirkUndWahlIDStapelart: {
        wahlID,
        wahlbezirkID,
        stapelArt: stapelArt,
      },
      ergebnisse: ergebnisValuesToSave,
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
  ): WahlvorschlagWithScorableKandidaten {
    return {
      identifikator: wahlvorschlag.identifikator,
      scorableKandidaten: [],
      kurzname: wahlvorschlag.kurzname,
      ordnungszahl: wahlvorschlag.ordnungszahl,
    };
  }

  function _withKandidatId(kandidatID: string) {
    return (ergebnis: Ergebnis) => ergebnis.kandidatID === kandidatID;
  }

  return {
    toErgebnisse,
    toWahlvorschlagWithScorableKandidaten,
  };
}
