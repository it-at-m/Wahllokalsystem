import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

export function useKandidatTools() {
  function hasAnyKennzeichenOrReststimme(kandidat: Kandidat): boolean {
    return hasAnyKennzeichen(kandidat) || !!kandidat.reststimmen;
  }

  function hasAnyKennzeichen(kandidat: Kandidat): boolean {
    return (
      kandidat.durchgestrichen ||
      !!kandidat.einzelstimmen ||
      !!kandidat.ungueltigeStimmen
    );
  }

  return {
    hasAnyKennzeichen,
    hasAnyKennzeichenOrReststimme,
  };
}
