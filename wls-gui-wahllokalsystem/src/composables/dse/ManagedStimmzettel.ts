import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { ManagedStimmzettelError } from "@/types/dse/error/ManagedStimmzettelError.ts";

/**
 * Check UI/UX Adr to see the rules:
 * https://it-at-m.github.io/Wahllokalsystem/technik/adr/ui/adr010-dse-stimmvergabe-stimmen-ergaenzen.html
 *
 * @param stimmzettel
 */

export const WAHLVORSCHLAG_NUMBER_MULTIPLER_FOR_ORDNUNGSZAHL = 100;

export function useManagedStimmzettel(stimmzettel: Stimmzettel) {
  /**
   *
   * @param ordnungszahl
   * @param votesToAdd
   *
   * @throws ManagedStimmzettelError when ordnungszahl does not describe a valid kandidat or rules deny action
   */
  function kandidatAddVotesOrThrow(ordnungszahl: number, votesToAdd: number) {
    if (!Number.isSafeInteger) {
      throw new ManagedStimmzettelError(
        "Die Anzahl der hinzuzufügenden Stimmen muss eine ganze Zahl sein."
      );
    }
    const kandidat = _getKandidatByOrdungszahl(ordnungszahl);
    if (!kandidat) {
      throw new ManagedStimmzettelError(
        `Kandidat mit Ordnungszahl ${ordnungszahl} existiert nicht.`
      );
    }

    _internalAddVotesToKandidat(kandidat, votesToAdd);
  }

  function _getKandidatByOrdungszahl(ordnungszahl: number) {
    const wahlvorschlagOrdnungszahl = Math.floor(
      ordnungszahl / WAHLVORSCHLAG_NUMBER_MULTIPLER_FOR_ORDNUNGSZAHL
    );

    const wahlvorschlag = stimmzettel.wahlvorschlaege.find(
      (wahlvorschlag) =>
        wahlvorschlagOrdnungszahl === wahlvorschlag.ordnungszahl
    );
    if (!wahlvorschlag) {
      return undefined;
    }

    const kandidatenListenposition =
      ordnungszahl % WAHLVORSCHLAG_NUMBER_MULTIPLER_FOR_ORDNUNGSZAHL;
    return _getKandidatToAddVotesByUser(
      wahlvorschlag.kandidaten,
      kandidatenListenposition
    );
  }

  function _getKandidatToAddVotesByUser(
    wahlvorschlagKandidaten: Kandidat[],
    listenposition: number
  ) {
    //filter by listenposition
    const kandidatenForListenPosition = wahlvorschlagKandidaten.filter(
      (kandidat) => kandidat.listenposition === listenposition
    );
    if (kandidatenForListenPosition.length === 0) {
      return undefined;
    }

    //has any kandidat already uservotes?
    const kandidatWithUserVotes = kandidatenForListenPosition.find(
      (kandidat) => kandidat.einzelstimmen !== null
    );
    if (kandidatWithUserVotes) {
      return kandidatWithUserVotes;
    }

    //get first unused nennung
    const firstNennungWithoutDiscard = kandidatenForListenPosition.find(
      (kandidat) => !kandidat.durchgestrichen
    );
    if (firstNennungWithoutDiscard) {
      return firstNennungWithoutDiscard;
    }

    return kandidatenForListenPosition[0];
  }

  function _internalAddVotesToKandidat(
    kandidat: Kandidat,
    numberOfVotesToAdd: number
  ) {
    const currentVotesByVoter = kandidat.einzelstimmen ?? 0;
    kandidat.einzelstimmen = currentVotesByVoter + Math.abs(numberOfVotesToAdd);
    //TODO hier kann man jetzt die Historie triggern
  }

  return {
    kandidatAddVotesOrThrow,
    stimmzettel, //TODO for debugging only
  };
}
export type ManagedStimmzettel = ReturnType<typeof useManagedStimmzettel>;
