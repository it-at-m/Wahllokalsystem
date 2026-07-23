import type { Ref } from "vue";

import { defineStore } from "pinia";
import { ref, watch } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";

const { registerStoreHMR } = useHmrUpdate();

const storeID = "experimentalFeaturesStore";

interface StimmzettelSummary {
  index: number;
  isValid: number;
  beschlussgrund: string;
}

export const useExperimentalFeaturesStore = defineStore(storeID, () => {
  const stimmzettelSummaryItems: Ref<StimmzettelSummary[]> = ref([]);
  const hasStimmzettelSummaryItems = ref(false);

  const subViewBeschlussfassung = ref("overview");
  const subViewStimmzettelerfassung = ref("4");

  const beschlussFortschrittMax = ref(20);
  const beschlussFortschrittCurrent = ref(7);
  const beschlussGueltigkeit1IsSelectable = ref(true);
  const beschlussGueltigkeit2IsSelectable = ref(true);
  const beschlussGueltigkeit3IsSelectable = ref(true);
  const beschlussStimmzettelFailureListenkreuzen = ref(false);
  const beschlussStimmzettelFailureZuVieleStimmen = ref(false);

  const kandidatScoreShowName = ref(true);
  const compactSimpleWahlvorschlag = ref(false);
  const spacesBetweenNonDirectFollowingKandidatenVotes = ref(true);
  const useKandidatScoreComponentInSimpleErfassung = ref(false);

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
      const valid = Math.floor(Math.random() * 4);
      result.push({
        index: i,
        isValid: valid,
        beschlussgrund:
          valid === 3
            ? "Wählerwille nicht zweifelsfrei erkennbar, Kennzeichnung nicht eindeutig zuzuordnen"
            : "",
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
    spacesBetweenNonDirectFollowingKandidatenVotes,
    useKandidatScoreComponentInSimpleErfassung,
    stimmzettelSummaryItems,
    hasStimmzettelSummaryItems,
    subViewBeschlussfassung,
    subViewStimmzettelerfassung,
  };
});

registerStoreHMR(useExperimentalFeaturesStore);
