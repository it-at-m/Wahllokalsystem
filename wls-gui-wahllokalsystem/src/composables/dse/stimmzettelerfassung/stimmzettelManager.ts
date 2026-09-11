import type { Kandidat as PersistedKandidat } from "@/types/dse/persistedStimmzettel/Kandidat.ts";
import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag as PersistedWahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";
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
  const { createStimmzettelWithWahlvorschlaege } = useStimmzettelTools();
  const { toPersistedStimmzettel } = useStimmzettelMapper();
  const stimmzettelBeforeEdit: Ref<PersistedStimmzettel | null> = ref(null);

  const managedBearbeitenDialogStimmzettel = ref(
    createStimmzettelWithWahlvorschlaege(wahlvorschlaege)
  );

  const bearbeitenDialogStimmzettelUtils = useBearbeitenDialogStimmzettelUtils(
    managedBearbeitenDialogStimmzettel,
    wahlID
  );

  const hasStimmzettelBeenEdited = computed<boolean>(() => {
    if (!stimmzettelBeforeEdit.value) return false;
    return !_isDeepEqual(
      stimmzettelBeforeEdit.value,
      managedBearbeitenDialogStimmzettel.value
    );
  });

  function setActiveStimmzettelWhenEditing(
    stimmzettelToSet: PersistedStimmzettel
  ) {
    stimmzettelBeforeEdit.value = stimmzettelToSet;

    managedBearbeitenDialogStimmzettel.value.gueltigkeit =
      stimmzettelToSet.gueltigkeit;
    managedBearbeitenDialogStimmzettel.value.invalideVotes =
      stimmzettelToSet.invalideVotes ?? 0;
    managedBearbeitenDialogStimmzettel.value.beschlussfassung =
      stimmzettelToSet.beschlussfassung;
    managedBearbeitenDialogStimmzettel.value.wahlvorstandBeschlussvorschlag =
      stimmzettelToSet.wahlvorstandBeschlussvorschlag ?? [];
    managedBearbeitenDialogStimmzettel.value.systemBeschlussvorschlag =
      stimmzettelToSet.systemBeschlussvorschlag ?? [];

    stimmzettelToSet.wahlvorschlaege.forEach(
      (wahlvorschlagOfStimmzettelToSet: PersistedWahlvorschlag) => {
        const managedWahlvorschlag =
          managedBearbeitenDialogStimmzettel.value.wahlvorschlaege.find(
            (wahlvorschlag) =>
              wahlvorschlag.wahlvorschlagID ===
              wahlvorschlagOfStimmzettelToSet.wahlvorschlagID
          );

        if (!managedWahlvorschlag) return;

        managedWahlvorschlag.selected =
          wahlvorschlagOfStimmzettelToSet.selected;

        (wahlvorschlagOfStimmzettelToSet.kandidaten || []).forEach(
          (kandidatOfStimmzettelToSet: PersistedKandidat) => {
            const managedKandidat = managedWahlvorschlag.kandidaten.find(
              (kandidat) =>
                kandidat.kandidatId === kandidatOfStimmzettelToSet.kandidatId &&
                kandidat.nennung === kandidatOfStimmzettelToSet.nennung
            );

            if (!managedKandidat) return;

            managedKandidat.einzelstimmen =
              kandidatOfStimmzettelToSet.votesByVoter;
            managedKandidat.ungueltigeStimmen =
              kandidatOfStimmzettelToSet.invalidVotes;
            managedKandidat.reststimmen =
              kandidatOfStimmzettelToSet.votesByWahlvorschlag;
            managedKandidat.durchgestrichen =
              kandidatOfStimmzettelToSet.isDiscarded;
          }
        );
      }
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
    managedBearbeitenDialogStimmzettel.value =
      createStimmzettelWithWahlvorschlaege(wahlvorschlaege);
    bearbeitenDialogStimmzettelUtils.changeHistory.reset();
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

    const normA = _normalizePersistedStimmzettel(persistedStimmzettel);
    const normB = _normalizePersistedStimmzettel(mappedFromDse);

    return JSON.stringify(normA) === JSON.stringify(normB);
  }

  function _normalizePersistedStimmzettel(
    stimmzettel: PersistedStimmzettel
  ): PersistedStimmzettel {
    const systemBeschluss = (stimmzettel.systemBeschlussvorschlag ?? [])
      .slice()
      .sort((x, y) => String(x.reason).localeCompare(String(y.reason)));
    const wvBeschluss = (stimmzettel.wahlvorstandBeschlussvorschlag ?? [])
      .slice()
      .sort((x, y) => x.text.localeCompare(y.text));

    const wvSorted = (stimmzettel.wahlvorschlaege ?? [])
      .slice()
      .sort((x, y) => x.wahlvorschlagID.localeCompare(y.wahlvorschlagID))
      .map(
        (wv) =>
          ({
            wahlvorschlagID: wv.wahlvorschlagID,
            selected: wv.selected,
            kandidaten: (wv.kandidaten ?? [])
              .slice()
              .sort((a, b) => {
                const idCmp = a.kandidatId.localeCompare(b.kandidatId);
                return idCmp !== 0 ? idCmp : a.nennung - b.nennung;
              })
              .map(
                (k) =>
                  ({
                    kandidatId: k.kandidatId,
                    nennung: k.nennung,
                    isDiscarded: k.isDiscarded,
                    votesByVoter: k.votesByVoter ?? null,
                    invalidVotes: k.invalidVotes ?? null,
                    votesByWahlvorschlag: k.votesByWahlvorschlag ?? null,
                  }) as PersistedKandidat
              ),
          }) as PersistedWahlvorschlag
      );

    return {
      stimmzettelkennung: stimmzettel.stimmzettelkennung,
      teamID: stimmzettel.teamID,
      wahlvorschlaege: wvSorted,
      invalideVotes: stimmzettel.invalideVotes ?? 0,
      gueltigkeit: stimmzettel.gueltigkeit,
      wahlvorstandBeschlussvorschlag: wvBeschluss,
      systemBeschlussvorschlag: systemBeschluss,
      beschlussfassung: stimmzettel.beschlussfassung
        ? { ...stimmzettel.beschlussfassung }
        : null,
    };
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
