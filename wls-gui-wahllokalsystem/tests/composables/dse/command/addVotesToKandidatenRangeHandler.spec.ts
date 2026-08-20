import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";

import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useAddVotesToKandidatenRangeHandler } from "@/composables/dse/command/addVotesToKandidatenRangeHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatenAddStimmenInRangeOrThrow: vi.fn(),
}));

describe("addVotesToKandidatenRangeHandler.ts", () => {
  const { canHandle, handleOrThrow } = useAddVotesToKandidatenRangeHandler();
  const { prepareManagedStimmzettelStimmzettel } =
    useManagedStimmzettelTestDataFactory();

  const validRanges: [number, number][] = [
    [101, 103],
    [201, 299],
    [999, 1001],
    [1099, 1010],
  ];

  describe("canHandle", () => {
    it.each(validRanges)(
      "should_returnTrue_when_commandContainsValidRange'%s-%s'WithoutPlus",
      (lower, upper) => {
        expect(canHandle(`${lower}-${upper}`)).toBe(true);
      }
    );

    it("should_returnTrue_when_commandContainsValidRangeWithPlusAndVotes", () => {
      expect(canHandle("101-103+3")).toBe(true);
    });

    it("should_returnTrue_when_commandContainsValidRangeWithPlusAndOneVoteExplicit", () => {
      expect(canHandle("101-103+1")).toBe(true);
    });

    it.each([
      "10-101",
      "abc-200",
      "100-1000",
      "101-100",
      "101-1000",
      "101-",
      "101-103+",
      "101-103+abc",
      "101-103+0",
    ])("should_returnFalse_when_command'%s'DoesNotMatchPattern", (command) => {
      expect(canHandle(command)).toBe(false);
    });
  });

  describe("handleOrThrow", () => {
    let mockManagedStimmzettel: ManagedStimmzettel;

    beforeEach(() => {
      mockManagedStimmzettel = {
        kandidatenAddStimmenInRangeOrThrow:
          mockDefinitions.kandidatenAddStimmenInRangeOrThrow as unknown as (
            lower: number,
            upper: number,
            votesToAdd: number
          ) => void,
        kandidatAddEinzelstimmenOrThrow: vi.fn(),
        kandidatAddUngueltigeStimmenOrThrow: vi.fn(),
        kandidatAddStreichungOrThrow: vi.fn(),
        kandidatenStreichungenInRangeOrThrow: vi.fn(),
        wahlvorschlagAddVotesOrThrow: vi.fn(),
        stimmzettel: computed(() =>
          prepareManagedStimmzettelStimmzettel().build()
        ),
        changeHistoryInReverseOrder: computed(() => []),
        wahlvorschlaegeWithListenkreuz: computed(() => []),
        stimmenSummary: computed(() => ({
          einzelstimmen: 0,
          reststimmen: 0,
          streichungen: 0,
          ungueltigeStimmen: 0,
        })),
      };
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(validRanges)(
      "should_callKandidatenAddStimmenInRangeOrThrow_when_commandIs'%s-%s'ValidWithoutPlus",
      (lower, upper) => {
        handleOrThrow(`${lower}-${upper}`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatenAddStimmenInRangeOrThrow
        ).toHaveBeenCalledTimes(1);
        const call = mockDefinitions.kandidatenAddStimmenInRangeOrThrow.mock
          .calls[0] as [number, number, number];
        expect(call[0]).toBe(Math.min(lower, upper));
        expect(call[1]).toBe(Math.max(lower, upper));
        expect(call[2]).toBe(1);
      }
    );

    it.each(validRanges)(
      "should_callKandidatenAddStimmenInRangeOrThrow_withParsedVotes_when_command'%s-%s'ContainsPlusAndVotes",
      (lower, upper) => {
        handleOrThrow(`${lower}-${upper}+3`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatenAddStimmenInRangeOrThrow
        ).toHaveBeenCalledTimes(1);
        const call = mockDefinitions.kandidatenAddStimmenInRangeOrThrow.mock
          .calls[0] as [number, number, number];
        expect(call[0]).toBe(Math.min(lower, upper));
        expect(call[1]).toBe(Math.max(lower, upper));
        expect(call[2]).toBe(3);
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("10-101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("abc-200", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatenAddStimmenInRangeOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatenAddStimmenInRangeOrThrow.mockImplementation(
        () => {
          throw managedStimmzettelError;
        }
      );

      expect(() => handleOrThrow("101-103", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatenAddStimmenInRangeOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatenAddStimmenInRangeOrThrow.mockImplementation(
        () => {
          throw new Error("unexpected error");
        }
      );

      expect(() => handleOrThrow("101-103", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
