import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useMbwErgebnisAndWahlvorschlagMapper(
  wahlID: string,
  wahlbezirkID: string
) {
  function mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse(
    stapelArt: StapelArtEnum,
    list: MbwErgebnisseAndWahlvorschlag[]
  ) {
    const ergebnisse: Ergebnis[] = list.map((ergebnisseAndWahlvorschlag) => ({
      wahlvorschlagID: ergebnisseAndWahlvorschlag.wahlvorschlag.identifikator,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl:
        ergebnisseAndWahlvorschlag.wahlvorschlag.ordnungszahl,
      ergebnis: _getErgebnisByStapelart(stapelArt, ergebnisseAndWahlvorschlag),
      numIndex: null,
    }));

    return {
      bezirkUndWahlIDStapelart: {
        stapelArt: stapelArt,
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
      },
      ergebnisse: ergebnisse,
    };
  }

  function _getErgebnisByStapelart(
    stapelArt: StapelArtEnum,
    ergebnisseAndWahlvorschlag: MbwErgebnisseAndWahlvorschlag
  ) {
    if (stapelArt == StapelArtEnum.MbwA) {
      return ergebnisseAndWahlvorschlag.ergebnisStapelA.ergebnis;
    } else if (stapelArt == StapelArtEnum.MbwB) {
      return ergebnisseAndWahlvorschlag.ergebnisStapelB.ergebnis;
    } else {
      throw new Error(
        `Für die Stapelart ${stapelArt} können keine Ergebnisse geliefert werden.`
      );
    }
  }

  return {
    mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse,
  };
}
