import type { InputHistoryItem } from "@/types/dse/InputHistoryItem.ts";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { StimmenSummary } from "@/types/dse/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { computed, ref } from "vue";

import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { InputHistoryTypeEnum } from "@/types/dse/InputHistoryTypeEnum.ts";

export const WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL = 100;

/**
 * Check UI/UX Adr to see the rules:
 * https://it-at-m.github.io/Wahllokalsystem/technik/adr/ui/adr010-dse-stimmvergabe-stimmen-ergaenzen.html
 *
 * @param stimmzettel
 */
export function useManagedStimmzettel(stimmzettel: Ref<Stimmzettel>) {
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
    if (!Number.isSafeInteger) {
      throw new ManagedStimmzettelError(
        "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl sein."
      );
    }
    const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }

    _internalAddVotesToKandidat(kandidat, votesToAdd);
  }

  function kandidatAddUngueltigeStimmenOrThrow(
    ordnungszahl: number,
    invalidVotesToAdd: number
  ) {
    if (!Number.isSafeInteger) {
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
    if (!Number.isSafeInteger) {
      throw new ManagedStimmzettelError(
        "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl sein."
      );
    }
    const kandidaten = [];
    for (
      let ordnungszahl = lowerOrdnungszahl;
      ordnungszahl <= upperOrdnungszahl;
      ordnungszahl++
    ) {
      const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
      if (!kandidat) {
        throw new ManagedStimmzettelError(
          `Kandidat mit Ordnungszahl ${ordnungszahl} existiert nicht.`
        );
      }
      kandidaten.push(kandidat);
    }

    _internalAddVotesToKandidatenRange(kandidaten, votesToAdd);
  }

  function kandidatAddStreichungOrThrow(ordnungszahl: number) {
    const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
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
    const kandidaten = [];
    for (
      let ordnungszahl = lowerOrdnungszahl;
      ordnungszahl <= upperOrdnungszahl;
      ordnungszahl++
    ) {
      const kandidat = _getKandidatToAddVotesByUserByOrdnungszahl(ordnungszahl);
      if (!kandidat) {
        throw new ManagedStimmzettelError(
          `Kandidat mit Ordnungszahl ${ordnungszahl} existiert nicht.`
        );
      }
      kandidaten.push(kandidat);
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

    _internalAddVotesToWahlvorschlag(wahlvorschlag);
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

  function _internalAddVotesToKandidat(
    kandidat: Kandidat,
    numberOfVotesToAdd: number
  ) {
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    kandidat.einzelstimmen =
      currentEinzelstimmen + Math.abs(numberOfVotesToAdd);
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${numberOfVotesToAdd > 1 ? " + " + numberOfVotesToAdd + " Stimmen" : ""}`,
        kandidat.name,
      ],
    });
  }

  function _internalAddInvalidVotesToKandidat(
    kandidat: Kandidat,
    numberOfInvalidVotesToAdd: number
  ) {
    const currentUngueltigeStimmen = kandidat.ungueltigeStimmen ?? 0;
    kandidat.ungueltigeStimmen =
      currentUngueltigeStimmen + Math.abs(numberOfInvalidVotesToAdd);
    changeHistory.value.push({
      type: InputHistoryTypeEnum.ADD_USER_VOTE,
      text: [
        `${kandidat.ordnungszahl}${numberOfInvalidVotesToAdd > 1 ? " + " + numberOfInvalidVotesToAdd + " ungültige Stimmen" : ""}`,
        kandidat.name,
      ],
    });
  }

  function _internalAddVotesToKandidatenRange(
    kandidaten: Kandidat[],
    numberOfVotesToAdd: number
  ) {
    kandidaten.map((kandidat) => {
      const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
      kandidat.einzelstimmen =
        currentEinzelstimmen + Math.abs(numberOfVotesToAdd);
    });
    changeHistory.value.push({
      type: InputHistoryTypeEnum.VOTE_RANGE,
      text: [
        `${kandidaten[0].ordnungszahl}-${kandidaten[kandidaten.length - 1].ordnungszahl}${numberOfVotesToAdd > 1 ? " + " + numberOfVotesToAdd + " Stimmen" : ""}`,
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
    wahlvorschlag.kandidaten.map((kandidat) => {
      const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
      kandidat.einzelstimmen = currentEinzelstimmen + 1;
    });
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

  return {
    changeHistoryInReverOrder: computed(() =>
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
