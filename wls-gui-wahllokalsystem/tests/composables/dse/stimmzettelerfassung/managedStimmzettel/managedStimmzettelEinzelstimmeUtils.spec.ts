import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useManagedStimmzettelEinzelstimmeUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelEinzelstimmeUtils.ts";

describe("managedStimmzettelEinzelstimmeUtils.ts", () => {
  const { prepareManagedStimmzettelKandidat } =
    useManagedStimmzettelTestDataFactory();

  describe("addVotesToKandidat", () => {
    it("should_addVotes_when_einzelstimmenNull", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(null)
        .build();

      const { addVotesToKandidat } = useManagedStimmzettelEinzelstimmeUtils();
      addVotesToKandidat(kandidat, 3);

      expect(kandidat.einzelstimmen).toBe(3);
    });

    it("should_addVotes_when_einzelstimmenAlreadyExists", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(1)
        .build();

      const { addVotesToKandidat } = useManagedStimmzettelEinzelstimmeUtils();
      addVotesToKandidat(kandidat, 2);

      expect(kandidat.einzelstimmen).toBe(3);
    });
  });

  describe("removeVotesFromKandidat", () => {
    it("should_removeVotes_when_called", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .build();

      const { removeVotesFromKandidat } =
        useManagedStimmzettelEinzelstimmeUtils();
      removeVotesFromKandidat(kandidat, 2);

      expect(kandidat.einzelstimmen).toBe(2);
    });

    it("should_setVotesToNull_when_newValueIsZero", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .build();

      const { removeVotesFromKandidat } =
        useManagedStimmzettelEinzelstimmeUtils();
      removeVotesFromKandidat(kandidat, 4);

      expect(kandidat.einzelstimmen).toBe(null);
    });
  });
});
