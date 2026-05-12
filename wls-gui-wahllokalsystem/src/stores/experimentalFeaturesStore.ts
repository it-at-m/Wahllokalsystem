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
    stimmzettelSummaryItems,
    hasStimmzettelSummaryItems,
    subViewBeschlussfassung,
    subViewStimmzettelerfassung,
  };
});

registerStoreHMR(useExperimentalFeaturesStore);
