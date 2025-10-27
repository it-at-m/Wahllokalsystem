import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";

import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useMbwErgebnisAndWahlvorschlagMapper(
  wahlID: string,
  wahlbezirkID: string
) {
  function mapStapelAFromErgebnisseAndWahlvorschlagListToErgebnisse(
    list: MbwErgebnisseAndWahlvorschlag[]
  ) {
    const ergebnisse: Ergebnis[] = list.map((ergebnisseAndWahlvorschlag) => ({
      wahlvorschlagID: ergebnisseAndWahlvorschlag.wahlvorschlag.identifikator,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl:
        ergebnisseAndWahlvorschlag.wahlvorschlag.ordnungszahl,
      ergebnis: ergebnisseAndWahlvorschlag.ergebnisStapelA.ergebnis,
      numIndex: null,
    }));

    return {
      bezirkUndWahlIDStapelart: {
        stapelArt: StapelArtEnum.MbwA,
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
      },
      ergebnisse: ergebnisse,
    };
  }

  function mapStapelBFromErgebnisseAndWahlvorschlagListToErgebnisse(
    list: MbwErgebnisseAndWahlvorschlag[]
  ) {
    const ergebnisse: Ergebnis[] = list.map((ergebnisseAndWahlvorschlag) => ({
      wahlvorschlagID: ergebnisseAndWahlvorschlag.wahlvorschlag.identifikator,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl:
        ergebnisseAndWahlvorschlag.wahlvorschlag.ordnungszahl,
      ergebnis: ergebnisseAndWahlvorschlag.ergebnisStapelB.ergebnis,
      numIndex: null,
    }));

    return {
      bezirkUndWahlIDStapelart: {
        stapelArt: StapelArtEnum.MbwB,
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
      },
      ergebnisse: ergebnisse,
    };
  }

  return {
    mapStapelAFromErgebnisseAndWahlvorschlagListToErgebnisse,
    mapStapelBFromErgebnisseAndWahlvorschlagListToErgebnisse,
  };
}
