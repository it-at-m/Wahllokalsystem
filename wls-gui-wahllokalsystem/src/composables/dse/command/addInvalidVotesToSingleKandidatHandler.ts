import type { ManagedStimmzettel } from "@/composables/dse/managedStimmzettel.ts";
import type { CommandHandler } from "@/types/dse/command/CommandHandler.ts";

import { useHandlerTools } from "@/composables/dse/command/handlerTools.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

interface CommandArguments {
  kandidatOrdnungszahl: number;
  countInvalidVotes: number;
}

export function useAddInvalidVotesToSingleKandidatHandler(): CommandHandler {
  const REGEX_ADD_INVALID_VOTES_TO_KANDIDAT = /^[uU]([1-9]\d{2,})(\+(\d*))?$/;
  const {
    isValidCount,
    isValidKandidatOrdnungszahl,
    parseOptionalCountToNumber,
  } = useHandlerTools();

  function canHandle(command: string): boolean {
    try {
      const commandArguments = _parseCommandArguments(command);
      return !!commandArguments;
    } catch {
      return false;
    }
  }

  function handleOrThrow(
    command: string,
    stimmzettel: ManagedStimmzettel
  ): void {
    const commandArguments = _parseCommandArguments(command);
    if (!commandArguments) {
      throw new CommandExecutionError(
        "Kandidat oder Stimmenanzahl konnten nicht eindeutig identifiziert werden."
      );
    }

    try {
      stimmzettel.kandidatAddUngueltigeStimmenOrThrow(
        commandArguments.kandidatOrdnungszahl,
        commandArguments.countInvalidVotes
      );
    } catch (error) {
      if (error instanceof ManagedStimmzettelError) {
        throw new CommandExecutionError(command, error.message);
      } else {
        throw new CommandExecutionError(command);
      }
    }
  }

  function _parseCommandArguments(command: string): CommandArguments | null {
    const match = REGEX_ADD_INVALID_VOTES_TO_KANDIDAT.exec(command);

    if (match?.[1] !== undefined) {
      const votesText = match[3];
      const commandArgs = {
        kandidatOrdnungszahl: Number.parseInt(match[1]),
        countInvalidVotes: parseOptionalCountToNumber(votesText),
      };
      return _isCommandArgumentsValid(commandArgs) ? commandArgs : null;
    } else {
      return null;
    }
  }

  function _isCommandArgumentsValid(
    commandArguments: CommandArguments
  ): boolean {
    return (
      isValidKandidatOrdnungszahl(commandArguments.kandidatOrdnungszahl) &&
      isValidCount(commandArguments.countInvalidVotes)
    );
  }

  return {
    canHandle,
    handleOrThrow,
  };
}
