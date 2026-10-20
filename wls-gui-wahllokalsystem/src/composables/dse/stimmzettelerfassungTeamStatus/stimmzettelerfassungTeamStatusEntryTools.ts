import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

export function useStimmzettelerfassungTeamStatusEntryTools() {
  function compareByTeamID(
    entryA: StimmzettelerfassungTeamStatusEntry,
    entryB: StimmzettelerfassungTeamStatusEntry
  ): number {
    return entryA.teamID.localeCompare(entryB.teamID);
  }

  return { compareByTeamID };
}
