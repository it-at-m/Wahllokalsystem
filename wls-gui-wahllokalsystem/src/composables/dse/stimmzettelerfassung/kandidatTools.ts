import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";
import type { PersistedKandidat } from "@/types/dse/stimmzettelerfassung/PersistedKandidat.ts";

export function useKandidatTools() {
  function hasAnyKennzeichenOrReststimme(kandidat: DseKandidat): boolean {
    return hasAnyKennzeichen(kandidat) || !!kandidat.reststimmen;
  }

  function hasAnyKennzeichen(kandidat: DseKandidat): boolean {
    return (
      kandidat.durchgestrichen ||
      !!kandidat.einzelstimmen ||
      !!kandidat.ungueltigeStimmen
    );
  }

  function getEinzelstimmenOrZero(kandidat: DseKandidat) {
    return kandidat.einzelstimmen ?? 0;
  }

  function getUngueltigeStimmenOrZero(kandidat: DseKandidat) {
    return kandidat.ungueltigeStimmen ?? 0;
  }

  function getTotalEinzelAndUngueltigeStimmen(kandidat: DseKandidat) {
    return (
      getEinzelstimmenOrZero(kandidat) + getUngueltigeStimmenOrZero(kandidat)
    );
  }

  function getTotalEinzelstimmenOfKandidatenWithSameId(
    kandidat: DseKandidat
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
    kandidat: DseKandidat
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

  function sortAndDeepCloneKandidaten(kandidaten: PersistedKandidat[]) {
    return kandidaten
      .slice()
      .sort((kandidat1, kandidat2) => {
        return (
          _compareKandidatenById(kandidat1, kandidat2) ||
          _compareKandidatenByNennung(kandidat1, kandidat2)
        );
      })
      .map((k) => ({
        kandidatId: k.kandidatId,
        nennung: k.nennung,
        isDiscarded: k.isDiscarded,
        votesByVoter: k.votesByVoter ?? null,
        invalidVotes: k.invalidVotes ?? null,
        votesByWahlvorschlag: k.votesByWahlvorschlag ?? null,
      }));
  }

  function _compareKandidatenById(
    kandidat1: PersistedKandidat,
    kandidat2: PersistedKandidat
  ) {
    return kandidat1.kandidatId.localeCompare(kandidat2.kandidatId);
  }
  function _compareKandidatenByNennung(
    kandidat1: PersistedKandidat,
    kandidat2: PersistedKandidat
  ) {
    return kandidat1.nennung - kandidat2.nennung;
  }

  return {
    getEinzelstimmenOrZero,
    getTotalEinzelAndUngueltigeStimmenOfKandidatenWithSameId,
    getTotalEinzelstimmenOfKandidatenWithSameId,
    getUngueltigeStimmenOrZero,
    hasAnyKennzeichen,
    hasAnyKennzeichenOrReststimme,
    sortAndDeepCloneKandidaten,
  };
}
