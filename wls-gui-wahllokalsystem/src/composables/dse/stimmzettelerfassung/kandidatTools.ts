import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

export function useKandidatTools() {
  function hasAnyKennzeichen(kandidat: Kandidat): boolean {
    return (
      kandidat.durchgestrichen ||
      !!kandidat.einzelstimmen ||
      !!kandidat.reststimmen ||
      !!kandidat.ungueltigeStimmen
    );
  }

  function getEinzelstimmenOrZero(kandidat: Kandidat) {
    return kandidat.einzelstimmen ?? 0;
  }

  function getUngueltigeStimmenOrZero(kandidat: Kandidat) {
    return kandidat.ungueltigeStimmen ?? 0;
  }

  function getTotalEinzelAndUngueltigeStimmen(kandidat: Kandidat) {
    return (
      getEinzelstimmenOrZero(kandidat) + getUngueltigeStimmenOrZero(kandidat)
    );
  }

  function getTotalEinzelstimmenOfKandidatenWithSameId(
    kandidat: Kandidat
  ): number {
    const kandidatenWithSameId = kandidat.owningWahlvorschlag.kandidaten.filter(
      (wahlvorschlagKandidat) =>
        kandidat.kandidatId === wahlvorschlagKandidat.kandidatId
    );
    return kandidatenWithSameId.reduce(
      (previousValue, currentValue) =>
        previousValue + getEinzelstimmenOrZero(currentValue),
      0
    );
  }

  function getTotalEinzelAndUngueltigeStimmenOfKandidatenWithSameId(
    kandidat: Kandidat
  ) {
    const kandidatenWithSameId = kandidat.owningWahlvorschlag.kandidaten.filter(
      (wahlvorschlagKandidat) =>
        kandidat.kandidatId === wahlvorschlagKandidat.kandidatId
    );
    return kandidatenWithSameId.reduce(
      (previousValue, currentValue) =>
        previousValue + getTotalEinzelAndUngueltigeStimmen(currentValue),
      0
    );
  }

  return {
    getEinzelstimmenOrZero,
    getTotalEinzelAndUngueltigeStimmenOfKandidatenWithSameId,
    getTotalEinzelstimmenOfKandidatenWithSameId,
    getUngueltigeStimmenOrZero,
    hasAnyKennzeichen,
  };
}
