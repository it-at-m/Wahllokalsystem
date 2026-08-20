import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";

import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useAddStreichungToSingleKandidatHandler } from "@/composables/dse/command/addStreichungToSingleKandidatHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatAddStreichungOrThrow: vi.fn(),
}));

describe("addStreichungToSingleKandidatHandler.ts", () => {
  const { canHandle, handleOrThrow } =
    useAddStreichungToSingleKandidatHandler();
  const { prepareManagedStimmzettelStimmzettel } =
    useManagedStimmzettelTestDataFactory();

  const validKandidatOrdnungszahlen = [
    101, 110, 199, 201, 210, 299, 999, 1001, 1010, 1099, 9999,
  ];

  describe("canHandle", () => {
    it.each(validKandidatOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'",
      (validOrdnungszahl) => {
        expect(canHandle(`s${validOrdnungszahl}`)).toBe(true);
        expect(canHandle(`S${validOrdnungszahl}`)).toBe(true);
      }
    );

    it.each(["10", "abc", "s0101", "S0101", "s100", "s1000", "s900", "s9900"])(
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
        kandidatAddStreichungOrThrow:
          mockDefinitions.kandidatAddStreichungOrThrow as unknown as (
            ordnungszahl: number
          ) => void,
        kandidatAddEinzelstimmenOrThrow: vi.fn(),
        kandidatAddUngueltigeStimmenOrThrow: vi.fn(),
        kandidatenAddStimmenInRangeOrThrow: vi.fn(),
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

    it.each(validKandidatOrdnungszahlen)(
      "should_callKandidatAddStreichungOrThrow_when_commandIs'%s'",
      (kandidatOrdnungszahl) => {
        handleOrThrow(`s${kandidatOrdnungszahl}`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatAddStreichungOrThrow
        ).toHaveBeenCalledTimes(1);
        expect(
          mockDefinitions.kandidatAddStreichungOrThrow.mock.calls[0]
        ).toStrictEqual([kandidatOrdnungszahl]);
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("s10", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("sabc", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatAddStreichungOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatAddStreichungOrThrow.mockImplementation(() => {
        throw managedStimmzettelError;
      });

      expect(() => handleOrThrow("s101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatAddStreichungOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatAddStreichungOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("s101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
