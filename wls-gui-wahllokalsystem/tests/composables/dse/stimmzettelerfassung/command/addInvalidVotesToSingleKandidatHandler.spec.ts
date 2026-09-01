import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";

import {
  invalidKandidatOrdnungszahlenCommand,
  validKandidatOrdnungszahlen,
} from "@tests/utils/dse/commandTestTools.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useAddInvalidVotesToSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/addInvalidVotesToSingleKandidatHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatAddInvalidVotesOrThrow: vi.fn(),
}));

describe("addInvalidVotesToSingleKandidatHandler.ts", () => {
  const { canHandle, handleOrThrow } =
    useAddInvalidVotesToSingleKandidatHandler();

  describe("canHandle", () => {
    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithoutPlus",
      (validOrdnungszahl) => {
        expect(canHandle(`u${validOrdnungszahl}`)).toBe(true);
        expect(canHandle(`U${validOrdnungszahl}`)).toBe(true);
      }
    );

    it("should_returnTrue_when_commandContainsValidOrdnungszahlWithPlusAndVotes", () => {
      expect(canHandle("u101+3")).toBe(true);
      expect(canHandle("U101+3")).toBe(true);
    });

    it("should_returnTrue_when_commandContainsValidOrdnungszahlWithPlusWithoutVotes", () => {
      expect(canHandle("u101+")).toBe(true);
      expect(canHandle("U101+")).toBe(true);
    });

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
        kandidatAddUngueltigeStimmenOrThrow:
          mockDefinitions.kandidatAddInvalidVotesOrThrow,
      } as unknown as ManagedStimmzettel;
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatAddInvalidVotesOrThrow_when_commandIs'%s'ValidWithoutPlus",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`u${kandidatOrdnungszahl}`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatAddInvalidVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatAddInvalidVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 1]);
      }
    );

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatAddInvalidVotesOrThrowWithParsedVotes_when_command'%s'ContainsPlusAndVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`u${kandidatOrdnungszahl}+3`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatAddInvalidVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatAddInvalidVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 3]);
      }
    );

    it.each(validKandidatOrdnungszahlen)(
      "should_defaultCountInvalidVotesToOne_when_commandContains'%s'PlusWithoutVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`u${kandidatOrdnungszahl}+`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatAddInvalidVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatAddInvalidVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 1]);
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("u10", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("uabc", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatAddInvalidVotesOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatAddInvalidVotesOrThrow.mockImplementation(() => {
        throw managedStimmzettelError;
      });

      expect(() => handleOrThrow("u101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatAddInvalidVotesOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatAddInvalidVotesOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("u101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
