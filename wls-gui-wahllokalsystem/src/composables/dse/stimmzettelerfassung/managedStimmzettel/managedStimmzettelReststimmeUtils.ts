import type { StimmenSummary } from "@/types/dse/stimmzettelerfassung/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";

export function useManagedStimmzettelReststimmeUtils(
  stimmzettel: Ref<Stimmzettel>,
  maximalErlaubteStimmenProWaehler: Ref<number>,
  COUNT_VOTES_GIVEN_BY_WAHLVORSCHLAG = 1
) {
  const logger = useLogging("mangeStimmzettelReststimmeUtils");

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
    }
    wahlvorschlag.selected = false;
  }

  function refreshWahlvorschlaegeVotes() {
    hasSystemErrorToManyListenKreuze.value = false;

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
