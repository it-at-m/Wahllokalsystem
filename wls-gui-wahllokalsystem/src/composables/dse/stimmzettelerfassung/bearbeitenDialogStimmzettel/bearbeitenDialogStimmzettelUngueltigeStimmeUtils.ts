import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";

export function useBearbeitenDialogStimmzettelUngueltigeStimmeUtils() {
  function addInvalidVotesToKandidat(
    kandidat: DseKandidat,
    numberOfVotes: number
  ) {
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen = currentUngueltigeStimmen + numberOfVotes;
  }

  function removeInvalidVotesFromKandidat(
    kandidat: DseKandidat,
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
