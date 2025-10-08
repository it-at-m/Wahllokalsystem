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
  return {
    getWahlvorschlagTitle,
    getFirstKandidatNameOrEmptyString,
  };
}
