import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

export function useStimmzettelerfassungTeamStatusEntryTools() {
  const EQUAL = 0;
  const SMALLER_THAN = -1;
  const GREATER_THAN = 1;

  function compareByTeamID(
    entryA: StimmzettelerfassungTeamStatusEntry,
    entryB: StimmzettelerfassungTeamStatusEntry
  ): number {
    if (entryA.teamID === entryB.teamID) {
      return EQUAL;
    }

    return entryA.teamID > entryB.teamID ? GREATER_THAN : SMALLER_THAN;
  }

  return { compareByTeamID };
}
