import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";

import {
  invalidCommandRanges,
  validRanges,
} from "@tests/utils/dse/commandTestTools.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useRemoveStreichungFromKandidatenRangeHandler } from "@/composables/dse/stimmzettelerfassung/command/removeStreichungFromKandidatenRangeHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatenRemoveStreichungenInRangeOrThrow: vi.fn(),
}));

describe("removeStreichungFromKandidatenRangeHandler.ts", () => {
  const { canHandle, handleOrThrow } =
    useRemoveStreichungFromKandidatenRangeHandler();

  describe("canHandle", () => {
    it.each(validRanges)(
      "should_returnTrue_when_commandContainsValidRange'%s-%s'WithTrailingMinus",
      (lower, upper) => {
        expect(canHandle(`s${lower}-${upper}-`)).toBe(true);
        expect(canHandle(`S${lower}-${upper}-`)).toBe(true);
      }
    );

    it.each(invalidCommandRanges)(
      "should_returnFalse_when_command'%s'DoesNotMatchPattern",
      (command) => {
        expect(canHandle(`s${command}`)).toBe(false);
      }
    );
  });

  describe("handleOrThrow", () => {
    let mockManagedStimmzettel: ManagedStimmzettel;

    beforeEach(() => {
      mockManagedStimmzettel = {
        kandidatenRemoveStreichungenInRangeOrThrow:
          mockDefinitions.kandidatenRemoveStreichungenInRangeOrThrow,
      } as unknown as ManagedStimmzettel;
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(validRanges)(
      "should_callKandidatenRemoveStreichungenInRangeOrThrow_when_commandIs'%s-%s-'",
      (lower, upper) => {
        handleOrThrow(`s${lower}-${upper}-`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatenRemoveStreichungenInRangeOrThrow
        ).toHaveBeenCalledTimes(1);
        const call = mockDefinitions.kandidatenRemoveStreichungenInRangeOrThrow
          .mock.calls[0] as [number, number];
        expect(call[0]).toBe(Math.min(lower, upper));
        expect(call[1]).toBe(Math.max(lower, upper));
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("s10-101-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("sabc-200-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatenRemoveStreichungenInRangeOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatenRemoveStreichungenInRangeOrThrow.mockImplementation(
        () => {
          throw managedStimmzettelError;
        }
      );

      expect(() => handleOrThrow("s101-103-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatenRemoveStreichungenInRangeOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatenRemoveStreichungenInRangeOrThrow.mockImplementation(
        () => {
          throw new Error("unexpected error");
        }
      );

      expect(() => handleOrThrow("s101-103-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
