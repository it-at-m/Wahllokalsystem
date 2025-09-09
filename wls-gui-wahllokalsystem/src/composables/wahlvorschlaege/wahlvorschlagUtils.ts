import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export function useWahlvorschlagUtils() {
  function getFirstKandidatNameOrEmptyString(wahlvorschlag: Wahlvorschlag) {
    if (wahlvorschlag.kandidaten) {
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
    getFirstKandidatNameOrEmptyString,
  };
}
