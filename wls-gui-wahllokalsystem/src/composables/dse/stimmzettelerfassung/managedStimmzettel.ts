import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { StimmenSummary } from "@/types/dse/stimmzettelerfassung/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useStimmzettelChangeHistory } from "@/composables/dse/stimmzettelerfassung/stimmzettelChangeHistory.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

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
  const changeHistory = useStimmzettelChangeHistory();

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

  function _internalAddVotesToKandidat(
    kandidat: Kandidat,
    numberOfVotesToAdd: number
  ) {
    const votesToAdd = Math.abs(numberOfVotesToAdd);
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    kandidat.einzelstimmen = currentEinzelstimmen + votesToAdd;
    _updateReststimmen();
    _addVotesForKandidatToWahlvorschlag(kandidat, votesToAdd);
    changeHistory.registerKandidatEinzelstimmenAdded(kandidat, votesToAdd);
  }

  function _internalAddInvalidVotesToKandidat(
    kandidat: Kandidat,
    numberOfInvalidVotesToAdd: number
  ) {
    const invalidVotesToAdd = Math.abs(numberOfInvalidVotesToAdd);
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen = currentUngueltigeStimmen + invalidVotesToAdd;
    _addInvalidVotesForKandidatToWahlvorschlag(kandidat, invalidVotesToAdd);
    changeHistory.registerKandidatUngueltigeStimmenAdded(
      kandidat,
      invalidVotesToAdd
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
      _updateReststimmen();
      _addVotesForKandidatToWahlvorschlag(kandidat, votesToAdd);
    });
    changeHistory.registerKandidatEinzelstimmenRangeSet(kandidaten, votesToAdd);
  }

  function _internalAddStreichungToKandidat(kandidat: Kandidat) {
    kandidat.durchgestrichen = true;
    changeHistory.registerKandidatStreichungSet(kandidat);
  }

  function _internalAddStreichungenToKandidatenRange(kandidaten: Kandidat[]) {
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = true));
    changeHistory.registerKandidatStreichungRangeSet(kandidaten);
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

  function _addVotesForKandidatToWahlvorschlag(
    kandidat: Kandidat,
    votesToAdd: number
  ) {
    const wahlvorschlagToUpdate = _getWahlvorschlagForKandidat(kandidat);
    if (wahlvorschlagToUpdate) {
      wahlvorschlagToUpdate.gueltigeStimmen += votesToAdd;
    }
  }

  function _addInvalidVotesForKandidatToWahlvorschlag(
    kandidat: Kandidat,
    invalidVotesToAdd: number
  ) {
    const wahlvorschlagToUpdate = _getWahlvorschlagForKandidat(kandidat);
    if (wahlvorschlagToUpdate) {
      wahlvorschlagToUpdate.ungueltigeStimmen += invalidVotesToAdd;
    }
  }

  function _updateReststimmen() {
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

  function _getWahlvorschlagForKandidat(kandidat: Kandidat) {
    return stimmzettel.value.wahlvorschlaege.find((wahlvorschlag) =>
      wahlvorschlag.kandidaten.find((k) => k.kandidatId === kandidat.kandidatId)
    );
  }

  function _isNotSafeIntegerThrow(value: number, errorMessage: string) {
    if (!Number.isSafeInteger(value)) {
      throw new ManagedStimmzettelError(errorMessage);
    }
  }

  return {
    changeHistory,

    kandidatAddEinzelstimmenOrThrow,
    kandidatAddUngueltigeStimmenOrThrow,
    kandidatenAddStimmenInRangeOrThrow,
    kandidatAddStreichungOrThrow,
    kandidatenStreichungenInRangeOrThrow,
    wahlvorschlagAddVotesOrThrow,
    stimmzettel: computed(() => stimmzettel.value),
    stimmenSummary,
    wahlvorschlaegeWithListenkreuz,
  };
}
export type ManagedStimmzettel = ReturnType<typeof useManagedStimmzettel>;
