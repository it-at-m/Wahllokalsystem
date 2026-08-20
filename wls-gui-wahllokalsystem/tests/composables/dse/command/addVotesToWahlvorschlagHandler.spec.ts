import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";

import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useAddVotesToWahlvorschlagHandler } from "@/composables/dse/command/addVotesToWahlvorschlagHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  wahlvorschlagAddVotesOrThrow: vi.fn(),
}));

describe("addVotesToWahlvorschlagHandler.ts", () => {
  const { canHandle, handleOrThrow } = useAddVotesToWahlvorschlagHandler();
  const { prepareManagedStimmzettelStimmzettel } =
    useManagedStimmzettelTestDataFactory();

  const validWahlvorschlagOrdnungszahlen = [
    1, 2, 9, 10, 11, 99, 100, 101, 999, 1000, 1001, 9999,
  ];

  describe("canHandle", () => {
    it.each(validWahlvorschlagOrdnungszahlen)(
      "should_returnTrue_when_commandContainsValidOrdnungszahl'%s'",
      (validOrdnungszahl) => {
        expect(canHandle(`${validOrdnungszahl}`)).toBe(true);
      }
    );

    it.each(["0", "abc", "010", "10000"])(
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
          mockDefinitions.wahlvorschlagAddVotesOrThrow as unknown as (
            wahlvorschlagOrdnungszahl: number
          ) => void,
        // weitere Methoden als Mocks
        kandidatAddEinzelstimmenOrThrow: vi.fn(),
        kandidatAddUngueltigeStimmenOrThrow: vi.fn(),
        kandidatenAddStimmenInRangeOrThrow: vi.fn(),
        kandidatAddStreichungOrThrow: vi.fn(),
        kandidatenStreichungenInRangeOrThrow: vi.fn(),
        stimmzettel: computed(() =>
          prepareManagedStimmzettelStimmzettel().build()
        ),
        changeHistoryInReverOrder: computed(() => []),
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

    it.each(validWahlvorschlagOrdnungszahlen)(
      "should_callWahlvorschlagAddVotesOrThrow_once_when_commandIs'%s'",
      (wahlvorschlagOrdnungszahl) => {
        handleOrThrow(`${wahlvorschlagOrdnungszahl}`, mockManagedStimmzettel);

        expect(
          mockDefinitions.wahlvorschlagAddVotesOrThrow
        ).toHaveBeenCalledTimes(1);
        const expectedArg =
          wahlvorschlagOrdnungszahl % 100 === 0
            ? wahlvorschlagOrdnungszahl / 100
            : wahlvorschlagOrdnungszahl;
        expect(
          mockDefinitions.wahlvorschlagAddVotesOrThrow.mock.calls[0]
        ).toStrictEqual([expectedArg]);
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

      expect(() => handleOrThrow("101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_wahlvorschlagAddVotesOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.wahlvorschlagAddVotesOrThrow.mockImplementation(() => {
        throw new Error("unexpected error");
      });

      expect(() => handleOrThrow("101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
