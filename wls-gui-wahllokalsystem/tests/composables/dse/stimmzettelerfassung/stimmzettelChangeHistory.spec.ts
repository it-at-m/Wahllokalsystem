import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useStimmzettelChangeHistory } from "@/composables/dse/stimmzettelerfassung/stimmzettelChangeHistory.ts";
import { InputHistoryTypeEnum } from "@/types/dse/stimmzettelerfassung/InputHistoryTypeEnum.ts";

describe("stimmzettelChangeHistory.ts", () => {
  const { createStimmzettelKandidat, createStimmzettelWahlvorschlag } =
    useStimmzettelTestDataFactory();

  describe("changeHistoryInReverseOrder", () => {
    it("should_returnEmptyHistory_when_noChangeWasRegistered", () => {
      const changeHistory = useStimmzettelChangeHistory();

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

      const changeHistory = useStimmzettelChangeHistory();

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
    it("should_addSingularVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatEinzelstimmenAdded(kandidat, 1);

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

    it("should_addPluralVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatEinzelstimmenAdded(kandidat, 2);

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

  describe("registerKandidatEinzelstimmenRangeSet", () => {
    it("should_addSingularVoteRangeHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
      };
      const lastKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 103,
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatEinzelstimmenRangeSet(
        [firstKandidat, lastKandidat],
        1
      );

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

    it("should_addPluralVoteRangeHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
      };
      const lastKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 103,
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatEinzelstimmenRangeSet(
        [firstKandidat, lastKandidat],
        2
      );

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
    it("should_addSingularInvalidVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsOne", () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatUngueltigeStimmenAdded(kandidat, 1);

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

    it("should_addPluralInvalidVoteHistoryEntryAndUpdateLastUsedKandidat_when_countIsGreaterThanOne", () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatUngueltigeStimmenAdded(kandidat, 2);

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

  describe("registerKandidatStreichungSet", () => {
    it("should_addDiscardKandidatHistoryEntryAndUpdateLastUsedKandidat_when_kandidatWasGiven", () => {
      const kandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
        name: "Max Mustermann",
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatStreichungSet(kandidat);

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

  describe("registerKandidatStreichungRangeSet", () => {
    it("should_addDiscardRangeHistoryEntryAndUpdateLastUsedKandidat_when_kandidatenWereGiven", () => {
      const firstKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 101,
      };
      const lastKandidat = {
        ...createStimmzettelKandidat(),
        ordnungszahl: 103,
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerKandidatStreichungRangeSet([
        firstKandidat,
        lastKandidat,
      ]);

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

  describe("registerWahlvorschlagSelected", () => {
    it("should_addWahlvorschlagHistoryEntryAndUpdateLastUsedWahlvorschlag_when_wahlvorschlagWasGiven", () => {
      const wahlvorschlag = {
        ...createStimmzettelWahlvorschlag(),
        kurzname: "WV",
      };

      const changeHistory = useStimmzettelChangeHistory();

      changeHistory.registerWahlvorschlagSelected(wahlvorschlag);

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
});
