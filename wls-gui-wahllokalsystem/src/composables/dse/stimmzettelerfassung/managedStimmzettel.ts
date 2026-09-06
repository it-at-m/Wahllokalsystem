import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { StimmenSummary } from "@/types/dse/stimmzettelerfassung/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
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
  const { kopfdaten } = storeToRefs(useKopfdatenStore());
  const logger = useLogging("managedStimmzettel");

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

  const kandidatenOfStimmzettel = computed(() =>
    stimmzettel.value.wahlvorschlaege
      .map((wahlvorschlag) => wahlvorschlag.kandidaten)
      .flat()
  );

  const selectedWahlvorschlaege = computed(() =>
    stimmzettel.value.wahlvorschlaege.filter(
      (wahlvorschlag) => wahlvorschlag.selected
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
      "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl sein."
    );
    const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }

    _internalAddVotesToKandidat(kandidat, votesToAdd);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatRemoveEinzelstimmenOrThrow(
    ordnungszahl: number,
    votesToRemove: number
  ) {
    _isNotSafeIntegerThrow(
      votesToRemove,
      "Die Anzahl der zu entfernenden Stimmen muss eine ganze Zahl sein."
    );
    const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (!kandidat.einzelstimmen || kandidat.einzelstimmen < votesToRemove) {
      throw new ManagedStimmzettelError(
        `Von Kandidat*in mit Ordnungszahl ${ordnungszahl} kann diese Menge an Stimmen nicht abgezogen werden.`
      );
    }

    _internalRemoveVotesFromKandidat(kandidat, votesToRemove);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatAddUngueltigeStimmenOrThrow(
    ordnungszahl: number,
    invalidVotesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      invalidVotesToAdd,
      "Die Anzahl der hinzuzufügenden ungültigen Stimmen muss eine ganze Zahl sein."
    );
    const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }

    _internalAddInvalidVotesToKandidat(kandidat, invalidVotesToAdd);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatRemoveUngueltigeStimmenOrThrow(
    ordnungszahl: number,
    invalidVotesToRemove: number
  ) {
    _isNotSafeIntegerThrow(
      invalidVotesToRemove,
      "Die Anzahl der zu entfernenden ungültigen Stimmen muss eine ganze Zahl sein."
    );
    const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
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
        `Von Kandidat*in mit Ordnungszahl ${ordnungszahl} kann diese Menge an ungültigen Stimmen nicht abgezogen werden.`
      );
    }

    _internalRemoveInvalidVotesFromKandidat(kandidat, invalidVotesToRemove);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatenAddStimmenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number,
    votesToAdd: number
  ) {
    _isNotSafeIntegerThrow(
      votesToAdd,
      "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl sein."
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

    _internalAddVotesToKandidatenRange(kandidaten, votesToAdd);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatAddStreichungOrThrow(ordnungszahl: number) {
    const kandidat = _getKandidatForStreichungByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat*in mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }
    if (kandidat.durchgestrichen) {
      throw new ManagedStimmzettelError(`Kandidat*in ist bereits gestrichen.`);
    }
    _internalAddStreichungToKandidat(kandidat);
    _refreshWahlvorschlaegeVotes();
  }

  function kandidatRemoveStreichungOrThrow(ordnungszahl: number) {
    const kandidat = _getKandidatToRemoveStreichungByOrdnungszahl(ordnungszahl);
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
    _internalRemoveStreichungFromKandidat(kandidat);
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

    _internalAddStreichungenToKandidatenRange(kandidaten);
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
    _internalRemoveStreichungenFromKandidatenRange(kandidaten);
    _refreshWahlvorschlaegeVotes();
  }

  function wahlvorschlagAddVotesOrThrow(wahlvorschlagOrdnungszahl: number) {
    const wahlvorschlag = _getWahlvorschlagToAddVotesByOrdnungszahl(
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
    _internalAddVotesToWahlvorschlag(wahlvorschlag);
    _refreshWahlvorschlaegeVotes();
  }

  function wahlvorschlagRemoveVotesOrThrow(wahlvorschlagOrdnungszahl: number) {
    const wahlvorschlag = _getWahlvorschlagToAddVotesByOrdnungszahl(
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
    _internalRemoveVotesFromWahlvorschlag(wahlvorschlag);
    _refreshWahlvorschlaegeVotes();
  }

  function _getKandidatToAddVotesForRangeByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl = kandidatenOfStimmzettel.value.filter(
      (kandidat) => kandidat.ordnungszahl === ordnungszahl
    );
    return kandidatenWithOrdnungszahl.length === 0
      ? undefined
      : kandidatenWithOrdnungszahl;
  }

  function _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl = kandidatenOfStimmzettel.value.filter(
      (kandidat) => kandidat.ordnungszahl === ordnungszahl
    );

    if (kandidatenWithOrdnungszahl.length === 0) {
      return undefined;
    } else {
      return _findKandidatToAddEinzelstimme(kandidatenWithOrdnungszahl);
    }
  }

  function _getKandidatForStreichungByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl = kandidatenOfStimmzettel.value.filter(
      (kandidat) => kandidat.ordnungszahl === ordnungszahl
    );

    if (kandidatenWithOrdnungszahl.length === 0) {
      return undefined;
    } else {
      return _findKandidatToAddStreichung(kandidatenWithOrdnungszahl);
    }
  }

  function _getKandidatToRemoveStreichungByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl = kandidatenOfStimmzettel.value.filter(
      (kandidat) => kandidat.ordnungszahl === ordnungszahl
    );

    if (kandidatenWithOrdnungszahl.length === 0) {
      return undefined;
    } else {
      return _findKandidatToRemoveStreichung(kandidatenWithOrdnungszahl);
    }
  }

  function _getWahlvorschlagToAddVotesByOrdnungszahl(ordnungszahl: number) {
    return stimmzettel.value.wahlvorschlaege.find(
      (wahlvorschlag) => wahlvorschlag.ordnungszahl === ordnungszahl
    );
  }

  function _findKandidatToAddEinzelstimme(
    kandidatenForListenPosition: Kandidat[]
  ) {
    //has any kandidat already uservotes?
    const kandidatWithEinzelstimmen = kandidatenForListenPosition.find(
      (kandidat) => kandidat.einzelstimmen !== null
    );
    if (kandidatWithEinzelstimmen) {
      return kandidatWithEinzelstimmen;
    }

    //get first unused nennung
    const firstNennungWithoutDurchstreichung = kandidatenForListenPosition.find(
      (kandidat) => !kandidat.durchgestrichen
    );
    return firstNennungWithoutDurchstreichung ?? kandidatenForListenPosition[0];
  }

  function _findKandidatToAddStreichung(
    kandidatenForListenPosition: Kandidat[]
  ) {
    const kandidatWithoutEinzelstimmenAndDurchstreichung =
      kandidatenForListenPosition.find(
        (kandidat) =>
          kandidat.einzelstimmen === null && !kandidat.durchgestrichen
      );
    if (kandidatWithoutEinzelstimmenAndDurchstreichung) {
      return kandidatWithoutEinzelstimmenAndDurchstreichung;
    }

    const firstNennungWithoutDurchstreichung = kandidatenForListenPosition.find(
      (kandidat) => !kandidat.durchgestrichen
    );
    if (firstNennungWithoutDurchstreichung) {
      return firstNennungWithoutDurchstreichung;
    }

    return kandidatenForListenPosition[0];
  }

  function _findKandidatToRemoveStreichung(
    kandidatenForListenPosition: Kandidat[]
  ) {
    const kandidatWithDurchstreichung = kandidatenForListenPosition.find(
      (kandidat) => kandidat.durchgestrichen
    );
    if (kandidatWithDurchstreichung) {
      return kandidatWithDurchstreichung;
    }
    return kandidatenForListenPosition[0];
  }

  function _internalAddVotesToKandidat(
    kandidat: Kandidat,
    numberOfVotesToAdd: number
  ) {
    const votesToAdd = Math.abs(numberOfVotesToAdd);
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    kandidat.einzelstimmen = currentEinzelstimmen + votesToAdd;
    changeHistory.registerKandidatEinzelstimmenAdded(kandidat, votesToAdd);
  }

  function _internalRemoveVotesFromKandidat(
    kandidat: Kandidat,
    numberOfVotesToRemove: number
  ) {
    const votesToRemove = Math.abs(numberOfVotesToRemove);
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    const newValue = currentEinzelstimmen - votesToRemove;
    kandidat.einzelstimmen = newValue > 0 ? newValue : null;
    changeHistory.registerKandidatEinzelstimmenRemoved(kandidat, votesToRemove);
  }

  function _internalAddInvalidVotesToKandidat(
    kandidat: Kandidat,
    numberOfInvalidVotesToAdd: number
  ) {
    const invalidVotesToAdd = Math.abs(numberOfInvalidVotesToAdd);
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen = currentUngueltigeStimmen + invalidVotesToAdd;
    changeHistory.registerKandidatUngueltigeStimmenAdded(
      kandidat,
      invalidVotesToAdd
    );
  }

  function _internalRemoveInvalidVotesFromKandidat(
    kandidat: Kandidat,
    numberOfVotesToRemove: number
  ) {
    const invalidVotesToRemove = Math.abs(numberOfVotesToRemove);
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen =
      currentUngueltigeStimmen - invalidVotesToRemove;
    changeHistory.registerKandidatUngueltigeStimmenRemoved(
      kandidat,
      invalidVotesToRemove
    );
  }

  function _internalAddVotesToKandidatenRange(
    kandidaten: Kandidat[],
    numberOfVotesToAdd: number
  ) {
    const votesToAdd = Math.abs(numberOfVotesToAdd);
    kandidaten.map((kandidat) => {
      const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
      kandidat.einzelstimmen = currentEinzelstimmen + votesToAdd;
    });
    changeHistory.registerKandidatEinzelstimmenRangeAdded(
      kandidaten,
      votesToAdd
    );
  }

  function _internalAddStreichungToKandidat(kandidat: Kandidat) {
    kandidat.durchgestrichen = true;
    changeHistory.registerKandidatStreichungSet(kandidat);
  }

  function _internalRemoveStreichungFromKandidat(kandidat: Kandidat) {
    kandidat.durchgestrichen = false;
    changeHistory.registerKandidatStreichungUnset(kandidat);
  }

  function _internalAddStreichungenToKandidatenRange(kandidaten: Kandidat[]) {
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = true));
    changeHistory.registerKandidatStreichungRangeSet(kandidaten);
  }

  function _internalRemoveStreichungenFromKandidatenRange(
    kandidaten: Kandidat[]
  ) {
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = false));
    changeHistory.registerKandidatStreichungRangeUnset(kandidaten);
  }

  function _internalAddVotesToWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    let remainingVotesForWahlvorschlag = remainingVotes.value;
    let index = 0;
    while (
      index < remainingVotesForWahlvorschlag &&
      index < wahlvorschlag.kandidaten.length
    ) {
      const kandidat = wahlvorschlag.kandidaten[index];
      if (
        !kandidat.durchgestrichen &&
        !kandidat.einzelstimmen &&
        !kandidat.ungueltigeStimmen
      ) {
        kandidat.reststimmen = 1;
      } else {
        remainingVotesForWahlvorschlag++;
      }
      index++;
    }
    wahlvorschlag.selected = true;
    changeHistory.registerWahlvorschlagSelected(wahlvorschlag);
  }

  function _internalRemoveVotesFromWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    wahlvorschlag.kandidaten.map((kandidat) => (kandidat.reststimmen = 0));
    wahlvorschlag.selected = false;
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
        _getKandidatToAddVotesForRangeByOrdnungszahl(ordnungszahl);
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
    if (!Number.isSafeInteger(value)) {
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
