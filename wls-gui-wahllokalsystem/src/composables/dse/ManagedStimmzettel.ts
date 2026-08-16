import type { InputHistoryItem } from "@/types/dse/InputHistoryItem.ts";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Ref } from "vue";

import { computed, ref } from "vue";

import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";
import { InputHistoryTypeEnum } from "@/types/dse/InputHistoryTypeEnum.ts";

/**
 * Check UI/UX Adr to see the rules:
 * https://it-at-m.github.io/Wahllokalsystem/technik/adr/ui/adr010-dse-stimmvergabe-stimmen-ergaenzen.html
 *
 * @param stimmzettel
 */

export const WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL = 100;

export function useManagedStimmzettel(stimmzettel: Ref<Stimmzettel>) {
  const changeHistory = ref<InputHistoryItem[]>([]);

  const kandidatenOfStimmzettel = computed(() =>
    stimmzettel.value.wahlvorschlaege
      .map((wahlvorschlag) => wahlvorschlag.kandidaten)
      .flat()
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
      text: [`${kandidat.ordnungszahl}`, kandidat.name],
    });
  }

  return {
    changeHistoryInReverOrder: computed(() => changeHistory.value.reverse()),
    kandidatAddEinzelstimmenOrThrow,
    stimmzettel: computed(() => stimmzettel.value),
  };
}
export type ManagedStimmzettel = ReturnType<typeof useManagedStimmzettel>;
