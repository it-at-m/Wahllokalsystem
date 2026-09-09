import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { computed, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useStringNumberMapTools } from "@/composables/common/stringNumberMapTools.ts";
import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";

export function useManagedStimmzettelReststimmeUtils(
  stimmzettel: Ref<Stimmzettel>,
  maximalErlaubteStimmenProWaehler: Ref<number>,
  maxEinzelstimmen: number,
  countVotesGivenAsReststimme = 1
) {
  const logger = useLogging("mangeStimmzettelReststimmeUtils");
  const { hasAnyKennzeichenOrReststimme } = useKandidatTools();

  const hasSystemErrorToManyListenKreuze = ref(false);

  const selectedWahlvorschlaege = computed(() =>
    stimmzettel.value.wahlvorschlaege.filter(
      (wahlvorschlag) => wahlvorschlag.selected
    )
  );

  function selectWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    wahlvorschlag.selected = true;
  }

  function deselectWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    if (wahlvorschlag.selected) {
      wahlvorschlag.kandidaten.forEach(
        (kandidat) => (kandidat.reststimmen = null)
      );
      wahlvorschlag.selected = false;
    }
  }

  function refreshWahlvorschlaegeVotes() {
    hasSystemErrorToManyListenKreuze.value = false;

    const votesKandidatenAlreadyGotTool = useStringNumberMapTools(
      new Map<string, number>()
    );
    const kandidatenOfSelectedWahlvorschlaege =
      stimmzettel.value.wahlvorschlaege.flatMap(
        (wahlvorschlag) => wahlvorschlag.kandidaten
      );
    //count votes that any kandidat of wahlvorschlag already got
    //we need to sum the votes of the nennungen cause sum of votes over all nennungen is limited
    kandidatenOfSelectedWahlvorschlaege.forEach((kandidat) =>
      votesKandidatenAlreadyGotTool.add(
        kandidat.kandidatId,
        (kandidat.ungueltigeStimmen ?? 0) + (kandidat.einzelstimmen ?? 0)
      )
    );

    const countRequiredVotesLeftToFulfilReststimmenvergabe =
      _getCountRequiredVotesForReststimmenvergabe(
        votesKandidatenAlreadyGotTool
      );
    const totalVotesAlreadyGiven =
      votesKandidatenAlreadyGotTool.sum() +
      (stimmzettel.value.invalideVotes ?? 0);
    const totalVotesLeft =
      maximalErlaubteStimmenProWaehler.value - totalVotesAlreadyGiven;
    logger.logDebug(
      `totalVotesAlreadyGiven > ${totalVotesAlreadyGiven}, totalVotesLeft > ${totalVotesLeft}, countRequiredVotesLeftToFulfilReststimmenvergabe > ${countRequiredVotesLeftToFulfilReststimmenvergabe}`
    );

    if (countRequiredVotesLeftToFulfilReststimmenvergabe === null) {
      selectedWahlvorschlaege.value.forEach((wahlvorschlag) =>
        _placeReststimmenOnWahlvorschlag(
          wahlvorschlag,
          totalVotesLeft,
          votesKandidatenAlreadyGotTool
        )
      );
    } else if (
      countRequiredVotesLeftToFulfilReststimmenvergabe <= totalVotesLeft
    ) {
      //Number.POSITIVE_INFINITY because with the condition we already ensured that are enough votes left
      selectedWahlvorschlaege.value.forEach((wahlvorschlag) =>
        _placeReststimmenOnWahlvorschlag(
          wahlvorschlag,
          Number.POSITIVE_INFINITY,
          votesKandidatenAlreadyGotTool
        )
      );
    } else {
      kandidatenOfSelectedWahlvorschlaege.forEach(
        (kandidat) => (kandidat.reststimmen = null)
      );
      hasSystemErrorToManyListenKreuze.value = true;
    }
  }

  function _getCountRequiredVotesForReststimmenvergabe(
    votesKandidatenAlreadyGotTool: ReturnType<typeof useStringNumberMapTools>
  ): number | null {
    if (selectedWahlvorschlaege.value.length > 1) {
      const kandidatenThatCouldGetWahlvorschlagVote =
        selectedWahlvorschlaege.value
          .flatMap((wahlvorschlag) => wahlvorschlag.kandidaten)
          .filter(
            (kandidat) =>
              !kandidat.durchgestrichen &&
              (kandidat.einzelstimmen ?? 0) === 0 &&
              (kandidat.ungueltigeStimmen ?? 0) === 0 &&
              votesKandidatenAlreadyGotTool.getOrDefault(kandidat.kandidatId) <
                maxEinzelstimmen
          );

      return (
        kandidatenThatCouldGetWahlvorschlagVote.length *
        countVotesGivenAsReststimme
      );
    } else {
      return null;
    }
  }

  function _placeReststimmenOnWahlvorschlag(
    wahlvorschlag: Wahlvorschlag,
    votesLeftToPlace: number,
    votesKandidatenAlreadyGotTool: ReturnType<typeof useStringNumberMapTools>
  ) {
    logger.logDebug(
      `placing reststimmen on ${wahlvorschlag.kurzname}, votesLeftToPlace > ${votesLeftToPlace}`
    );
    let restStimmenSpent = 0;
    wahlvorschlag.kandidaten.forEach((kandidat) => {
      //is kandidat allowed to get reststimmen
      if (
        !hasAnyKennzeichenOrReststimme(kandidat) &&
        restStimmenSpent + countVotesGivenAsReststimme <= votesLeftToPlace &&
        votesKandidatenAlreadyGotTool.getOrDefault(kandidat.kandidatId) +
          countVotesGivenAsReststimme <=
          maxEinzelstimmen
      ) {
        kandidat.reststimmen = countVotesGivenAsReststimme;

        votesKandidatenAlreadyGotTool.add(
          kandidat.kandidatId,
          countVotesGivenAsReststimme
        );
        restStimmenSpent += countVotesGivenAsReststimme;
      } else {
        kandidat.reststimmen = null;
      }
    });
  }

  function resetError() {
    hasSystemErrorToManyListenKreuze.value = false;
  }

  return {
    hasSystemErrorToManyListenKreuze,
    refreshWahlvorschlaegeVotes,
    resetError,
    selectWahlvorschlag,
    deselectWahlvorschlag,
  };
}
