import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { StimmenSummary } from "@/types/dse/stimmzettelerfassung/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useManagedStimmzettelEinzelstimmeUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelEinzelstimmeUtils.ts";
import { useManagedStimmzettelKandidatUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelKandidatUtils.ts";
import { useManagedStimmzettelReststimmeUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelReststimmeUtils.ts";
import { useManagedStimmzettelUngueltigeStimmeUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelUngueltigeStimmeUtils.ts";
import { useManagedStimmzettelWahlvorschlagUtils } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/managedStimmzettelWahlvorschlagUtils.ts";
import { useStimmzettelChangeHistory } from "@/composables/dse/stimmzettelerfassung/stimmzettelChangeHistory.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

/**
 * Check UI/UX Adr to see the rules:
 * https://it-at-m.github.io/Wahllokalsystem/technik/adr/ui/adr010-dse-stimmvergabe-stimmen-ergaenzen.html
 *
 * @param stimmzettel
 * @param wahlID
 * @param maxEinzelstimmen
 * @param COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG
 */
export function useManagedStimmzettel(
  stimmzettel: Ref<Stimmzettel>,
  wahlID: string,
  maxEinzelstimmen = 3,
  COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG = 1
) {
  const changeHistory = useStimmzettelChangeHistory();
  const {
    kandidatenOfStimmzettel,
    getKandidatToAddVotesByOrdnungszahl,
    getKandidatToAddVotesForRangeByOrdnungszahl,
    getKandidatForStreichungByOrdnungszahl,
    getKandidatToRemoveStreichungByOrdnungszahl,
  } = useManagedStimmzettelKandidatUtils(stimmzettel);
  const { getWahlvorschlagByOrdnungszahl } =
    useManagedStimmzettelWahlvorschlagUtils(stimmzettel);
  const { addVotesToKandidat, removeVotesFromKandidat } =
    useManagedStimmzettelEinzelstimmeUtils();
  const { addInvalidVotesToKandidat, removeInvalidVotesFromKandidat } =
    useManagedStimmzettelUngueltigeStimmeUtils();

  const { kopfdaten } = storeToRefs(useKopfdatenStore());
  const logger = useLogging("managedStimmzettel");

  const selectedWahlvorschlaege = computed(() =>
    stimmzettel.value.wahlvorschlaege.filter(
      (wahlvorschlag) => wahlvorschlag.selected
    )
  );

  const maximalErlaubteStimmenProWaehler = computed(
    () =>
      kopfdaten.value.find((kd) => kd.wahlID === wahlID)
        ?.maximalErlaubteStimmenProWaehler ?? 0
  );

  const countTotalVotes = computed(
    () =>
      kandidatenWithValues.value.reduce(
        (total, kandidat) =>
          total +
          (kandidat.einzelstimmen ?? 0) +
          (kandidat.ungueltigeStimmen ?? 0) +
          (kandidat.reststimmen ?? 0),
        0
      ) + (stimmzettel.value.invalideVotes ?? 0)
  );
  const totalVotesWithoutReststimmen = computed(
    () =>
      kandidatenWithValues.value.reduce(
        (total, kandidat) =>
          total +
          (kandidat.einzelstimmen ?? 0) +
          (kandidat.ungueltigeStimmen ?? 0),
        0
      ) + (stimmzettel.value.invalideVotes ?? 0)
  );
  const countTotalEinzelstimmen = computed(() =>
    kandidatenWithValues.value.reduce(
      (total, kandidat) => total + (kandidat.einzelstimmen ?? 0),
      0
    )
  );
  const countTotalUngueltigeStimmen = computed(
    () =>
      kandidatenWithValues.value.reduce(
        (total, kandidat) => total + (kandidat.ungueltigeStimmen ?? 0),
        0
      ) + (stimmzettel.value.invalideVotes ?? 0)
  );

  const effectiveStimmzettelGueltigkeit = computed(() => {
    if (systemErrors.value.length > 0) {
      return StimmzettelGueltigkeitEnum.BeschlussAusstehend;
    }

    return stimmzettel.value.gueltigkeit;
  });

  const hasAnyValuesSet = computed(() => kandidatenWithValues.value.length > 0);

  const hasSystemErrorAtLeastOneKandidatWithToManyEinzelstimmen = computed(() =>
    kandidatenWithValues.value.some(
      (kandidat) => (kandidat.einzelstimmen ?? 0) > maxEinzelstimmen
    )
  );
  const hasSystemErrorAnyKandidatWithInvalidVotes = computed(() =>
    kandidatenWithValues.value.some(
      (kandidat) => (kandidat.ungueltigeStimmen ?? 0) > 0
    )
  );

  const systemErrors = computed(() => {
    const result: SystemBeschlussgrund[] = [];

    if (hasSystemErrorAnyKandidatWithInvalidVotes.value) {
      result.push({
        reason: SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig,
      });
    }

    if (
      hasSystemErrorAtLeastOneKandidatWithToManyEinzelstimmen.value &&
      countTotalVotes.value <= maximalErlaubteStimmenProWaehler.value
    ) {
      result.push({
        reason:
          SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit,
      });
    }

    if (
      countTotalVotes.value > maximalErlaubteStimmenProWaehler.value ||
      hasSystemErrorToManyListenKreuze.value
    ) {
      result.push({
        reason:
          SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze,
      });
    }

    return result;
  });

  const kandidatenWithValues = computed(() =>
    kandidatenOfStimmzettel.value.filter(_hasKandidatAnyStimmeOrStreichung)
  );

  const stimmenSummary = computed(() => {
    const summary: StimmenSummary = {
      einzelstimmen: 0,
      ungueltigeStimmen: 0,
      reststimmen: 0,
      streichungen: 0,
    };
    return kandidatenWithValues.value.reduce(
      (summary, kandidat) => _updateSummaryByKandidat(summary, kandidat),
      summary
    );
  });

  const wahlvorschlaegeWithListenkreuz = computed(() =>
    stimmzettel.value.wahlvorschlaege.filter(
      (wahlvorschlag) => wahlvorschlag.selected
    )
  );

  const {
    selectWahlvorschlag,
    deselectWahlvorschlag,
    updateReststimmenWhenVotesAdded,
    updateReststimmenWhenVotesRemoved,
  } = useManagedStimmzettelReststimmeUtils(wahlID, stimmenSummary, stimmzettel);

  const remainingVotes = computed(() => {
    return maximalErlaubteStimmenProWaehler.value - countTotalVotes.value;
  });

  const hasSystemErrorToManyListenKreuze = ref(false);

  watch(
    () => stimmzettel.value.invalideVotes,
    () => {
      _refreshWahlvorschlaegeVotes();
    }
  );

  function resetStimmzettel() {
    changeHistory.reset();
    stimmzettel.value.wahlvorschlaege.map((wahlvorschlag) => {
      wahlvorschlag.selected = false;
      wahlvorschlag.kandidaten.map((kandidat) => {
        kandidat.einzelstimmen = null;
        kandidat.ungueltigeStimmen = null;
        kandidat.reststimmen = null;
        kandidat.durchgestrichen = false;
      });
    });
    stimmzettel.value.gueltigkeit = StimmzettelGueltigkeitEnum.Valid;
    stimmzettel.value.invalideVotes = null;
    stimmzettel.value.wahlvorstandBeschlussvorschlag = [];
  }

  /**
   *
   * @param ordnungszahl
   * @param votesToAdd
   *
   * @throws ManagedStimmzettelError when ordnungszahl does not describe a valid kandidat or rules deny action
   */
  function kandidatAddEinzelstimmenOrThrow(
    ordnungszahl: number,
    votesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      votesToAdd,
      "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    addVotesToKandidat(kandidat, votesToAdd);
    changeHistory.registerKandidatEinzelstimmenAdded(kandidat, votesToAdd);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatRemoveEinzelstimmenOrThrow(
    ordnungszahl: number,
    votesToRemove: number
  ) {
    _isNotSafeIntegerThrow(
      votesToRemove,
      "Die Anzahl der zu entfernenden Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (!kandidat.einzelstimmen || kandidat.einzelstimmen < votesToRemove) {
      throw new ManagedStimmzettelError(
        `Von Kandidat*in mit Ordnungszahl ${ordnungszahl} können keine ${votesToRemove} Stimmen abgezogen werden.`
      );
    }
    removeVotesFromKandidat(kandidat, votesToRemove);
    changeHistory.registerKandidatEinzelstimmenRemoved(kandidat, votesToRemove);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatAddUngueltigeStimmenOrThrow(
    ordnungszahl: number,
    invalidVotesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      invalidVotesToAdd,
      "Die Anzahl der hinzuzufügenden ungültigen Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    addInvalidVotesToKandidat(kandidat, invalidVotesToAdd);
    changeHistory.registerKandidatUngueltigeStimmenAdded(
      kandidat,
      invalidVotesToAdd
    );
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatRemoveUngueltigeStimmenOrThrow(
    ordnungszahl: number,
    invalidVotesToRemove: number
  ) {
    _isNotSafeIntegerThrow(
      invalidVotesToRemove,
      "Die Anzahl der zu entfernenden ungültigen Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidat = getKandidatToAddVotesByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (
      !kandidat.ungueltigeStimmen ||
      kandidat.ungueltigeStimmen < invalidVotesToRemove
    ) {
      throw new ManagedStimmzettelError(
        `Von Kandidat*in mit Ordnungszahl ${ordnungszahl} können keine ${invalidVotesToRemove} ungültigen Stimmen abgezogen werden.`
      );
    }
    removeInvalidVotesFromKandidat(kandidat, invalidVotesToRemove);
    changeHistory.registerKandidatUngueltigeStimmenRemoved(
      kandidat,
      invalidVotesToRemove
    );
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatenAddStimmenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number,
    votesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      votesToAdd,
      "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl größer 0 sein."
    );
    const kandidaten = _getKandidatenInRangeOrThrow(
      lowerOrdnungszahl,
      upperOrdnungszahl
    );
    if (kandidaten.filter((kandidat) => kandidat.durchgestrichen).length > 0) {
      throw new ManagedStimmzettelError(
        "Der Bereich enthält mindestens eine Streichung."
      );
    }
    kandidaten.map((kandidat) => addVotesToKandidat(kandidat, votesToAdd));
    updateReststimmenWhenVotesAdded();
    changeHistory.registerKandidatEinzelstimmenRangeAdded(
      kandidaten,
      votesToAdd
    );
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatAddStreichungOrThrow(ordnungszahl: number) {
    const kandidat = getKandidatForStreichungByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (kandidat.durchgestrichen) {
      throw new ManagedStimmzettelError(`Kandidat*in ist bereits gestrichen.`);
    }
    kandidat.durchgestrichen = true;
    changeHistory.registerKandidatStreichungSet(kandidat);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatRemoveStreichungOrThrow(ordnungszahl: number) {
    const kandidat = getKandidatToRemoveStreichungByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (!kandidat.durchgestrichen) {
      throw new ManagedStimmzettelError(
        `Für Kandidat*in mit Ordnungszahl ${ordnungszahl} kann keine Streichung entfernt werden.`
      );
    }
    kandidat.durchgestrichen = false;
    changeHistory.registerKandidatStreichungUnset(kandidat);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatenStreichungenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number
  ) {
    const kandidaten = _getKandidatenInRangeOrThrow(
      lowerOrdnungszahl,
      upperOrdnungszahl
    );
    if (kandidaten.every((kandidat) => kandidat.durchgestrichen)) {
      throw new ManagedStimmzettelError(`Der Bereich ist bereits gestrichen.`);
    }
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = true));
    changeHistory.registerKandidatStreichungRangeSet(kandidaten);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatenRemoveStreichungenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number
  ) {
    const kandidaten = _getKandidatenInRangeOrThrow(
      lowerOrdnungszahl,
      upperOrdnungszahl
    );
    if (kandidaten.every((kandidat) => !kandidat.durchgestrichen)) {
      throw new ManagedStimmzettelError(
        `Im Bereich sind bereits alle Streichungen entfernt.`
      );
    }
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = false));
    changeHistory.registerKandidatStreichungRangeUnset(kandidaten);
    _refreshWahlvorschlaegeVotes();
  }

  function wahlvorschlagAddVotesOrThrow(wahlvorschlagOrdnungszahl: number) {
    const wahlvorschlag = getWahlvorschlagByOrdnungszahl(
      wahlvorschlagOrdnungszahl
    );
    if (!wahlvorschlag) {
      throw new ManagedStimmzettelError(
        `Wahlvorschlag mit Ordnungszahl ${wahlvorschlagOrdnungszahl} existiert nicht.`
      );
    }
    if (wahlvorschlag.selected) {
      throw new ManagedStimmzettelError(
        `Wahlvorschlag ist bereits ausgewählt.`
      );
    }
    selectWahlvorschlag(wahlvorschlag);
    changeHistory.registerWahlvorschlagSelected(wahlvorschlag);
    _refreshWahlvorschlaegeVotes();
  }

  function wahlvorschlagRemoveVotesOrThrow(wahlvorschlagOrdnungszahl: number) {
    const wahlvorschlag = getWahlvorschlagByOrdnungszahl(
      wahlvorschlagOrdnungszahl
    );
    if (!wahlvorschlag) {
      throw new ManagedStimmzettelError(
        `Wahlvorschlag mit Ordnungszahl ${wahlvorschlagOrdnungszahl} existiert nicht.`
      );
    }
    if (!wahlvorschlag.selected) {
      throw new ManagedStimmzettelError(`Wahlvorschlag ist bereits abgewählt.`);
    }
    deselectWahlvorschlag(wahlvorschlag);
    _refreshWahlvorschlaegeVotes();
    changeHistory.registerWahlvorschlagDeselected(wahlvorschlag);
  }

  function _hasKandidatAnyStimmeOrStreichung(kandidat: Kandidat) {
    return (
      kandidat.einzelstimmen !== null ||
      kandidat.ungueltigeStimmen ||
      kandidat.reststimmen ||
      kandidat.durchgestrichen
    );
  }

  function _updateSummaryByKandidat(
    stimmenSummary: StimmenSummary,
    kandidat: Kandidat
  ) {
    if (kandidat.ungueltigeStimmen) {
      stimmenSummary.ungueltigeStimmen += kandidat.ungueltigeStimmen;
    }
    if (kandidat.reststimmen) {
      stimmenSummary.reststimmen += kandidat.reststimmen;
    }
    if (kandidat.durchgestrichen) {
      stimmenSummary.streichungen += 1;
    }
    if (kandidat.einzelstimmen) {
      stimmenSummary.einzelstimmen += kandidat.einzelstimmen;
    }
    return stimmenSummary;
  }

  function _getKandidatenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number
  ): Kandidat[] {
    const kandidaten: Kandidat[] = [];
    for (
      let ordnungszahl = lowerOrdnungszahl;
      ordnungszahl <= upperOrdnungszahl;
      ordnungszahl++
    ) {
      const kandidatenByOrdnungszahl =
        getKandidatToAddVotesForRangeByOrdnungszahl(ordnungszahl);
      if (!kandidatenByOrdnungszahl) {
        throw new ManagedStimmzettelError(
          `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
        );
      }
      kandidatenByOrdnungszahl.map((kandidat) => kandidaten.push(kandidat));
    }
    return kandidaten;
  }

  function _isNotSafeIntegerThrow(value: number, errorMessage: string) {
    if (!Number.isSafeInteger(value) || value <= 0) {
      throw new ManagedStimmzettelError(errorMessage);
    }
  }

  function _refreshWahlvorschlaegeVotes() {
    const wahlvorschlaegeSelected = stimmzettel.value.wahlvorschlaege.filter(
      (wahlvorschlag) => wahlvorschlag.selected
    );
    logger.log(
      `wahlvorschlaegeSelected.length > ${wahlvorschlaegeSelected.length}`
    );
    if (wahlvorschlaegeSelected.length > 0) {
      const totalVotesByUser =
        stimmzettel.value.wahlvorschlaege
          .flatMap((wahlvorschlag) => wahlvorschlag.kandidaten)
          .map(
            (kandidat) =>
              (kandidat.ungueltigeStimmen ?? 0) + (kandidat.einzelstimmen ?? 0)
          )
          .reduce((prev, current) => prev + current, 0) +
        (stimmzettel.value.invalideVotes ?? 0);
      const votesLeftForReststimmen =
        maximalErlaubteStimmenProWaehler.value - totalVotesByUser;
      logger.log(`wahlvorschlagVotesToSpent > ${votesLeftForReststimmen}`);

      if (wahlvorschlaegeSelected.length === 1) {
        const wahlvorschlagToRefresh = selectedWahlvorschlaege.value[0];
        if (wahlvorschlagToRefresh) {
          if (votesLeftForReststimmen > 0) {
            let wahlvorschlagVotesSpend = 0;
            wahlvorschlagToRefresh.kandidaten.forEach((kandidat) => {
              logger.log(
                `onEach - kandidat.einzelstimmen > ${kandidat.einzelstimmen}, kandidat.reststimmen > ${kandidat.reststimmen}, kandidat.ungueltigeStimmen > ${kandidat.ungueltigeStimmen}, wahlvorschlagVotesSpend > ${wahlvorschlagVotesSpend}`
              );
              if (
                (kandidat.einzelstimmen ?? 0) > 0 ||
                kandidat.durchgestrichen ||
                (kandidat.ungueltigeStimmen ?? 0) > 0
              ) {
                kandidat.reststimmen = null;
              } else {
                if (
                  wahlvorschlagVotesSpend <=
                  votesLeftForReststimmen - COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG
                ) {
                  kandidat.reststimmen = COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG;
                  wahlvorschlagVotesSpend += COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG;
                } else {
                  kandidat.reststimmen = null;
                }
              }
            });
          } else {
            wahlvorschlaegeSelected.forEach((wahlvorschlag) => {
              wahlvorschlag.kandidaten.forEach(
                (kandidat) => (kandidat.reststimmen = 0)
              );
            });
          }
        }
      } else {
        //clear current wahlvorschlaege votes
        wahlvorschlaegeSelected.forEach((wahlvorschlag) => {
          wahlvorschlag.kandidaten.forEach(
            (kandidat) => (kandidat.reststimmen = 0)
          );
        });
        hasSystemErrorToManyListenKreuze.value = false;

        //set new wahlvorschlag votes
        const kandidatenThatCouldGetWahlvorschlagVote = wahlvorschlaegeSelected
          .flatMap((wahlvorschlag) => wahlvorschlag.kandidaten)
          .filter(
            (kandidat) =>
              !kandidat.durchgestrichen &&
              (kandidat.einzelstimmen ?? 0) === 0 &&
              (kandidat.ungueltigeStimmen ?? 0) === 0
          );
        const requiredVotesLeftToFulfilListenkreuze =
          kandidatenThatCouldGetWahlvorschlagVote.length *
          COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG;
        logger.log(
          `requiredVotesLeftToFulfilListenkreuze: ${requiredVotesLeftToFulfilListenkreuze}, wahlvorschlagVotesToSpent: ${votesLeftForReststimmen}`
        );
        if (requiredVotesLeftToFulfilListenkreuze > votesLeftForReststimmen) {
          //reset all set wahlvorschlaege votes
          wahlvorschlaegeSelected.forEach((wahlvorschlag) =>
            wahlvorschlag.kandidaten.forEach(
              (kandidat) => (kandidat.reststimmen = null)
            )
          );
          hasSystemErrorToManyListenKreuze.value = true;
        } else {
          kandidatenThatCouldGetWahlvorschlagVote.forEach(
            (kandidat) =>
              (kandidat.reststimmen = COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG)
          );
        }
      }
    }
  }

  return {
    changeHistory,
    effectiveStimmzettelGueltigkeit,
    hasAnyValuesSet,
    hasSystemErrorAtLeastOneKandidatWithToManyEinzelstimmen,
    hasSystemErrorAnyKandidatWithInvalidVotes,
    systemErrors,
    resetStimmzettel,
    kandidatAddEinzelstimmenOrThrow,
    kandidatRemoveEinzelstimmenOrThrow,
    kandidatAddUngueltigeStimmenOrThrow,
    kandidatRemoveUngueltigeStimmenOrThrow,
    kandidatenAddStimmenInRangeOrThrow,
    kandidatAddStreichungOrThrow,
    kandidatRemoveStreichungOrThrow,
    kandidatenStreichungenInRangeOrThrow,
    kandidatenRemoveStreichungenInRangeOrThrow,
    wahlvorschlagAddVotesOrThrow,
    wahlvorschlagRemoveVotesOrThrow,
    stimmzettel: computed(() => stimmzettel.value),
    stimmenSummary,
    wahlvorschlaegeWithListenkreuz,
  };
}
export type ManagedStimmzettel = ReturnType<typeof useManagedStimmzettel>;
