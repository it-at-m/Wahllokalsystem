import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

export function useManagedStimmzettelUngueltigeStimmeUtils() {
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
    const newValue = currentUngueltigeStimmen - numberOfVotes;
    kandidat.ungueltigeStimmen = newValue > 0 ? newValue : null;
  }

  return {
    addInvalidVotesToKandidat,
    removeInvalidVotesFromKandidat,
  };
}
