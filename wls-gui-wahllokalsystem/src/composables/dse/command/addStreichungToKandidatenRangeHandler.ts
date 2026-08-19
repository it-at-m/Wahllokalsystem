import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";
import type { CommandHandler } from "@/types/dse/command/CommandHandler.ts";

import { WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/composables/dse/ManagedStimmzettel.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

interface CommandArguments {
  kandidatOrdnungszahlLowerBound: number;
  kandidatOrdnungszahlUpperBound: number;
}

export function useAddStreichungToKandidatenRangeHandler(): CommandHandler {
  const REXEG_ADD_STREICHUNG_TO_KANDIDATEN_RANGE =
    /^[sS]([1-9]\d{2,})(?:-([1-9]\d{2,}))?$/;

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
        "Kandidat konnte nicht eindeutig identifiziert werden."
      );
    }

    try {
      stimmzettel.kandidatenStreichungenInRangeOrThrow(
        commandArguments.kandidatOrdnungszahlLowerBound,
        commandArguments.kandidatOrdnungszahlUpperBound
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
    const match = REXEG_ADD_STREICHUNG_TO_KANDIDATEN_RANGE.exec(command);

    if (match?.[1] !== undefined) {
      const bound1 = Number.parseInt(match[1]);
      const bound2 = Number.parseInt(match[2]);
      const commandArgs = {
        kandidatOrdnungszahlLowerBound: Math.min(bound1, bound2),
        kandidatOrdnungszahlUpperBound: Math.max(bound1, bound2),
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
        0
    );
  }

  return {
    canHandle,
    handleOrThrow,
  };
}
