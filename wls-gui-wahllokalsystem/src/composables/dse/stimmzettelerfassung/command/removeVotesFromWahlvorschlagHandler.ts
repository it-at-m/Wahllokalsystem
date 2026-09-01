import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import type { CommandHandler } from "@/types/dse/stimmzettelerfassung/command/CommandHandler.ts";

import { useHandlerTools } from "@/composables/dse/stimmzettelerfassung/command/handlerTools.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

export function useRemoveVotesFromWahlvorschlagHandler(): CommandHandler {
  const REGEX_REMOVE_VOTES_TO_WAHLVORSCHLAG = /^([1-9]\d*)(-)$/;
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
      stimmzettel.wahlvorschlagRemoveVotesOrThrow(commandArgument);
    } catch (error) {
      if (error instanceof ManagedStimmzettelError) {
        throw new CommandExecutionError(command, error.message);
      } else {
        throw new CommandExecutionError(command);
      }
    }
  }

  function _parseCommandArguments(command: string): number | null {
    const match = REGEX_REMOVE_VOTES_TO_WAHLVORSCHLAG.exec(command);

    if (match?.[1] !== undefined) {
      const commandArg = Number.parseInt(
        match[1].endsWith("00")
          ? match[1].substring(0, match[1].length - 2)
          : match[1]
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
