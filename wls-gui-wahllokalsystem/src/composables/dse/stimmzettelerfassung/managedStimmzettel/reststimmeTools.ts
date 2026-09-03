import type { StimmenSummary } from "@/types/dse/stimmzettelerfassung/StimmenSummary.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useKopfdatenStore } from "@/stores/kopfdatenStore.ts";

export function useReststimmeTools(
  wahlID: string,
  stimmenSummary: Ref<StimmenSummary>,
  stimmzettel: Ref<Stimmzettel>
) {
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

  function selectWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
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
  }

  function deselectWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    wahlvorschlag.kandidaten.map((kandidat) => (kandidat.reststimmen = 0));
    wahlvorschlag.selected = false;
  }

  function updateReststimmenWhenVotesAdded() {
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

  function updateReststimmenWhenVotesRemoved() {
    if (remainingVotes.value > 0) {
      const wahlvorschlagToUpdate = stimmzettel.value.wahlvorschlaege.find(
        (wahlvorschlag) => wahlvorschlag.selected
      );
      if (wahlvorschlagToUpdate) {
        for (let i = remainingVotes.value; i > 0; i--) {
          const kandidatToUpdate = wahlvorschlagToUpdate.kandidaten.find(
            (kandidat) =>
              !kandidat.durchgestrichen &&
              !kandidat.einzelstimmen &&
              !kandidat.ungueltigeStimmen &&
              !kandidat.reststimmen
          );
          if (kandidatToUpdate) {
            kandidatToUpdate.reststimmen = 1;
          }
        }
      }
    }
  }

  return {
    selectWahlvorschlag,
    deselectWahlvorschlag,
    updateReststimmenWhenVotesAdded,
    updateReststimmenWhenVotesRemoved,
  };
}
