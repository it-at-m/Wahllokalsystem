import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import { useStimmzettelChangeHistory } from "@/composables/dse/stimmzettelerfassung/stimmzettelChangeHistory.ts";
import { InputHistoryTypeEnum } from "@/types/dse/stimmzettelerfassung/InputHistoryTypeEnum.ts";

describe("stimmzettelChangeHistory.ts", () => {
  const { createStimmzettelKandidat, createStimmzettelWahlvorschlag } =
    useStimmzettelTestDataFactory();

  let changeHistory: ReturnType<typeof useStimmzettelChangeHistory>;

  beforeEach(() => {
    changeHistory = useStimmzettelChangeHistory();
  });

  describe("changeHistoryInReverseOrder", () => {
    it("should_returnEmptyHistory_when_noChangeWasRegistered", () => {
      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([]);
      expect(changeHistory.lastUsedKandidat.value).toBeNull();
      expect(changeHistory.lastUsedWahlvorschlag.value).toBeNull();
    });

    it("should_returnHistoryInReverseOrder_when_multipleChangesWereRegistered", () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Kandidat 1",
      };
      const secondKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 102,
        name: "Kandidat 2",
      };
      const wahlvorschlag = {
        ...createStimmzettelWahlvorschlag(),
        kurzname: "WV",
      };

      changeHistory.registerKandidatEinzelstimmenAdded(firstKandidat, 1);
      changeHistory.registerKandidatStreichungSet(secondKandidat);
      changeHistory.registerWahlvorschlagSelected(wahlvorschlag);

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.SET_WAHLVORSCHLAG,
          text: ["WV"],
        },
        {
          type: InputHistoryTypeEnum.DISCARD_KANDIDAT,
          text: ["102", "Kandidat 2"],
        },
        {
          type: InputHistoryTypeEnum.ADD_USER_VOTE,
          text: ["101 + 1 Stimme", "Kandidat 1"],
        },
      ]);
    });
  });

  describe("registerKandidatEinzelstimmenAdded", () => {
    it("should_addSingularVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatEinzelstimmenAdded(kandidat, 1);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.ADD_USER_VOTE,
          text: ["101 + 1 Stimme", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });

    it("should_addPluralVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatEinzelstimmenAdded(kandidat, 2);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.ADD_USER_VOTE,
          text: ["101 + 2 Stimmen", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatEinzelstimmenRemoved", () => {
    it("should_addSingularVoteRemovalHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatEinzelstimmenRemoved(kandidat, 1);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
          text: ["101 - 1 Stimme", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });

    it("should_addPluralVoteRemovalHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatEinzelstimmenRemoved(kandidat, 2);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
          text: ["101 - 2 Stimmen", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatEinzelstimmenRangeSet", () => {
    it("should_addSingularVoteRangeHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", async () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
      };
      const lastKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 103,
      };

      changeHistory.registerKandidatEinzelstimmenRangeSet(
        [firstKandidat, lastKandidat],
        1
      );

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.VOTE_RANGE,
          text: ["101-103 + 1 Stimme"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(lastKandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        lastKandidat.owningWahlvorschlag
      );
    });

    it("should_addPluralVoteRangeHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", async () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
      };
      const lastKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 103,
      };

      changeHistory.registerKandidatEinzelstimmenRangeSet(
        [firstKandidat, lastKandidat],
        2
      );

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.VOTE_RANGE,
          text: ["101-103 + 2 Stimmen"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(lastKandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        lastKandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatUngueltigeStimmenAdded", () => {
    it("should_addSingularInvalidVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatUngueltigeStimmenAdded(kandidat, 1);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.ADD_USER_VOTE,
          text: ["101 + 1 ungültige Stimme", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });

    it("should_addPluralInvalidVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatUngueltigeStimmenAdded(kandidat, 2);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.ADD_USER_VOTE,
          text: ["101 + 2 ungültige Stimmen", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatUngueltigeStimmenRemoved", () => {
    it("should_addSingularInvalidVoteRemovalHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatUngueltigeStimmenRemoved(kandidat, 1);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
          text: ["101 - 1 ungültige Stimme", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });

    it("should_addPluralInvalidVoteRemovalHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatUngueltigeStimmenRemoved(kandidat, 2);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
          text: ["101 - 2 ungültige Stimmen", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatStreichungSet", () => {
    it("should_addDiscardKandidatHistoryEntryAndUpdateLastUsedKandidat_when_kandidatWasGiven", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatStreichungSet(kandidat);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.DISCARD_KANDIDAT,
          text: ["101", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatStreichungUnset", () => {
    it("should_addRevokeDiscardKandidatHistoryEntryAndUpdateLastUsedKandidat_when_kandidatWasGiven", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatStreichungUnset(kandidat);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.REVOKE_DISCARDED_KANDIDAT,
          text: ["101", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatStreichungRangeSet", () => {
    it("should_addDiscardRangeHistoryEntryAndUpdateLastUsedKandidat_when_kandidatenWereGiven", async () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
      };
      const lastKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 103,
      };

      changeHistory.registerKandidatStreichungRangeSet([
        firstKandidat,
        lastKandidat,
      ]);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.DISCARD_RANGE,
          text: ["101-103"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(lastKandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        lastKandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerKandidatStreichungRangeUnset", () => {
    it("should_addRevokeDiscardRangeHistoryEntryAndUpdateLastUsedKandidat_when_kandidatenWereGiven", async () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
      };
      const lastKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 103,
      };

      changeHistory.registerKandidatStreichungRangeUnset([
        firstKandidat,
        lastKandidat,
      ]);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.REVOKE_DISCARDED_KANDIDAT,
          text: ["101-103"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(lastKandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        lastKandidat.owningWahlvorschlag
      );
    });
  });

  describe("registerWahlvorschlagSelected", () => {
    it("should_addWahlvorschlagHistoryEntryAndUpdateLastUsedWahlvorschlag_when_wahlvorschlagWasGiven", async () => {
      const wahlvorschlag = {
        ...createStimmzettelWahlvorschlag(),
        kurzname: "WV",
      };

      changeHistory.registerWahlvorschlagSelected(wahlvorschlag);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.SET_WAHLVORSCHLAG,
          text: ["WV"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toBeNull();
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        wahlvorschlag
      );
    });
  });

  describe("registerWahlvorschlagDeselected", () => {
    it("should_addRevokeWahlvorschlagHistoryEntryAndUpdateLastUsedWahlvorschlag_when_wahlvorschlagWasGiven", async () => {
      const wahlvorschlag = {
        ...createStimmzettelWahlvorschlag(),
        kurzname: "WV",
      };

      changeHistory.registerWahlvorschlagDeselected(wahlvorschlag);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.REVOKE_WAHLVORSCHLAG,
          text: ["WV"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toBeNull();
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        wahlvorschlag
      );
    });
  });

  describe("reset", () => {
    it("should_clearHistoryAndLastUsedData_when_called", async () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      changeHistory.registerKandidatEinzelstimmenAdded(kandidat, 1);

      await flushPromises();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([
        {
          type: InputHistoryTypeEnum.ADD_USER_VOTE,
          text: ["101 + 1 Stimme", "Max Mustermann"],
        },
      ]);
      expect(changeHistory.lastUsedKandidat.value).toStrictEqual(kandidat);
      expect(changeHistory.lastUsedWahlvorschlag.value).toStrictEqual(
        kandidat.owningWahlvorschlag
      );

      changeHistory.reset();

      expect(changeHistory.changeHistoryInReverseOrder.value).toStrictEqual([]);
      expect(changeHistory.lastUsedKandidat.value).toBeNull();
      expect(changeHistory.lastUsedWahlvorschlag.value).toBeNull();
    });
  });
});
