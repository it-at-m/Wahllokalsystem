import type { Ref } from "vue";

import { defineStore } from "pinia";
import { ref, watch } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "experimentalFeaturesStore";

interface StimmzettelSummary {
  index: number;
  countUserVotes: number;
  countUserVotesInvalid: number;
  countListenkreuze: number;
  countUserDiscards: number;
  isValid: number;
  isBeschlussGefasst: boolean;
}

export const useExperimentalFeaturesStore = defineStore(storeID, () => {
  const stimmzettelSummaryItems: Ref<StimmzettelSummary[]> = ref([]);
  const hasStimmzettelSummaryItems = ref(false);

  const subViewBeschlussfassung = ref("overview");
  const subViewStimmzettelerfassung = ref("1");

  const beschlussFortschrittMax = ref(20);
  const beschlussFortschrittCurrent = ref(7);
  const beschlussGueltigkeit1IsSelectable = ref(true);
  const beschlussGueltigkeit2IsSelectable = ref(true);
  const beschlussGueltigkeit3IsSelectable = ref(true);
  const beschlussStimmzettelFailureListenkreuzen = ref(false);
  const beschlussStimmzettelFailureZuVieleStimmen = ref(false);

  const kandidatScoreShowName = ref(true);
  const compactSimpleWahlvorschlag = ref(false);

  watch(hasStimmzettelSummaryItems, (newValue) => {
    if (newValue) {
      stimmzettelSummaryItems.value = _createStimmzettelSummaryItems(150);
    } else {
      stimmzettelSummaryItems.value = [];
    }
  });

  function _createStimmzettelSummaryItems(count = 20): StimmzettelSummary[] {
    const result: StimmzettelSummary[] = [];
    for (let i = 1; i <= count; i++) {
      result.push({
        index: i,
        countUserVotes: Math.floor(Math.random() * 80),
        countUserVotesInvalid: Math.floor(Math.random() * 10),
        countListenkreuze: Math.floor(Math.random() * 2),
        countUserDiscards: Math.floor(Math.random() * 4),
        isValid: Math.floor(Math.random() * 3),
        isBeschlussGefasst: Math.floor(Math.random() * 2) % 2 === 0,
      });
    }
    return result;
  }

  return {
    beschlussFortschrittMax,
    beschlussFortschrittCurrent,
    beschlussGueltigkeit1IsSelectable,
    beschlussGueltigkeit2IsSelectable,
    beschlussGueltigkeit3IsSelectable,
    beschlussStimmzettelFailureListenkreuzen,
    beschlussStimmzettelFailureZuVieleStimmen,
    kandidatScoreShowName,
    compactSimpleWahlvorschlag,
    stimmzettelSummaryItems,
    hasStimmzettelSummaryItems,
    subViewBeschlussfassung,
    subViewStimmzettelerfassung,
  };
});

registerStoreHMR(useExperimentalFeaturesStore);
