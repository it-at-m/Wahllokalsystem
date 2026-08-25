import type { InputHistoryItem } from "@/types/dse/InputHistoryItem.ts";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { StimmenSummary } from "@/types/dse/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";
import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { InputHistoryTypeEnum } from "@/types/dse/InputHistoryTypeEnum.ts";

export const WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL = 100;

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
    if (!Number.isSafeInteger(votesToAdd)) {
      throw new ManagedStimmzettelError(
        "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl sein."
      );
    }
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
    if (!Number.isSafeInteger(invalidVotesToAdd)) {
      throw new ManagedStimmzettelError(
        "Die Anzahl der hinzuzufügenden ungültigen Stimmen muss eine ganze Zahl sein."
      );
    }
    const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }

    _internalAddInvalidVotesToKandidat(kandidat, invalidVotesToAdd);
  }

  function kandidatenAddStimmenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number,
    votesToAdd: number
  ) {
    if (lowerOrdnungszahl > upperOrdnungszahl) {
      throw new ManagedStimmzettelError(
        `Der Bereich ${lowerOrdnungszahl}-${upperOrdnungszahl} ist ungültig.`
      );
    }
    if (!Number.isSafeInteger(votesToAdd)) {
      throw new ManagedStimmzettelError(
        "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl sein."
      );
    }
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
        `Kandidat mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }

    _internalAddStreichungToKandidat(kandidat);
  }

  function kandidatenStreichungenInRangeOrThrow(
    lowerOrdnungszahl: number,
    upperOrdnungszahl: number
  ) {
    if (lowerOrdnungszahl > upperOrdnungszahl) {
      throw new ManagedStimmzettelError(
        `Der Bereich ${lowerOrdnungszahl}-${upperOrdnungszahl} ist ungültig.`
      );
    }
    const kandidaten = _getKandidatenInRangeOrThrow(
      lowerOrdnungszahl,
      upperOrdnungszahl
    );

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
    if (firstNennungWithoutDurchstreichung) {
      return firstNennungWithoutDurchstreichung;
    }

    return kandidatenForListenPosition[0];
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
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${votesToAdd > 1 ? " + " + votesToAdd + " Stimmen" : ""}`,
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
        `${kandidat.ordnungszahl}${invalidVotesToAdd > 1 ? " + " + invalidVotesToAdd + " ungültige Stimmen" : " + 1 ungültige Stimme"}`,
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
    });
    changeHistory.value.push({
      type: InputHistoryTypeEnum.VOTE_RANGE,
      text: [
        `${kandidaten[0].ordnungszahl}-${kandidaten[kandidaten.length - 1].ordnungszahl}${votesToAdd > 1 ? " + " + votesToAdd + " Stimmen" : ""}`,
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

  function _internalAddStreichungenToKandidatenRange(kandidaten: Kandidat[]) {
    kandidaten.map((kandidat) => (kandidat.durchgestrichen = true));
    changeHistory.value.push({
      type: InputHistoryTypeEnum.DISCARD_RANGE,
      text: [
        `${kandidaten[0].ordnungszahl}-${kandidaten[kandidaten.length - 1].ordnungszahl}`,
      ],
    });
  }

  function _internalAddVotesToWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    const kopfdatenStore = useKopfdatenStore();
    const { kopfdaten } = storeToRefs(kopfdatenStore);
    const maximalErlaubteStimmenProWaehler =
      kopfdaten.value.find((kd) => kd.wahlID === wahlID)
        ?.maximalErlaubteStimmenProWaehler ?? 0;

    const currentGesamtStimmen =
      stimmenSummary.value.ungueltigeStimmen +
      stimmenSummary.value.einzelstimmen +
      stimmenSummary.value.reststimmen;
    let reststimmen = maximalErlaubteStimmenProWaehler - currentGesamtStimmen;
    let index = 0;
    while (index < reststimmen && index < wahlvorschlag.kandidaten.length) {
      const kandidat = wahlvorschlag.kandidaten[index];
      if (!kandidat.durchgestrichen) {
        kandidat.reststimmen = 1;
      } else {
        reststimmen++;
      }
      index++;
    }
    wahlvorschlag.selected = true;
    changeHistory.value.push({
      type: InputHistoryTypeEnum.SET_WAHLVORSCHLAG,
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
          `Kandidat mit Ordnungszahl ${ordnungszahl} existiert nicht.`
        );
      }
      kandidatenByOrdnungszahl.map((kandidat) => kandidaten.push(kandidat));
    }
    return kandidaten;
  }

  return {
    changeHistoryInReverseOrder: computed(() =>
      [...changeHistory.value].reverse()
    ),
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
