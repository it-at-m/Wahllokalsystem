import type { DseStimmzettel } from "@/types/dse/stimmzettelerfassung/DseStimmzettel.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef, Ref } from "vue";

import { computed, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useBearbeitenDialogStimmzettelUtils } from "@/composables/dse/stimmzettelerfassung/bearbeitenDialogStimmzettelUtils.ts";
import { COMMAND_HANDLERS } from "@/composables/dse/stimmzettelerfassung/command/commandHandlers.ts";
import { useStimmzettelMapper } from "@/composables/dse/stimmzettelerfassung/stimmzettelMapper.ts";
import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const { logDebug } = useLogging("stimmzettelManager");

export function useStimmzettelManager(
  stimmzettelkennung: ComputedRef<number>,
  wahlvorschlaege: Wahlvorschlag[],
  wahlID: string
) {
  const { createStimmzettelWithWahlvorschlaege, isDeepEqualIgnoreGueltigkeit } =
    useStimmzettelTools();
  const {
    toPersistedStimmzettel,
    mapPersistedStimmzettelValuesToExistingDseStimmzettel,
  } = useStimmzettelMapper();
  const stimmzettelBeforeEdit: Ref<PersistedStimmzettel | null> = ref(null);
  const activeTeamID = ref<string | null>(null);

  const managedBearbeitenDialogStimmzettel = ref(
    createStimmzettelWithWahlvorschlaege(wahlvorschlaege)
  );

  const bearbeitenDialogStimmzettelUtils = useBearbeitenDialogStimmzettelUtils(
    managedBearbeitenDialogStimmzettel,
    wahlID
  );

  const hasStimmzettelBeenEdited = computed<boolean>(() => {
    if (stimmzettelBeforeEdit.value) {
      const mappedFromDse: PersistedStimmzettel = toPersistedStimmzettel(
        managedBearbeitenDialogStimmzettel.value,
        stimmzettelkennung.value,
        activeTeamID.value
      );
      return !isDeepEqualIgnoreGueltigkeit(
        mappedFromDse,
        stimmzettelBeforeEdit.value
      );
    } else {
      return bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value;
    }
  });

  function getStimmzettelSnapshot(): PersistedStimmzettel {
    return toPersistedStimmzettel(
      managedBearbeitenDialogStimmzettel.value,
      stimmzettelkennung.value,
      activeTeamID.value
    );
  }

  function startEditStimmzettel(stimmzettelToSet: PersistedStimmzettel) {
    stimmzettelBeforeEdit.value = stimmzettelToSet;
    bearbeitenDialogStimmzettelUtils.resetStimmzettelAndHistory(
      stimmzettelToSet
    );
    activeTeamID.value = stimmzettelToSet.teamID;

    mapPersistedStimmzettelValuesToExistingDseStimmzettel(
      managedBearbeitenDialogStimmzettel.value,
      stimmzettelToSet
    );
  }

  function startNewStimmzettel(teamID: string) {
    activeTeamID.value = teamID;
    stimmzettelBeforeEdit.value = null;
    startWithClearedStimmzettel();
  }

  function startWithClearedStimmzettel() {
    managedBearbeitenDialogStimmzettel.value =
      createStimmzettelWithWahlvorschlaege(wahlvorschlaege);
    bearbeitenDialogStimmzettelUtils.resetStimmzettelAndHistory();
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

    handlerForCommand.handleOrThrow(
      commandString,
      bearbeitenDialogStimmzettelUtils
    );
  }

  return {
    getStimmzettelSnapshot,
    parseCommandOrThrowError,
    startNewStimmzettel,
    startWithClearedStimmzettel,
    bearbeitenDialogStimmzettelUtils,
    startEditStimmzettel,
    hasStimmzettelBeenEdited,
    stimmzettelBeforeEdit: stimmzettelBeforeEdit,
  };
}

export type StimmzettelManager = ReturnType<typeof useStimmzettelManager>;
