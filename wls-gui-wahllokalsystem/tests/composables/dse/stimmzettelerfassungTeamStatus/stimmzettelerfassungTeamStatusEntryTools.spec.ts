import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { describe, expect, it } from "vitest";

import { useStimmzettelerfassungTeamStatusEntryTools } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusEntryTools.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

describe("stimmzettelerfassungTeamStatusEntryTools.ts", () => {
  const { compareByTeamID } = useStimmzettelerfassungTeamStatusEntryTools();

  const createEntry = (
    teamID: string
  ): StimmzettelerfassungTeamStatusEntry => ({
    teamID,
    status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
  });

  describe("compareByTeamID", () => {
    it("should_returnZero_when_teamIDsAreEqual", () => {
      const result = compareByTeamID(
        createEntry("team1"),
        createEntry("team1")
      );

      expect(result).toBe(0);
    });

    it("should_returnNegativeValue_when_firstTeamIDIsSmaller", () => {
      const result = compareByTeamID(
        createEntry("team1"),
        createEntry("team2")
      );

      expect(result).toBeLessThan(0);
    });

    it("should_returnPositiveValue_when_firstTeamIDIsGreater", () => {
      const result = compareByTeamID(
        createEntry("team2"),
        createEntry("team1")
      );

      expect(result).toBeGreaterThan(0);
    });
  });
});
