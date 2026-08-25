import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { COMMAND_HANDLERS } from "@/composables/dse/stimmzettelerfassung/command/commandHandlers.ts";
import { useManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/ManagedStimmzettel.ts";
import { useStimmzettelUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelUtils.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const { logDebug } = useLogging("stimmzettelManager");

export function useStimmzettelManager(wahlvorschlaege: Wahlvorschlag[]) {
  const { createStimmzettelWithWahlvorschlaege } = useStimmzettelUtils();

  const managedStimmzettel = useManagedStimmzettel(
    ref(createStimmzettelWithWahlvorschlaege(wahlvorschlaege))
  );

  /**
   *
   * @param commandString
   * @throws UnsupportedCommandError when no handler was found for command string
   * @throws CommandExecutionError see CommandHandler#handleOrThrow
   */
  function parseCommandOrThrowError(commandString: string) {
    logDebug(`parsing command ${commandString}`);

    const handlerForCommand = COMMAND_HANDLERS.find((handler) =>
      handler.canHandle(commandString)
    );
    if (!handlerForCommand) {
      throw new UnsupportedCommandError(commandString);
    }

    handlerForCommand.handleOrThrow(commandString, managedStimmzettel);
  }

  return { parseCommandOrThrowError, managedStimmzettel };
}

export type StimmzettelManager = ReturnType<typeof useStimmzettelManager>;
