import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef } from "vue";

import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { COMMAND_HANDLERS } from "@/composables/dse/stimmzettelerfassung/command/commandHandlers.ts";
import { useManagedStimmzettel } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import { useStimmzettelMapper } from "@/composables/dse/stimmzettelerfassung/stimmzettelMapper.ts";
import { useStimmzettelUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelUtils.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const { logDebug } = useLogging("stimmzettelManager");

export function useStimmzettelManager(
  stimmzettelkennung: ComputedRef<number>,
  wahlvorschlaege: Wahlvorschlag[],
  wahlID: string,
  teamID: string
) {
  const { createStimmzettelWithWahlvorschlaege } = useStimmzettelUtils();
  const { toPersistedStimmzettel } = useStimmzettelMapper();

  const stimmzettelToManage = ref(
    createStimmzettelWithWahlvorschlaege(wahlvorschlaege)
  );

  const managedStimmzettel = useManagedStimmzettel(stimmzettelToManage, wahlID);

  function getStimmzettelSnapshot(): PersistedStimmzettel {
    return toPersistedStimmzettel(
      stimmzettelToManage.value,
      stimmzettelkennung.value,
      teamID
    );
  }

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

  return {
    getStimmzettelSnapshot,
    parseCommandOrThrowError,
    managedStimmzettel,
  };
}

export type StimmzettelManager = ReturnType<typeof useStimmzettelManager>;
