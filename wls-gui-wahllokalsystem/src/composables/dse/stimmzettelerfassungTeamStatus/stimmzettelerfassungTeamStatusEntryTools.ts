import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

export function useStimmzettelerfassungTeamStatusEntryTools() {
  const EQUAL = 0;
  const SMALER_THAN = -1;
  const GREATER_THAN = 1;

  function compareByTeamID(
    entryA: StimmzettelerfassungTeamStatusEntry,
    entryB: StimmzettelerfassungTeamStatusEntry
  ): number {
    if (entryA.teamID === entryB.teamID) {
      return EQUAL;
    }

    return entryA.teamID > entryB.teamID ? GREATER_THAN : SMALER_THAN;
  }

  return { compareByTeamID };
}
