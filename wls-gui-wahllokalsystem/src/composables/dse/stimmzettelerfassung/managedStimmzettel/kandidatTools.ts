import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Ref } from "vue";

import { computed } from "vue";

export function useKandidatTools(stimmzettel: Ref<Stimmzettel>) {
  const kandidatenOfStimmzettel = computed(() =>
    stimmzettel.value.wahlvorschlaege
      .map((wahlvorschlag) => wahlvorschlag.kandidaten)
      .flat()
  );

  function getKandidatToAddVotesByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl =
      _getKandidatenByOrdnungszahl(ordnungszahl);
    return kandidatenWithOrdnungszahl.length === 0
      ? undefined
      : _findKandidatToAddEinzelstimme(kandidatenWithOrdnungszahl);
  }

  function getKandidatToAddVotesForRangeByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl =
      _getKandidatenByOrdnungszahl(ordnungszahl);
    return kandidatenWithOrdnungszahl.length === 0
      ? undefined
      : kandidatenWithOrdnungszahl;
  }

  function getKandidatForStreichungByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl =
      _getKandidatenByOrdnungszahl(ordnungszahl);
    return kandidatenWithOrdnungszahl.length === 0
      ? undefined
      : _findKandidatToAddStreichung(kandidatenWithOrdnungszahl);
  }

  function getKandidatToRemoveStreichungByOrdnungszahl(ordnungszahl: number) {
    const kandidatenWithOrdnungszahl =
      _getKandidatenByOrdnungszahl(ordnungszahl);
    return kandidatenWithOrdnungszahl.length === 0
      ? undefined
      : _findKandidatToRemoveStreichung(kandidatenWithOrdnungszahl);
  }

  function _getKandidatenByOrdnungszahl(ordnungszahl: number) {
    return kandidatenOfStimmzettel.value.filter(
      (kandidat) => kandidat.ordnungszahl === ordnungszahl
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
    return firstNennungWithoutDurchstreichung ?? kandidatenForListenPosition[0];
  }

  function _findKandidatToRemoveStreichung(
    kandidatenForListenPosition: Kandidat[]
  ) {
    const kandidatWithDurchstreichung = kandidatenForListenPosition.find(
      (kandidat) => kandidat.durchgestrichen
    );
    return kandidatWithDurchstreichung ?? kandidatenForListenPosition[0];
  }

  return {
    kandidatenOfStimmzettel,
    getKandidatToAddVotesByOrdnungszahl,
    getKandidatToAddVotesForRangeByOrdnungszahl,
    getKandidatForStreichungByOrdnungszahl,
    getKandidatToRemoveStreichungByOrdnungszahl,
  };
}
