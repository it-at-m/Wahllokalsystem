import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

export function useStimmzettelerfassungTeamStatusEntryTools() {
  const EQUAL = 0;

  function compareByTeamID(
    entryA: StimmzettelerfassungTeamStatusEntry,
    entryB: StimmzettelerfassungTeamStatusEntry
  ): number {
    if (entryA.teamID === entryB.teamID) {
      return EQUAL;
    }

    return entryA.teamID.localeCompare(entryB.teamID);
  }

  return { compareByTeamID };
}
