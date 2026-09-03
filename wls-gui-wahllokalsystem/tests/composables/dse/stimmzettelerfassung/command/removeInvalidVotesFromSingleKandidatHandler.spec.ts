import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";

import {
  invalidKandidatOrdnungszahlenCommand,
  validKandidatOrdnungszahlen,
} from "@tests/utils/dse/commandTestTools.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useRemoveInvalidVotesFromSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/removeInvalidVotesFromSingleKandidatHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatRemoveInvalidVotesOrThrow: vi.fn(),
}));

describe("removeInvalidVotesFromSingleKandidatHandler.ts", () => {
  const { canHandle, handleOrThrow } =
    useRemoveInvalidVotesFromSingleKandidatHandler();

  describe("canHandle", () => {
    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithMinusAndVotes",
      (validOrdnungszahl) => {
        expect(canHandle(`u${validOrdnungszahl}-3`)).toBe(true);
        expect(canHandle(`U${validOrdnungszahl}-3`)).toBe(true);
      }
    );

    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithMinusWithoutVotes",
      (validOrdnungszahl) => {
        expect(canHandle(`u${validOrdnungszahl}-`)).toBe(true);
        expect(canHandle(`U${validOrdnungszahl}-`)).toBe(true);
      }
    );

    it.each(invalidKandidatOrdnungszahlenCommand)(
      "should_returnFalse_when_command'%s'DoesNotMatchPattern",
      (command) => {
        expect(canHandle(`u${command}`)).toBe(false);
      }
    );
  });

  describe("handleOrThrow", () => {
    let mockManagedStimmzettel: ManagedStimmzettel;

    beforeEach(() => {
      mockManagedStimmzettel = {
        kandidatRemoveUngueltigeStimmenOrThrow:
          mockDefinitions.kandidatRemoveInvalidVotesOrThrow,
      } as unknown as ManagedStimmzettel;
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatRemoveInvalidVotesOrThrow_when_commandIs'%s'WithMinusWithoutVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`u${kandidatOrdnungszahl}-`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatRemoveInvalidVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatRemoveInvalidVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 1]);
      }
    );

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatRemoveInvalidVotesOrThrow_withParsedVotes_when_command'%s'ContainsMinusAndVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`u${kandidatOrdnungszahl}-3`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatRemoveInvalidVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatRemoveInvalidVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 3]);
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("u10", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("uabc", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("u101-0", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatRemoveInvalidVotesOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatRemoveInvalidVotesOrThrow.mockImplementation(
        () => {
          throw managedStimmzettelError;
        }
      );

      expect(() => handleOrThrow("u101-1", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatRemoveInvalidVotesOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatRemoveInvalidVotesOrThrow.mockImplementation(
        () => {
          throw new Error("unexpected error");
        }
      );

      expect(() => handleOrThrow("u101-1", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
