import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

export function useUngueltigeStimmeTools() {
  function addInvalidVotesToKandidat(
    kandidat: Kandidat,
    numberOfVotes: number
  ) {
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen = currentUngueltigeStimmen + numberOfVotes;
  }

  function removeInvalidVotesFromKandidat(
    kandidat: Kandidat,
    numberOfVotes: number
  ) {
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen = currentUngueltigeStimmen - numberOfVotes;
  }

  return {
    addInvalidVotesToKandidat,
    removeInvalidVotesFromKandidat,
  };
}
