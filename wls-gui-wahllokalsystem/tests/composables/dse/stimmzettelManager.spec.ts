import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { afterEach, describe, expect, it, vi } from "vitest";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelManager.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const mockDefinitions = vi.hoisted(() => ({
  handlerOneCanHandle: vi.fn(),
  handlerTwoCanHandle: vi.fn(),
  handlerOneHandleOrThrow: vi.fn(),
  handlerTwoHandleOrThrow: vi.fn(),
}));

vi.mock("@/composables/dse/command/commandHandlers.ts", () => {
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
});

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
        [dummyWahlvorschlag],
        "wahl-1"
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
        [dummyWahlvorschlag],
        "wahl-1"
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
        [dummyWahlvorschlag],
        "wahl-1"
      );

      expect(() => parseCommandOrThrowError(command)).toThrow(
        CommandExecutionError
      );

      expect(mockDefinitions.handlerOneCanHandle).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerOneHandleOrThrow).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.handlerTwoHandleOrThrow).not.toHaveBeenCalled();
    });
  });
});
