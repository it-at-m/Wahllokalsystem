import type { ManagedStimmzettel } from "@/composables/dse/managedStimmzettel.ts";
import type { CommandHandler } from "@/types/dse/command/CommandHandler.ts";

import { useHandlerTools } from "@/composables/dse/command/handlerTools.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

export function useAddStreichungToSingleKandidatHandler(): CommandHandler {
  const REGEX_ADD_STREICHUNG_TO_KANDIDAT = /^[sS]([1-9]\d{2,})$/;
  const { isValidKandidatOrdnungszahl } = useHandlerTools();

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
        "Kandidat konnte nicht eindeutig identifiziert werden."
      );
    }

    try {
      stimmzettel.kandidatAddStreichungOrThrow(commandArgument);
    } catch (error) {
      if (error instanceof ManagedStimmzettelError) {
        throw new CommandExecutionError(command, error.message);
      } else {
        throw new CommandExecutionError(command);
      }
    }
  }

  function _parseCommandArguments(command: string): number | null {
    const match = REGEX_ADD_STREICHUNG_TO_KANDIDAT.exec(command);

    if (match?.[1] !== undefined) {
      const commandArg = Number.parseInt(match[1]);
      return _isCommandArgumentsValid(commandArg) ? commandArg : null;
    } else {
      return null;
    }
  }

  function _isCommandArgumentsValid(commandArgument: number): boolean {
    return isValidKandidatOrdnungszahl(commandArgument);
  }

  return {
    canHandle,
    handleOrThrow,
  };
}
