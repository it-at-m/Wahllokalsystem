import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useMbwUtils(wahlID: string, wahlbezirkID: string) {
  function getErgebnisseStapelAFromErgebnisseAndWahlvorschlagList(
    list: MbwErgebnisseAndWahlvorschlag[]
  ) {
    const ergebnisseStapelA: Ergebnisse = {
      bezirkUndWahlIDStapelart: {
        stapelArt: StapelArtEnum.MbwA,
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
      },
      ergebnisse: [],
    };
    for (const ergebnisseAndWahlvorschlag of list) {
      ergebnisseStapelA.ergebnisse.push(
        ergebnisseAndWahlvorschlag.ergebnisStapelA
      );
    }
    return ergebnisseStapelA;
  }

  function getErgebnisseStapelBFromErgebnisseAndWahlvorschlagList(
    list: MbwErgebnisseAndWahlvorschlag[]
  ) {
    const ergebnisseStapelB: Ergebnisse = {
      bezirkUndWahlIDStapelart: {
        stapelArt: StapelArtEnum.MbwB,
        wahlID: wahlID,
        wahlbezirkID: wahlbezirkID,
      },
      ergebnisse: [],
    };
    for (const ergebnisseAndWahlvorschlag of list) {
      ergebnisseStapelB.ergebnisse.push(
        ergebnisseAndWahlvorschlag.ergebnisStapelB
      );
    }
    return ergebnisseStapelB;
  }

  return {
    getErgebnisseStapelAFromErgebnisseAndWahlvorschlagList,
    getErgebnisseStapelBFromErgebnisseAndWahlvorschlagList,
  };
}
