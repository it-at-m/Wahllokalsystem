import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useManagedStimmzettelUngueltigeStimmeUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelUngueltigeStimmeUtils.ts";

describe("managedStimmzettelUngueltigeStimmeUtils.ts", () => {
  const { prepareManagedStimmzettelKandidat } =
    useManagedStimmzettelTestDataFactory();

  describe("addInvalidVotesToKandidat", () => {
    it("should_addInvalidVotes_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ungueltigeStimmen(null)
        .build();

      const { addInvalidVotesToKandidat } =
        useManagedStimmzettelUngueltigeStimmeUtils();
      addInvalidVotesToKandidat(kandidat, 3);

      expect(kandidat.ungueltigeStimmen).toBe(3);
    });
  });

  describe("removeInvalidVotesFromKandidat", () => {
    it("should_removeInvalidVotes_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ungueltigeStimmen(4)
        .build();

      const { removeInvalidVotesFromKandidat } =
        useManagedStimmzettelUngueltigeStimmeUtils();
      removeInvalidVotesFromKandidat(kandidat, 2);

      expect(kandidat.ungueltigeStimmen).toBe(2);
    });

    it("should_setInvalidVotesToNull_when_newValueIsZero", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ungueltigeStimmen(4)
        .build();

      const { removeInvalidVotesFromKandidat } =
        useManagedStimmzettelUngueltigeStimmeUtils();
      removeInvalidVotesFromKandidat(kandidat, 4);

      expect(kandidat.ungueltigeStimmen).toBe(null);
    });

    it("should_setInvalidVotesToNull_when_newValueIsLowerThanZero", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ungueltigeStimmen(4)
        .build();

      const { removeInvalidVotesFromKandidat } =
        useManagedStimmzettelUngueltigeStimmeUtils();
      removeInvalidVotesFromKandidat(kandidat, 5);

      expect(kandidat.ungueltigeStimmen).toBe(null);
    });
  });
});
