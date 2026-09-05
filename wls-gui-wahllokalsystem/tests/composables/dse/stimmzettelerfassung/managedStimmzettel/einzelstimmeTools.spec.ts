import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useEinzelstimmeTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/einzelstimmeTools.ts";

describe("einzelstimmeTools.ts", () => {
  const { prepareManagedStimmzettelKandidat } =
    useManagedStimmzettelTestDataFactory();

  describe("addVotesToKandidat", () => {
    it("should_addVotes_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(null)
        .build();

      const { addVotesToKandidat } = useEinzelstimmeTools();
      addVotesToKandidat(kandidat, 3);

      expect(kandidat.einzelstimmen).toBe(3);
    });
  });

  describe("removeVotesFromKandidat", () => {
    it("should_removeVotes_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .build();

      const { removeVotesFromKandidat } = useEinzelstimmeTools();
      removeVotesFromKandidat(kandidat, 2);

      expect(kandidat.einzelstimmen).toBe(2);
    });

    it("should_setVotesToNull_when_newValueIsZero", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .build();

      const { removeVotesFromKandidat } = useEinzelstimmeTools();
      removeVotesFromKandidat(kandidat, 4);

      expect(kandidat.einzelstimmen).toBe(null);
    });
  });
});
