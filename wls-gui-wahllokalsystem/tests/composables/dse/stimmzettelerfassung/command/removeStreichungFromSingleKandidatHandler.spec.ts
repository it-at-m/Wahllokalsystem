import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";

import {
  invalidKandidatOrdnungszahlenCommand,
  validKandidatOrdnungszahlen,
} from "@tests/utils/dse/commandTestTools.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useRemoveStreichungFromSingleKandidatHandler } from "@/composables/dse/stimmzettelerfassung/command/removeStreichungFromSingleKandidatHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatRemoveStreichungOrThrow: vi.fn(),
}));

describe("removeStreichungFromSingleKandidatHandler.ts", () => {
  const { canHandle, handleOrThrow } =
    useRemoveStreichungFromSingleKandidatHandler();

  describe("canHandle", () => {
    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithMinus",
      (validOrdnungszahl) => {
        expect(canHandle(`s${validOrdnungszahl}-`)).toBe(true);
        expect(canHandle(`S${validOrdnungszahl}-`)).toBe(true);
      }
    );

    it.each(invalidKandidatOrdnungszahlenCommand)(
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
        kandidatRemoveStreichungOrThrow:
          mockDefinitions.kandidatRemoveStreichungOrThrow,
      } as unknown as ManagedStimmzettel;
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatRemoveStreichungOrThrow_when_commandIs'%s-'",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`s${kandidatOrdnungszahl}-`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatRemoveStreichungOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatRemoveStreichungOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl]);
      }
    );

    it("should_throwCommandExecutionError_when_ordnungszahlIsInvalid", () => {
      expect(() => handleOrThrow("s10", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_throwCommandExecutionError_when_ordnungszahlHasWrongFormat", () => {
      expect(() => handleOrThrow("sabc", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_throwCommandExecutionError_when_minusIsMissing", () => {
      expect(() => handleOrThrow("s101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatRemoveStreichungOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatRemoveStreichungOrThrow.mockImplementation(() => {
        throw managedStimmzettelError;
      });

      expect(() => handleOrThrow("s101-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatRemoveStreichungOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatRemoveStreichungOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("s101-", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
