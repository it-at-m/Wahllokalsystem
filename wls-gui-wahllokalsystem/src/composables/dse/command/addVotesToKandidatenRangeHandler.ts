import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";
import type { CommandHandler } from "@/types/dse/command/CommandHandler.ts";

import { WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/composables/dse/ManagedStimmzettel.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

interface CommandArguments {
  kandidatOrdnungszahlLowerBound: number;
  kandidatOrdnungszahlUpperBound: number;
  countVotes: number;
}

export function useAddVotesToKandidatenRangeHandler(): CommandHandler {
  const REGEX_ADD_VOTES_TO_KANDIDATEN_RANGE =
    /^([1-9]\d{2,})-([1-9]\d{2,})(\+(\d+))?$/;

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
      stimmzettel.kandidatenAddStimmenInRangeOrThrow(
        commandArguments.kandidatOrdnungszahlLowerBound,
        commandArguments.kandidatOrdnungszahlUpperBound,
        commandArguments.countVotes
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
    const match = REGEX_ADD_VOTES_TO_KANDIDATEN_RANGE.exec(command);

    if (match?.[1] !== undefined) {
      const votesText = match[4];
      const bound1 = Number.parseInt(match[1]);
      const bound2 = Number.parseInt(match[2]);
      const commandArgs = {
        kandidatOrdnungszahlLowerBound: Math.min(bound1, bound2),
        kandidatOrdnungszahlUpperBound: Math.max(bound1, bound2),
        countVotes:
          votesText && votesText.length > 0 ? Number.parseInt(votesText) : 1,
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
      Number.isSafeInteger(commandArguments.kandidatOrdnungszahlLowerBound) &&
      commandArguments.kandidatOrdnungszahlLowerBound %
        WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL !=
        0 &&
      Number.isSafeInteger(commandArguments.kandidatOrdnungszahlUpperBound) &&
      commandArguments.kandidatOrdnungszahlUpperBound %
        WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL !=
        0 &&
      Number.isSafeInteger(commandArguments.countVotes)
    );
  }

  return {
    canHandle,
    handleOrThrow,
  };
}
