import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";

import {
  invalidWahlvorschlagOrdnungszahlen,
  validWahlvorschlagOrdnungszahlen,
} from "@tests/utils/dse/commandTestTools.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useRemoveVotesFromWahlvorschlagHandler } from "@/composables/dse/stimmzettelerfassung/command/removeVotesFromWahlvorschlagHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  wahlvorschlagRemoveVotesOrThrow: vi.fn(),
}));

describe("removeVotesFromWahlvorschlagHandler.ts", () => {
  const { canHandle, handleOrThrow } = useRemoveVotesFromWahlvorschlagHandler();

  const commandToExpectedOrdnungszahl: [string, number][] = [
    ["1-", 1],
    ["100-", 1],
    ["1000-", 10],
  ];

  describe("canHandle", () => {
    it.each(validWahlvorschlagOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithMinus",
      (validOrdnungszahl) => {
        expect(canHandle(`${validOrdnungszahl}-`)).toBe(true);
      }
    );

    it.each(invalidWahlvorschlagOrdnungszahlen)(
      "should_returnFalse_when_command'%s'DoesNotMatchPattern",
      (command) => {
        expect(canHandle(`${command}-`)).toBe(false);
      }
    );
  });

  describe("handleOrThrow", () => {
    let mockManagedStimmzettel: ManagedStimmzettel;

    beforeEach(() => {
      mockManagedStimmzettel = {
        wahlvorschlagRemoveVotesOrThrow:
          mockDefinitions.wahlvorschlagRemoveVotesOrThrow,
      } as unknown as ManagedStimmzettel;
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(commandToExpectedOrdnungszahl)(
      "should_callWahlvorschlagRemoveVotesOrThrow_when_commandIs'%s'",
      (command, expectedOrdnungszahl) => {
        handleOrThrow(command, mockManagedStimmzettel);

        expect(
          mockDefinitions.wahlvorschlagRemoveVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.wahlvorschlagRemoveVotesOrThrow.mock.calls[0]
        ).toStrictEqual([expectedOrdnungszahl]);
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("0-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("abc-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_wahlvorschlagRemoveVotesOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Wahlvorschlag existiert nicht."
      );
      mockDefinitions.wahlvorschlagRemoveVotesOrThrow.mockImplementation(() => {
        throw managedStimmzettelError;
      });

      expect(() => handleOrThrow("100-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_wahlvorschlagRemoveVotesOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.wahlvorschlagRemoveVotesOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("100-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
