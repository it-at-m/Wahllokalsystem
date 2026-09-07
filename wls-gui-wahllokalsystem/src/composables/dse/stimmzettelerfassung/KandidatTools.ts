import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

export function useKandidatTools() {
  function hasAnyKennzeichen(kandidat: Kandidat) {
    return (
      kandidat.durchgestrichen ||
      kandidat.einzelstimmen ||
      kandidat.reststimmen ||
      kandidat.ungueltigeStimmen
    );
  }

  return {
    hasAnyKennzeichen,
  };
}
