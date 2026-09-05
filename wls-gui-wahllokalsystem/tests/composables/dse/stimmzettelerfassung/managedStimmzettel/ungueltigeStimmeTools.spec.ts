import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useUngueltigeStimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/ungueltigeStimmeTools.ts";

describe("ungueltigeStimmeTools.ts", () => {
  const { prepareManagedStimmzettelKandidat } =
    useManagedStimmzettelTestDataFactory();

  describe("addInvalidVotesToKandidat", () => {
    it("should_addInvalidVotes_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ungueltigeStimmen(null)
        .build();

      const { addInvalidVotesToKandidat } = useUngueltigeStimmeTools();
      addInvalidVotesToKandidat(kandidat, 3);

      expect(kandidat.ungueltigeStimmen).toBe(3);
    });
  });

  describe("removeInvalidVotesFromKandidat", () => {
    it("should_removeInvalidVotes_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .ungueltigeStimmen(4)
        .build();

      const { removeInvalidVotesFromKandidat } = useUngueltigeStimmeTools();
      removeInvalidVotesFromKandidat(kandidat, 2);

      expect(kandidat.ungueltigeStimmen).toBe(2);
    });
  });
});
