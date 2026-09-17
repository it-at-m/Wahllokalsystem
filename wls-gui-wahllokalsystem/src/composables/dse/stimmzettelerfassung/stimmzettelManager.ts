import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Stimmzettel as DseStimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef, Ref } from "vue";

import { computed, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { COMMAND_HANDLERS } from "@/composables/dse/stimmzettelerfassung/command/commandHandlers.ts";
import { useBearbeitenDialogStimmzettelUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel.ts";
import { useStimmzettelMapper } from "@/composables/dse/stimmzettelerfassung/stimmzettelMapper.ts";
import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelUtils.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const { logDebug } = useLogging("stimmzettelManager");

export function useStimmzettelManager(
  stimmzettelkennung: ComputedRef<number>,
  wahlvorschlaege: Wahlvorschlag[],
  wahlID: string,
  teamID: string
) {
  const {
    createStimmzettelWithWahlvorschlaege,
    normalizePersistedStimmzettel,
  } = useStimmzettelTools();
  const {
    toPersistedStimmzettel,
    mapPersistedStimmzettelValuesToExistingDseStimmzettel,
  } = useStimmzettelMapper();
  const stimmzettelBeforeEdit: Ref<PersistedStimmzettel | null> = ref(null);

  const managedBearbeitenDialogStimmzettel = ref(
    createStimmzettelWithWahlvorschlaege(wahlvorschlaege)
  );

  const bearbeitenDialogStimmzettelUtils = useBearbeitenDialogStimmzettelUtils(
    managedBearbeitenDialogStimmzettel,
    wahlID
  );

  const hasStimmzettelBeenEdited = computed<boolean>(() => {
    if (stimmzettelBeforeEdit.value) {
      return !_isDeepEqual(
        stimmzettelBeforeEdit.value,
        managedBearbeitenDialogStimmzettel.value
      );
    } else {
      return bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value;
    }
  });

  function setActiveStimmzettelWhenEditing(
    sourceStimmzettel: PersistedStimmzettel
  ) {
    stimmzettelBeforeEdit.value = sourceStimmzettel;
    bearbeitenDialogStimmzettelUtils.resetStimmzettelAndHistory(
      sourceStimmzettel
    );

    mapPersistedStimmzettelValuesToExistingDseStimmzettel(
      managedBearbeitenDialogStimmzettel.value,
      sourceStimmzettel
    );
  }

  function getStimmzettelSnapshot(): PersistedStimmzettel {
    return toPersistedStimmzettel(
      managedBearbeitenDialogStimmzettel.value,
      stimmzettelkennung.value,
      teamID
    );
  }

  function startNewStimmzettel() {
    stimmzettelBeforeEdit.value = null;
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

  function _isDeepEqual(
    persistedStimmzettel: PersistedStimmzettel,
    dseStimmzettel: DseStimmzettel
  ): boolean {
    const mappedFromDse: PersistedStimmzettel = toPersistedStimmzettel(
      dseStimmzettel,
      stimmzettelkennung.value,
      teamID
    );

    const normA = normalizePersistedStimmzettel(persistedStimmzettel);
    const normB = normalizePersistedStimmzettel(mappedFromDse);

    return JSON.stringify(normA) === JSON.stringify(normB);
  }

  return {
    getStimmzettelSnapshot,
    parseCommandOrThrowError,
    startNewStimmzettel,
    bearbeitenDialogStimmzettelUtils,
    setActiveStimmzettelWhenEditing,
    hasStimmzettelBeenEdited,
    stimmzettelBeforeEdit: stimmzettelBeforeEdit,
  };
}

export type StimmzettelManager = ReturnType<typeof useStimmzettelManager>;
