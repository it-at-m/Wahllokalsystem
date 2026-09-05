import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

export function useKandidatTools() {
  function hasNoDataSet(kandidat: Kandidat) {
    return (
      !kandidat.durchgestrichen &&
      _isNull(kandidat.einzelstimmen) &&
      _isNull(kandidat.reststimmen) &&
      _isNull(kandidat.ungueltigeStimmen)
    );
  }

  function hasAnyKennzeichen(kandidat: Kandidat) {
    return (
      kandidat.durchgestrichen ||
      kandidat.einzelstimmen ||
      kandidat.reststimmen ||
      kandidat.ungueltigeStimmen
    );
  }

  function _isNull(value: unknown | null) {
    return value === null;
  }

  return {
    hasNoDataSet,
    hasAnyKennzeichen,
  };
}
