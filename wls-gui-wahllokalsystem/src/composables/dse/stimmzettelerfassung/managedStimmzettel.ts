import type { InputHistoryItem } from "@/types/dse/stimmzettelerfassung/InputHistoryItem.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { StimmenSummary } from "@/types/dse/stimmzettelerfassung/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { InputHistoryTypeEnum } from "@/types/dse/stimmzettelerfassung/InputHistoryTypeEnum.ts";

/**
 * Check UI/UX Adr to see the rules:
 * https://it-at-m.github.io/Wahllokalsystem/technik/adr/ui/adr010-dse-stimmvergabe-stimmen-ergaenzen.html
 *
 * @param stimmzettel
 * @param wahlID
 */
export function useManagedStimmzettel(
  stimmzettel: Ref<Stimmzettel>,
  wahlID: string
) {
  const changeHistory = ref<InputHistoryItem[]>([]);

  const kandidatenOfStimmzettel = computed(() =>
    stimmzettel.value.wahlvorschlaege
      .map((wahlvorschlag) => wahlvorschlag.kandidaten)
      .flat()
  );

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
    const kopfdatenStore = useKopfdatenStore();
    const { kopfdaten } = storeToRefs(kopfdatenStore);
    const maximalErlaubteStimmenProWaehler =
      kopfdaten.value.find((kd) => kd.wahlID === wahlID)
        ?.maximalErlaubteStimmenProWaehler ?? 0;

    const currentGesamtStimmen =
      stimmenSummary.value.ungueltigeStimmen +
      stimmenSummary.value.einzelstimmen +
      stimmenSummary.value.reststimmen;
    return maximalErlaubteStimmenProWaehler - currentGesamtStimmen;
  });

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
        `Von Kandidat*in mit Ordnungszahl ${ordnungszahl} können keine ${votesToRemove} Stimmen abgezogen werden.`
      );
    }

    _internalRemoveVotesFromKandidat(kandidat, votesToRemove);
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

    _internalRemoveInvalidVotesFromKandidat(kandidat, invalidVotesToRemove);
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
    _updateReststimmenWhenVotesAdded();
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${" + " + votesToAdd + (votesToAdd > 1 ? " Stimmen" : " Stimme")}`,
        kandidat.name,
      ],
    });
  }

  function _internalRemoveVotesFromKandidat(
    kandidat: Kandidat,
    numberOfVotesToRemove: number
  ) {
    const votesToRemove = Math.abs(numberOfVotesToRemove);
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    kandidat.einzelstimmen = currentEinzelstimmen - votesToRemove;
    _updateReststimmenWhenVotesRemoved();
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${" - " + votesToRemove + (votesToRemove > 1 ? " Stimmen" : " Stimme")}`,
        kandidat.name,
      ],
    });
  }

  function _internalAddInvalidVotesToKandidat(
    kandidat: Kandidat,
    numberOfInvalidVotesToAdd: number
  ) {
    const invalidVotesToAdd = Math.abs(numberOfInvalidVotesToAdd);
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen = currentUngueltigeStimmen + invalidVotesToAdd;
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${" + " + invalidVotesToAdd + " ungültige " + (invalidVotesToAdd > 1 ? "Stimmen" : "Stimme")}`,
        kandidat.name,
      ],
    });
  }

  function _internalRemoveInvalidVotesFromKandidat(
    kandidat: Kandidat,
    numberOfVotesToRemove: number
  ) {
    const invalidVotesToRemove = Math.abs(numberOfVotesToRemove);
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen =
      currentUngueltigeStimmen - invalidVotesToRemove;
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REMOVE_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${" - " + invalidVotesToRemove + " ungültige " + (invalidVotesToRemove > 1 ? "Stimmen" : "Stimme")}`,
        kandidat.name,
      ],
    });
  }

  function _internalAddVotesToKandidatenRange(
    kandidaten: Kandidat[],
    numberOfVotesToAdd: number
  ) {
    const votesToAdd = Math.abs(numberOfVotesToAdd);
    kandidaten.map((kandidat) => {
      const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
      kandidat.einzelstimmen = currentEinzelstimmen + votesToAdd;
      _updateReststimmenWhenVotesAdded();
    });
    changeHistory.value.push({
      type: InputHistoryTypeEnum.VOTE_RANGE,
      text: [
        `${kandidaten[0].ordnungszahl}-${kandidaten[kandidaten.length - 1].ordnungszahl}${" + " + votesToAdd + (votesToAdd > 1 ? " Stimmen" : " Stimme")}`,
      ],
    });
  }

  function _internalAddStreichungToKandidat(kandidat: Kandidat) {
    kandidat.durchgestrichen = true;
    changeHistory.value.push({
      type: InputHistoryTypeEnum.DISCARD_KANDIDAT,
      text: [`${kandidat.ordnungszahl}`, kandidat.name],
    });
  }

  function _internalRemoveStreichungFromKandidat(kandidat: Kandidat) {
    kandidat.durchgestrichen = false;
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REVOKE_DISCARDED_KANDIDAT,
      text: [`${kandidat.ordnungszahl}`, kandidat.name],
    });
  }

  function _internalAddStreichungenToKandidatenRange(kandidaten: Kandidat[]) {
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = true));
    changeHistory.value.push({
      type: InputHistoryTypeEnum.DISCARD_RANGE,
      text: [
        `${kandidaten[0].ordnungszahl}-${kandidaten[kandidaten.length - 1].ordnungszahl}`,
      ],
    });
  }

  function _internalRemoveStreichungenFromKandidatenRange(
    kandidaten: Kandidat[]
  ) {
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = false));
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REVOKE_DISCARDED_KANDIDAT,
      text: [
        `${kandidaten[0].ordnungszahl}-${kandidaten[kandidaten.length - 1].ordnungszahl}`,
      ],
    });
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
    changeHistory.value.push({
      type: InputHistoryTypeEnum.SET_WAHLVORSCHLAG,
      text: [`${wahlvorschlag.kurzname}`],
    });
  }

  function _internalRemoveVotesFromWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    wahlvorschlag.kandidaten.map((kandidat) => (kandidat.reststimmen = 0));
    wahlvorschlag.selected = false;
    changeHistory.value.push({
      type: InputHistoryTypeEnum.REVOKE_WAHLVORSCHLAG,
      text: [`${wahlvorschlag.kurzname}`],
    });
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

  function _updateReststimmenWhenVotesAdded() {
    if (remainingVotes.value < 0) {
      const wahlvorschlagToUpdate = stimmzettel.value.wahlvorschlaege.find(
        (wahlvorschlag) =>
          wahlvorschlag.kandidaten.some(
            (kandidat) =>
              kandidat.reststimmen !== null && kandidat.reststimmen > 0
          )
      );
      if (wahlvorschlagToUpdate) {
        for (let i = remainingVotes.value; i < 0; i++) {
          const kandidatToUpdate = wahlvorschlagToUpdate?.kandidaten
            .slice()
            .reverse()
            .find(
              (kandidat) =>
                kandidat.reststimmen !== null && kandidat.reststimmen > 0
            );
          if (kandidatToUpdate) {
            kandidatToUpdate.reststimmen = 0;
          }
        }
      }
    }
  }

  function _updateReststimmenWhenVotesRemoved() {
    if (remainingVotes.value > 0) {
      const wahlvorschlagToUpdate = stimmzettel.value.wahlvorschlaege.find(
        (wahlvorschlag) => wahlvorschlag.selected
      );
      if (wahlvorschlagToUpdate) {
        for (let i = remainingVotes.value; i > 0; i--) {
          const kandidatToUpdate = wahlvorschlagToUpdate.kandidaten.find(
            (kandidat) => !kandidat.reststimmen || kandidat.reststimmen === 0
          );
          if (kandidatToUpdate) {
            kandidatToUpdate.reststimmen = 1;
          }
        }
      }
    }
  }

  function _isNotSafeIntegerThrow(value: number, errorMessage: string) {
    if (!Number.isSafeInteger(value)) {
      throw new ManagedStimmzettelError(errorMessage);
    }
  }

  return {
    changeHistoryInReverseOrder: computed(() =>
      [...changeHistory.value].reverse()
    ),
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
