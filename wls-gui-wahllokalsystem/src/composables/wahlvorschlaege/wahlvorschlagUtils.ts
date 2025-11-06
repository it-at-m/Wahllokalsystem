import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export function useWahlvorschlagUtils() {
  function getWahlvorschlagTitle(wahlvorschlag: Wahlvorschlag) {
    return `${wahlvorschlag.ordnungszahl} - ${wahlvorschlag.kurzname}, ${getFirstKandidatNameOrEmptyString(wahlvorschlag)}`;
  }

  function getFirstKandidatNameOrEmptyString(wahlvorschlag: Wahlvorschlag) {
    if (wahlvorschlag.kandidaten && wahlvorschlag.kandidaten.size > 0) {
      const kandidatWithLowedListenPosition = [
        ...wahlvorschlag.kandidaten,
      ].reduce((min, current) =>
        current.listenposition < min.listenposition ? current : min
      );
      return kandidatWithLowedListenPosition.name;
    } else {
      return "";
    }
  }

  function getKandidatLaufendeNummer(
    wahlvorschlagNummer: number,
    kandidatListenPosition: number,
    kandidatListenPositionPadLength = 2
  ) {
    return `${wahlvorschlagNummer}${kandidatListenPosition.toString().padStart(kandidatListenPositionPadLength, "0")}`;
  }

  function sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege: Wahlvorschlaege) {
    const sortedArray = Array.from(wahlvorschlaege.wahlvorschlaege).sort(
      (vorschlagA, vorschlagB) =>
        vorschlagA.ordnungszahl - vorschlagB.ordnungszahl
    );
    wahlvorschlaege.wahlvorschlaege = new Set(sortedArray);
    return wahlvorschlaege;
  }

  function sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl(
    mbwErgebnisse: MbwErgebnisseAndWahlvorschlag[]
  ) {
    return mbwErgebnisse.sort(
      (a, b) => a.wahlvorschlag.ordnungszahl - b.wahlvorschlag.ordnungszahl
    );
  }

  return {
    getWahlvorschlagTitle,
    getFirstKandidatNameOrEmptyString,
    getKandidatLaufendeNummer,
    sortWahlvorschlaegeByOrdnungszahl,
    sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl,
  };
}
