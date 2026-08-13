import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";

import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useAddVotesToSingleKandidatHandler } from "@/composables/dse/command/addVotesToSingleKandidatHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatAddVotesOrThrow: vi.fn(),
}));

describe("addVotesToSingleKandidatHandler.ts", () => {
  const { canHandle, handleOrThrow } = useAddVotesToSingleKandidatHandler();
  const { prepareManagedStimmzettelStimmzettel } =
    useManagedStimmzettelTestDataFactory();

  const validKandidatOrdnungszahlen = [
    101, 110, 199, 201, 210, 299, 999, 1001, 1010, 1099, 9999,
  ];

  describe("canHandle", () => {
    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'WithoutPlus",
      (validOrdnungszahl) => {
        expect(canHandle(`${validOrdnungszahl}`)).toBe(true);
      }
    );

    it("should_returnTrue_when_commandContainsValidOrdnungszahlWithPlusAndVotes", () => {
      expect(canHandle("101+3")).toBe(true);
    });

    it("should_returnTrue_when_commandContainsValidOrdnungszahlWithPlusWithoutVotes", () => {
      expect(canHandle("101+")).toBe(true);
    });

    it.each(["10", "abc", "0101", "100", "1000", "900", "9900"])(
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
        kandidatAddVotesOrThrow:
          mockDefinitions.kandidatAddVotesOrThrow as unknown as (
            ordnungszahl: number,
            votesToAdd: number
          ) => void,
        stimmzettel: prepareManagedStimmzettelStimmzettel().build(),
      };
    });

    afterEach(() => {
      vi.resetAllMocks();
      vi.clearAllMocks();
    });

    it.each([validKandidatOrdnungszahlen])(
      "should_callKandidatAddVotesOrThrow_once_when_commandIs'%s'ValidWithoutPlus",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`${kandidatOrdnungszahl}`, mockManagedStimmzettel);

        expect(mockDefinitions.kandidatAddVotesOrThrow).toHaveBeenCalledTimes(
          1
        );
        expect(
          mockDefinitions.kandidatAddVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 1]);
      }
    );

    it.each([validKandidatOrdnungszahlen])(
      "should_callKandidatAddVotesOrThrow_withParsedVotes_when_command'%s'ContainsPlusAndVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`${kandidatOrdnungszahl}+3`, mockManagedStimmzettel);

        expect(mockDefinitions.kandidatAddVotesOrThrow).toHaveBeenCalledTimes(
          1
        );
        expect(
          mockDefinitions.kandidatAddVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 3]);
      }
    );

    it.each([validKandidatOrdnungszahlen])(
      "should_defaultCountVotesToOne_when_commandContains'%s'PlusWithoutVotes",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`${kandidatOrdnungszahl}+`, mockManagedStimmzettel);

        expect(mockDefinitions.kandidatAddVotesOrThrow).toHaveBeenCalledTimes(
          1
        );
        expect(
          mockDefinitions.kandidatAddVotesOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl, 1]);
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("10", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("abc", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatAddVotesOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatAddVotesOrThrow.mockImplementation(() => {
        throw managedStimmzettelError;
      });

      expect(() => handleOrThrow("101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatAddVotesOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatAddVotesOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
