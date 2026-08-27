import type {CommandHandler} from "@/types/dse/command/CommandHandler.ts";
import type {ManagedStimmzettel} from "@/composables/dse/managedStimmzettel.ts";
import {useHandlerTools} from "@/composables/dse/command/handlerTools.ts";
import {CommandExecutionError} from "@/types/dse/error/CommandExecutionError.ts";
import {ManagedStimmzettelError} from "@/types/dse/error/ManagedStimmzettelError.ts";

interface CommandArguments {
  kandidatOrdnungszahl: number;
  removeVotes: number;
}

export function useRemoveVotesFromSingleKandidatHandler(): CommandHandler {
  const REGEX_REMOVE_VOTE_FROM_KANDIDAT = /^([1-9]\d{2,})(-(\d*))?$/;
  const {
    isValidCount,
    isValidKandidatOrdnungszahl,
    parseOptionalCountToNumber,
  } = useHandlerTools();

  function canHandle(command:string): boolean {
    try {
      const commandArguments = _parseCommandArguments(command);
      return !!commandArguments;
    } catch {
      return false;
    }
  }

  function handleOrThrow(command:string, stimmzettel: ManagedStimmzettel): void {
    const commandArguments = _parseCommandArguments(command);
    if (!commandArguments) {
      throw new CommandExecutionError(
        "Kandidat*in oder Stimmenanzahl konnten nicht eindeutig identifiziert werden."
      );
    }

    try {
      stimmzettel.kandidatRemoveEinzelstimmenOrThrow(
        commandArguments.kandidatOrdnungszahl,
        commandArguments.removeVotes
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
    const match = REGEX_REMOVE_VOTE_FROM_KANDIDAT.exec(command);

    if (match?.[1] !== undefined) {
      const votesText = match[3];
      const commandArgs = {
        kandidatOrdnungszahl: Number.parseInt(match[1]),
        removeVotes: parseOptionalCountToNumber(votesText),
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
      isValidCount(commandArguments.removeVotes)
    );
  }

  return {
    canHandle,
    handleOrThrow,
  };
}