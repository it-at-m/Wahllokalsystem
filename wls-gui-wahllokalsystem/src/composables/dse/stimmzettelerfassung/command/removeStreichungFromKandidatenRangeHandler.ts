import type { ManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import type { CommandHandler } from "@/types/dse/stimmzettelerfassung/command/CommandHandler.ts";

import { useHandlerTools } from "@/composables/dse/stimmzettelerfassung/command/handlerTools.ts";
import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

interface CommandArguments {
  kandidatOrdnungszahlLowerBound: number;
  kandidatOrdnungszahlUpperBound: number;
}

export function useRemoveStreichungToKandidatenRangeHandler(): CommandHandler {
  const REXEG_REMOVE_STREICHUNG_TO_KANDIDATEN_RANGE =
    /^[sS]([1-9]\d{2,})-([1-9]\d{2,})-$/;
  const { isValidKandidatOrdnungszahl, isValidRange, normalizeBounds } =
    useHandlerTools();

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
        "Kandidat*in konnte nicht eindeutig identifiziert werden."
      );
    }

    try {
      stimmzettel.kandidatenRemoveStreichungenInRangeOrThrow(
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
    const match = REXEG_REMOVE_STREICHUNG_TO_KANDIDATEN_RANGE.exec(command);

    if (match?.[1] !== undefined) {
      const bound1 = Number.parseInt(match[1]);
      const bound2 = Number.parseInt(match[2]);
      const { lower, upper } = normalizeBounds(bound1, bound2);
      const commandArgs = {
        kandidatOrdnungszahlLowerBound: lower,
        kandidatOrdnungszahlUpperBound: upper,
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
      isValidKandidatOrdnungszahl(
        commandArguments.kandidatOrdnungszahlLowerBound
      ) &&
      isValidKandidatOrdnungszahl(
        commandArguments.kandidatOrdnungszahlUpperBound
      ) &&
      isValidRange(
        commandArguments.kandidatOrdnungszahlLowerBound,
        commandArguments.kandidatOrdnungszahlUpperBound
      )
    );
  }

  return {
    canHandle,
    handleOrThrow,
  };
}
