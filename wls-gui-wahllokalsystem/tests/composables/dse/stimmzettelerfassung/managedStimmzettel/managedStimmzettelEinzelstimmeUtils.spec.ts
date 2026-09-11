import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useManagedStimmzettelEinzelstimmeUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelEinzelstimmeUtils.ts";

const mockDefinitions = vi.hoisted(() => ({
  getTotalEinzelstimmenOfKandidatenWithSameId: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/kandidatTools.ts"),
  async (importOriginal) => {
    const org = await importOriginal();
    return {
      useKandidatTools: () => ({
        ...org.useKandidatTools(),
        getTotalEinzelstimmenOfKandidatenWithSameId:
          mockDefinitions.getTotalEinzelstimmenOfKandidatenWithSameId,
      }),
    };
  }
);

const { generateRandomNumber } = useCommonTestDataFactory();

describe("managedStimmzettelEinzelstimmeUtils.ts", () => {
  const MAX_EINZELSTIMMEN = 3;
  const { prepareManagedStimmzettelKandidat } =
    useManagedStimmzettelTestDataFactory();

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("addVotesToKandidat", () => {
    it("should_addVotes_when_einzelstimmenNull", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(null)
        .durchgestrichen(false)
        .build();

      mockDefinitions.getTotalEinzelstimmenOfKandidatenWithSameId.mockReturnValue(
        0
      );

      const { addVotesToKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      addVotesToKandidat(kandidat, 3);

      expect(kandidat.einzelstimmen).toBe(3);
    });

    it("should_addVotesToEinzelStimmenOnly_when_einzelstimmenAlreadyExistsAndNewVotesAreBelowMaxEinzelstimmen", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(1)
        .durchgestrichen(false)
        .build();

      mockDefinitions.getTotalEinzelstimmenOfKandidatenWithSameId.mockReturnValue(
        1
      );

      const { addVotesToKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      addVotesToKandidat(kandidat, 2);

      expect(kandidat.einzelstimmen).toBe(3);
    });

    it.each([null, 2])(
      "should_addVotesToEinzelStimmenAndUngueltigeStimmen_when_newVotesIsAboveMaxEinzelstimmenAndUngueltigeStimmenAre'%s'",
      (countInvalidVotesGiven) => {
        const kandidat = prepareManagedStimmzettelKandidat()
          .einzelstimmen(1)
          .ungueltigeStimmen(countInvalidVotesGiven)
          .durchgestrichen(false)
          .build();

        mockDefinitions.getTotalEinzelstimmenOfKandidatenWithSameId.mockReturnValue(
          1
        );

        const { addVotesToKandidat } =
          useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
        addVotesToKandidat(kandidat, MAX_EINZELSTIMMEN + 1);

        expect(kandidat.einzelstimmen).toBe(MAX_EINZELSTIMMEN);
        expect(kandidat.ungueltigeStimmen).toBe(
          (countInvalidVotesGiven ?? 0) + 2
        );
      }
    );

    it.each([null, 2])(
      "should_setOnlyUngueltigeStimmen_when_otherKandidatenAlreadyUsedMaxEinzelstimmenKandidatHas'%s'UngueltigeStimmen",
      (countInvalidVotesGiven) => {
        const kandidat = prepareManagedStimmzettelKandidat()
          .einzelstimmen(null)
          .ungueltigeStimmen(countInvalidVotesGiven)
          .durchgestrichen(false)
          .build();

        mockDefinitions.getTotalEinzelstimmenOfKandidatenWithSameId.mockReturnValue(
          MAX_EINZELSTIMMEN
        );

        const { addVotesToKandidat } =
          useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
        addVotesToKandidat(kandidat, MAX_EINZELSTIMMEN + 1);

        expect(kandidat.einzelstimmen).toBe(null);
        expect(kandidat.ungueltigeStimmen).toBe(
          (countInvalidVotesGiven ?? 0) + MAX_EINZELSTIMMEN + 1
        );
      }
    );

    it.each([null, 2])(
      "should_addVotesToEinzelStimmenAndUngueltigeStimmen_when_newVotesIsAboveMaxEinzelstimmenCauseOtherKandidatenAlreadyUsedSomeEinzelstimmenAndUngueltigeStimmenAre'%s'",
      (countInvalidVotesGiven) => {
        const kandidat = prepareManagedStimmzettelKandidat()
          .einzelstimmen(null)
          .ungueltigeStimmen(countInvalidVotesGiven)
          .durchgestrichen(false)
          .build();

        mockDefinitions.getTotalEinzelstimmenOfKandidatenWithSameId.mockReturnValue(
          MAX_EINZELSTIMMEN - 1
        );

        const { addVotesToKandidat } =
          useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
        addVotesToKandidat(kandidat, MAX_EINZELSTIMMEN + 1);

        expect(kandidat.einzelstimmen).toBe(1);
        expect(kandidat.ungueltigeStimmen).toBe(
          (countInvalidVotesGiven ?? 0) + MAX_EINZELSTIMMEN
        );
      }
    );

    it("should_addAllVotesAsInvalidVotes_when_kandidatIsGestrichen", () => {
      const initalUngueltigeStimmen = generateRandomNumber(2);
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(null)
        .ungueltigeStimmen(initalUngueltigeStimmen)
        .durchgestrichen(true)
        .build();

      const votesToAdd = generateRandomNumber(2);

      mockDefinitions.getTotalEinzelstimmenOfKandidatenWithSameId.mockReturnValue(
        initalUngueltigeStimmen + MAX_EINZELSTIMMEN + votesToAdd
      );

      const { addVotesToKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      addVotesToKandidat(kandidat, votesToAdd);

      expect(kandidat.einzelstimmen).toBe(null);
      expect(kandidat.ungueltigeStimmen).toBe(
        initalUngueltigeStimmen + votesToAdd
      );
    });
  });

  describe("removeVotesFromKandidat", () => {
    it("should_removeEinzelstimmenVotes_when_noUngueltigeStimmenAreGiven", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .ungueltigeStimmen(null)
        .build();

      const { removeVotesFromKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      removeVotesFromKandidat(kandidat, 2);

      expect(kandidat.einzelstimmen).toBe(2);
    });

    it("should_setEinzelstimmenVotesToNull_when_newValueIsZero", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .ungueltigeStimmen(null)
        .build();

      const { removeVotesFromKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      removeVotesFromKandidat(kandidat, 4);

      expect(kandidat.einzelstimmen).toBe(null);
    });

    it("should_reduceUngueltigeStimmenOnly_when_ungueltigeStimmenAreGivenAndLargerThanVotesToRemove", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .ungueltigeStimmen(4)
        .build();

      const { removeVotesFromKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      removeVotesFromKandidat(kandidat, 2);

      expect(kandidat.einzelstimmen).toBe(4);
      expect(kandidat.ungueltigeStimmen).toBe(2);
    });

    it("should_reduceUngueltigeAndEinzelstimmen_when_ungueltigeStimmenAreGivenButNotLargerThanVotesToRemove", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .ungueltigeStimmen(4)
        .build();

      const { removeVotesFromKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      removeVotesFromKandidat(kandidat, 6);

      expect(kandidat.einzelstimmen).toBe(2);
      expect(kandidat.ungueltigeStimmen).toBe(null);
    });

    it("should_reduceUngueltigeAndEinzelstimmenToZero_when_bothAreGivenAndNumberToRemoveIsEqualSum", () => {
      const kandidat = prepareManagedStimmzettelKandidat()
        .einzelstimmen(4)
        .ungueltigeStimmen(4)
        .build();

      const { removeVotesFromKandidat } =
        useManagedStimmzettelEinzelstimmeUtils(MAX_EINZELSTIMMEN);
      removeVotesFromKandidat(kandidat, 8);

      expect(kandidat.einzelstimmen).toBe(null);
      expect(kandidat.ungueltigeStimmen).toBe(null);
    });
  });
});
