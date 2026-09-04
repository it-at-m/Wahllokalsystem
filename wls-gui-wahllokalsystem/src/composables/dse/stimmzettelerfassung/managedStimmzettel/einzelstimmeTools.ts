import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

export function useEinzelstimmeTools() {
  function addVotesToKandidat(kandidat: Kandidat, numberOfVotes: number) {
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    kandidat.einzelstimmen = currentEinzelstimmen + numberOfVotes;
  }

  function removeVotesFromKandidat(kandidat: Kandidat, numberOfVotes: number) {
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    const newValue = currentEinzelstimmen - numberOfVotes;
    kandidat.einzelstimmen = newValue > 0 ? newValue : null;
  }

  return {
    addVotesToKandidat,
    removeVotesFromKandidat,
  };
}
