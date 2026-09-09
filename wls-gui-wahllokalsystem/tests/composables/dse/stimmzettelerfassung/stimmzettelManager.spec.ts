import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { afterEach, describe, expect, it, vi } from "vitest";
import { computed } from "vue";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const mockDefinitions = vi.hoisted(() => ({
  handlerOneCanHandle: vi.fn(),
  handlerTwoCanHandle: vi.fn(),
  handlerOneHandleOrThrow: vi.fn(),
  handlerTwoHandleOrThrow: vi.fn(),
  mangedStimmzettel: {
    kandidatAddEinzelstimmenOrThrow: vi.fn(),
    resetChangeHistory: vi.fn(),
  },
}));

vi.mock(
  "@/composables/dse/stimmzettelerfassung/command/commandHandlers.ts",
  () => {
    const handlers = [
      {
        canHandle: mockDefinitions.handlerOneCanHandle,
        handleOrThrow: mockDefinitions.handlerOneHandleOrThrow,
      },
      {
        canHandle: mockDefinitions.handlerTwoCanHandle,
        handleOrThrow: mockDefinitions.handlerTwoHandleOrThrow,
      },
    ];
    return { COMMAND_HANDLERS: handlers };
  }
);
vi.mock("@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts", () => ({
  useBearbeitenDialogStimmzettelUtils: (
    stimmzettel: Ref<Stimmzettel>,
    wahlID: string
  ) => {
    return {
      kandidatAddEinzelstimmenOrThrow:
        mockDefinitions.mangedStimmzettel.kandidatAddEinzelstimmenOrThrow,
      changeHistory: {
        reset: mockDefinitions.mangedStimmzettel.resetChangeHistory,
      },
      stimmzettel,
      wahlID,
    };
  },
}));

describe("stimmzettelManager.ts", () => {
  const dummyWahlvorschlag: Wahlvorschlag = {
    identifikator: "id",
    ordnungszahl: 1,
    kurzname: "KV",
    erhaeltStimmen: true,
    kandidaten: [],
  };

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("parseCommandOrThrowError", () => {
    it("should_callFirstHandlerThatCanHandleCommand_when_commandIsSupported", () => {
      const command = "101+1";

      mockDefinitions.handlerOneCanHandle.mockReturnValue(true);
      mockDefinitions.handlerTwoCanHandle.mockReturnValue(false);

      const { parseCommandOrThrowError } = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      parseCommandOrThrowError(command);

      expect(mockDefinitions.handlerOneCanHandle).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerOneCanHandle.mock.calls[0]).toStrictEqual([
        command,
      ]);

      expect(mockDefinitions.handlerOneHandleOrThrow).toHaveBeenCalledTimes(1);
      const callArgs = mockDefinitions.handlerOneHandleOrThrow.mock.calls[0];
      expect(callArgs[0]).toBe(command);
      expect(
        (callArgs[1] as ManagedStimmzettel).kandidatAddEinzelstimmenOrThrow
      ).toBeDefined();

      expect(mockDefinitions.handlerTwoCanHandle).not.toHaveBeenCalled();
      expect(mockDefinitions.handlerTwoHandleOrThrow).not.toHaveBeenCalled();
    });

    it("should_throwUnsupportedCommandError_when_noHandlerCanHandleCommand", () => {
      const command = "UNKNOWN";

      mockDefinitions.handlerOneCanHandle.mockReturnValue(false);
      mockDefinitions.handlerTwoCanHandle.mockReturnValue(false);

      const { parseCommandOrThrowError } = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      expect(() => parseCommandOrThrowError(command)).toThrow(
        UnsupportedCommandError
      );

      expect(mockDefinitions.handlerOneHandleOrThrow).not.toHaveBeenCalled();
      expect(mockDefinitions.handlerTwoHandleOrThrow).not.toHaveBeenCalled();
    });

    it("should_rethrowCommandExecutionError_when_handlerThrowsCommandExecutionError", () => {
      const command = "101+1";
      const error = new CommandExecutionError(command);

      mockDefinitions.handlerOneCanHandle.mockReturnValue(true);
      mockDefinitions.handlerOneHandleOrThrow.mockImplementation(() => {
        throw error;
      });

      const { parseCommandOrThrowError } = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      expect(() => parseCommandOrThrowError(command)).toThrow(
        CommandExecutionError
      );

      expect(mockDefinitions.handlerOneCanHandle).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerOneHandleOrThrow).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerTwoHandleOrThrow).not.toHaveBeenCalled();
    });
  });

  describe("startNewStimmzettel", () => {
    it("should_setNewStimmzettelAndResetHistory_when_called", () => {
      const unitUnderTest = useStimmzettelManager(
        computed(() => 1),
        [dummyWahlvorschlag],
        "wahl-1",
        "team A"
      );

      const stimmzettelBeforeStartNewOne =
        unitUnderTest.bearbeitenDialogStimmzettelUtils.stimmzettel.value;
      stimmzettelBeforeStartNewOne.invalideVotes = 20;

      unitUnderTest.startNewStimmzettel();

      expect(
        unitUnderTest.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      ).not.toStrictEqual(stimmzettelBeforeStartNewOne);
      expect(
        unitUnderTest.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      ).not.toBe(stimmzettelBeforeStartNewOne);
      expect(
        mockDefinitions.mangedStimmzettel.resetChangeHistory
      ).toHaveBeenCalledTimes(1);
    });
  });
});
