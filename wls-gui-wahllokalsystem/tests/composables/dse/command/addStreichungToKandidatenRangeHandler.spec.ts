import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";

import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useAddStreichungToKandidatenRangeHandler } from "@/composables/dse/command/addStreichungToKandidatenRangeHandler.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

const mockDefinitions = vi.hoisted(() => ({
  kandidatenStreichungenInRangeOrThrow: vi.fn(),
}));

describe("addStreichungToKandidatenRangeHandler.ts", () => {
  const { canHandle, handleOrThrow } =
    useAddStreichungToKandidatenRangeHandler();
  const { prepareManagedStimmzettelStimmzettel } =
    useManagedStimmzettelTestDataFactory();

  const validRanges: [number, number][] = [
    [101, 103],
    [201, 299],
    [999, 1001],
    [1099, 1010], // unsortiert, wird im Handler sortiert
  ];

  describe("canHandle", () => {
    it.each(validRanges)(
      "should_returnTrue_when_commandContainsValidRange'%s-%s'",
      (lower, upper) => {
        expect(canHandle(`s${lower}-${upper}`)).toBe(true);
        expect(canHandle(`S${lower}-${upper}`)).toBe(true);
      }
    );

    it.each([
      "s10-101",
      "sabc-200",
      "s100-1000",
      "s101-100",
      "s101-1000",
      "s101-",
    ])("should_returnFalse_when_command'%s'DoesNotMatchPattern", (command) => {
      expect(canHandle(command)).toBe(false);
    });
  });

  describe("handleOrThrow", () => {
    let mockManagedStimmzettel: ManagedStimmzettel;

    beforeEach(() => {
      mockManagedStimmzettel = {
        kandidatenStreichungenInRangeOrThrow:
          mockDefinitions.kandidatenStreichungenInRangeOrThrow as unknown as (
            lower: number,
            upper: number
          ) => void,
        // weitere Methoden als Mocks
        kandidatAddEinzelstimmenOrThrow: vi.fn(),
        kandidatAddUngueltigeStimmenOrThrow: vi.fn(),
        kandidatenAddStimmenInRangeOrThrow: vi.fn(),
        kandidatAddStreichungOrThrow: vi.fn(),
        wahlvorschlagAddVotesOrThrow: vi.fn(),
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

    it.each(validRanges)(
      "should_callKandidatenStreichungenInRangeOrThrow_once_when_commandIs'%s-%s'",
      (lower, upper) => {
        handleOrThrow(`s${lower}-${upper}`, mockManagedStimmzettel);

        expect(
          mockDefinitions.kandidatenStreichungenInRangeOrThrow
        ).toHaveBeenCalledTimes(1);
        const call = mockDefinitions.kandidatenStreichungenInRangeOrThrow.mock
          .calls[0] as [number, number];
        expect(call[0]).toBe(Math.min(lower, upper));
        expect(call[1]).toBe(Math.max(lower, upper));
      }
    );

    it("should_throwCommandExecutionError_when_commandArgumentsAreInvalid", () => {
      expect(() => handleOrThrow("s10-101", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
      expect(() => handleOrThrow("sabc-200", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapManagedStimmzettelErrorInCommandExecutionError_when_kandidatenStreichungenInRangeOrThrowThrowsManagedStimmzettelError", () => {
      const managedStimmzettelError = new ManagedStimmzettelError(
        "Kandidat existiert nicht."
      );
      mockDefinitions.kandidatenStreichungenInRangeOrThrow.mockImplementation(
        () => {
          throw managedStimmzettelError;
        }
      );

      expect(() => handleOrThrow("s101-103", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });

    it("should_wrapGenericErrorInCommandExecutionError_when_kandidatenStreichungenInRangeOrThrowThrowsUnexpectedError", () => {
      mockDefinitions.kandidatenStreichungenInRangeOrThrow.mockImplementation(
        () => {
          throw new Error("unexpected error");
        }
      );

      expect(() => handleOrThrow("s101-103", mockManagedStimmzettel)).toThrow(
        CommandExecutionError
      );
    });
  });
});
