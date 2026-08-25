import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";
import type { CommandHandler } from "@/types/dse/command/CommandHandler.ts";

import { useHandlerTools } from "@/composables/dse/command/handlerTools.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

export function useAddVotesToWahlvorschlagHandler(): CommandHandler {
  const REGEX_ADD_VOTES_TO_WAHLVORSCHLAG = /^[1-9]\d{0,}$/;
  const { isValidWahlvorschlagOrdnungszahl } = useHandlerTools();

  function canHandle(command: string): boolean {
    try {
      const commandArgument = _parseCommandArguments(command);
      return !!commandArgument;
    } catch {
      return false;
    }
  }

  function handleOrThrow(
    command: string,
    stimmzettel: ManagedStimmzettel
  ): void {
    const commandArgument = _parseCommandArguments(command);
    if (!commandArgument) {
      throw new CommandExecutionError(
        "Wahlvorschlag konnten nicht eindeutig identifiziert werden."
      );
    }

    try {
      stimmzettel.wahlvorschlagAddVotesOrThrow(commandArgument);
    } catch (error) {
      if (error instanceof ManagedStimmzettelError) {
        throw new CommandExecutionError(command, error.message);
      } else {
        throw new CommandExecutionError(command);
      }
    }
  }

  function _parseCommandArguments(command: string): number | null {
    const match = REGEX_ADD_VOTES_TO_WAHLVORSCHLAG.exec(command);

    if (match?.[0] !== undefined) {
      const commandArg = Number.parseInt(
        match[0].endsWith("00")
          ? match[0].substring(0, match[0].length - 2)
          : match[0]
      );
      return isValidWahlvorschlagOrdnungszahl(commandArg) ? commandArg : null;
    } else {
      return null;
    }
  }

  return {
    canHandle,
    handleOrThrow,
  };
}
