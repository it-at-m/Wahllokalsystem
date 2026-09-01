import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";

import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useAddVotesToWahlvorschlagHandler } from "@/composables/dse/stimmzettelerfassung/command/addVotesToWahlvorschlagHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  wahlvorschlagAddVotesOrThrow: vi.fn(),
}));

describe("addVotesToWahlvorschlagHandler.ts", () => {
  const { canHandle, handleOrThrow } = useAddVotesToWahlvorschlagHandler();

  const validWahlvorschlagOrdnungszahlen = [1, 2, 9, 10, 11, 99, 100, 1000];
  const commandToExpectedOrdnungszahl: [string, number][] = [
    ["1", 1],
    ["100", 1],
    ["1000", 10],
  ];

  describe("canHandle", () => {
    it.each(validWahlvorschlagOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'",
      (validOrdnungszahl) => {
        expect(canHandle(`${validOrdnungszahl}`)).toBe(true);
      }
    );

    it.each(["0", "abc", "010", "101", "999", "1001", "9999"])(
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
        wahlvorschlagAddVotesOrThrow:
          mockDefinitions.wahlvorschlagAddVotesOrThrow,
      } as unknown as ManagedStimmzettel;
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(commandToExpectedOrdnungszahl)(
      "should_callWahlvorschlagAddVotesOrThrow_when_commandIs'%s'",
      (command, expectedOrdnungszahl) => {
        handleOrThrow(command, mockManagedStimmzettel);

        expect(
          mockDefinitions.wahlvorschlagAddVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.wahlvorschlagAddVotesOrThrow.mock.calls[0]
        ).toStrictEqual([expectedOrdnungszahl]);
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("0", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("abc", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_wahlvorschlagAddVotesOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Wahlvorschlag existiert nicht."
      );
      mockDefinitions.wahlvorschlagAddVotesOrThrow.mockImplementation(() => {
        throw managedStimmzettelError;
      });

      expect(() => handleOrThrow("100", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_wahlvorschlagAddVotesOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.wahlvorschlagAddVotesOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("100", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
