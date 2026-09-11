import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";

import { useNumberTools } from "@/composables/common/numberTools.ts";
import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";

const { zeroAsNull } = useNumberTools();
const {
  getEinzelstimmenOrZero,
  getTotalEinzelstimmenOfKandidatenWithSameId,
  getUngueltigeStimmenOrZero,
} = useKandidatTools();

export function useManagedStimmzettelEinzelstimmeUtils(
  maxEinzelstimmen: number
) {
  function addVotesToKandidat(kandidat: Kandidat, numberOfVotes: number) {
    if (kandidat.durchgestrichen) {
      kandidat.ungueltigeStimmen = zeroAsNull(
        getUngueltigeStimmenOrZero(kandidat) + numberOfVotes
      );
    } else {
      const sumEinzelstimmenAllKandidatenWithThatIdAlreadyGot =
        getTotalEinzelstimmenOfKandidatenWithSameId(kandidat);
      const votesToAdd = _getVotesAllowedToAdd(
        numberOfVotes,
        sumEinzelstimmenAllKandidatenWithThatIdAlreadyGot
      );

      kandidat.einzelstimmen = zeroAsNull(
        getEinzelstimmenOrZero(kandidat) + votesToAdd.einzelstimmen
      );
      kandidat.ungueltigeStimmen = zeroAsNull(
        getUngueltigeStimmenOrZero(kandidat) + votesToAdd.ungueltigeStimmen
      );
    }
  }

  function removeVotesFromKandidat(kandidat: Kandidat, numberOfVotes: number) {
    let numberOfVotesToRemove = numberOfVotes;
    const currentEinzelstimmen = kandidat.einzelstimmen ?? 0;
    const currentInvalidVotes = kandidat.ungueltigeStimmen ?? 0;

    if (currentInvalidVotes > 0) {
      if (numberOfVotesToRemove > currentInvalidVotes) {
        numberOfVotesToRemove -= currentInvalidVotes;
        kandidat.ungueltigeStimmen = null;
      } else {
        kandidat.ungueltigeStimmen = zeroAsNull(
          currentInvalidVotes - numberOfVotesToRemove
        );
        numberOfVotesToRemove = 0;
      }
    }

    const newValueEinzelstimmen = currentEinzelstimmen - numberOfVotesToRemove;
    kandidat.einzelstimmen =
      newValueEinzelstimmen > 0 ? newValueEinzelstimmen : null;
  }

  function _getVotesAllowedToAdd(
    numberOfVotesToAdd: number,
    numberOfEinzelstimmenKandidatenWithIdAlreadyGot: number
  ): {
    einzelstimmen: number;
    ungueltigeStimmen: number;
  } {
    const requestNewNumberOfEinzelstimmen =
      numberOfEinzelstimmenKandidatenWithIdAlreadyGot + numberOfVotesToAdd;
    if (requestNewNumberOfEinzelstimmen > maxEinzelstimmen) {
      return {
        einzelstimmen:
          maxEinzelstimmen - numberOfEinzelstimmenKandidatenWithIdAlreadyGot,
        ungueltigeStimmen: requestNewNumberOfEinzelstimmen - maxEinzelstimmen,
      };
    } else {
      return {
        einzelstimmen: numberOfVotesToAdd,
        ungueltigeStimmen: 0,
      };
    }
  }

  return {
    addVotesToKandidat,
    removeVotesFromKandidat,
  };
}
