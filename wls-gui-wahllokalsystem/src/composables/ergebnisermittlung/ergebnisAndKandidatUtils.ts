import type { ErgebnisAndKandidat } from "@/types/ergebnisermittlung/ErgebnisAndKandidat.ts";

export function useErgebnisAndKandidatUtils() {
  function summeKandidatenStimmen(
    kandidatenMitErgebnis: ErgebnisAndKandidat[]
  ) {
    return kandidatenMitErgebnis.reduce(
      (prev, current) => prev + (current.ergebnis.ergebnis ?? 0),
      0
    );
  }

  return {
    summeKandidatenStimmen,
  };
}
