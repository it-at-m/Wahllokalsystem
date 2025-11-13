import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export function useWahlvorschlagUtils() {
  function getWahlvorschlagTitle(wahlvorschlag: Wahlvorschlag) {
    return `${wahlvorschlag.ordnungszahl} - ${wahlvorschlag.kurzname}, ${getFirstKandidatNameOrEmptyString(wahlvorschlag)}`;
  }

  function getFirstKandidatNameOrEmptyString(wahlvorschlag: Wahlvorschlag) {
    if (wahlvorschlag.kandidaten && wahlvorschlag.kandidaten.length > 0) {
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

  function sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege: Wahlvorschlaege) {
    wahlvorschlaege.wahlvorschlaege = Array.from(
      wahlvorschlaege.wahlvorschlaege
    ).sort(
      (vorschlagA, vorschlagB) =>
        vorschlagA.ordnungszahl - vorschlagB.ordnungszahl
    );
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
    sortWahlvorschlaegeByOrdnungszahl,
    sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl,
  };
}
