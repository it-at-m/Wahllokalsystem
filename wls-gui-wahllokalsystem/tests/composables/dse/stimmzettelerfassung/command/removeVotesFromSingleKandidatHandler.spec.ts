import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";

import {
  invalidKandidatOrdnungszahlenCommand,
  validKandidatOrdnungszahlen,
} from "@tests/utils/dse/commandTestTools.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useRemoveVotesFromSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/removeVotesFromSingleKandidatHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatRemoveVotesOrThrow: vi.fn(),
}));

describe("removeVotesFromSingleKandidatHandler.ts", () => {
  const { canHandle, handleOrThrow } =
    useRemoveVotesFromSingleKandidatHandler();

  describe("canHandle", () => {
    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithMinusAndVotes",
      (validOrdnungszahl) => {
        expect(canHandle(`${validOrdnungszahl}-3`)).toBe(true);
      }
    );

    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithMinusWithoutVotes",
      (validOrdnungszahl) => {
        expect(canHandle(`${validOrdnungszahl}-`)).toBe(true);
      }
    );

    it.each(invalidKandidatOrdnungszahlenCommand)(
      "should_returnFalse_when_command'%s'DoesNotMatchPattern",
      (command) => {
        expect(canHandle(command)).toBe(false);
      }
    );
  });

  describe("handleOrThrow", () => {
    let mockManagedStimmzettel: ManagedStimmzettel;

    beforeEach(() => {
      mockManagedStimmzettel = {
        kandidatRemoveEinzelstimmenOrThrow:
          mockDefinitions.kandidatRemoveVotesOrThrow,
      } as unknown as ManagedStimmzettel;
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatRemoveVotesOrThrow_when_commandIs'%s'WithMinusWithoutVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`${kandidatOrdnungszahl}-`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatRemoveVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatRemoveVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 1]);
      }
    );

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatRemoveVotesOrThrow_withParsedVotes_when_command'%s'ContainsMinusAndVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`${kandidatOrdnungszahl}-3`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatRemoveVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatRemoveVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 3]);
      }
    );

    it("should_throwCommandExecutionError_when_ordnungszahlIsInvalid", () => {
      expect(() => handleOrThrow("10", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_throwCommandExecutionError_when_ordnungszahlHasWrongFormat", () => {
      expect(() => handleOrThrow("abc", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_throwCommandExecutionError_when_commandDoesNotMatchToHandler", () => {
      expect(() => handleOrThrow("101-0", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatRemoveVotesOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatRemoveVotesOrThrow.mockImplementation(() => {
        throw managedStimmzettelError;
      });

      expect(() => handleOrThrow("101-1", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatRemoveVotesOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatRemoveVotesOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("101-1", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
