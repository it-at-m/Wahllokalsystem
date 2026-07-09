<template>
  <div v-if="wahlbezirkIdForWahl">
    <the-o-b-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Obw"
      :title-stimmen-zaehlen="titleStimmenZaehlen"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
    />
    <the-s-r-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Srw"
      :title-stimmen-zaehlen="titleStimmenZaehlen"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
    />
    <the-b-a-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Baw"
      :title-stimmen-zaehlen="titleStimmenZaehlen"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
    />
    <the-m-b-w-scores-list-group
      v-if="wahl.wahlart === WahlWahlartEnum.Mbw"
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
      :disabled="isMBWAuszaehlungDisabled"
      :disabled-message="disabledMessagePreviousStepsRequired"
      :is-wahl-finished="isMBWAuszaehlungFinished"
    />
    <the-dse-list-items
      :wahl-id="wahl.wahlID"
      :wahlbezirk-id="wahlbezirkIdForWahl"
      :disabled="false"
      disabled-message=""
      :is-wahl-finished="false"
    />
  </div>
</template>

<script setup lang="ts">
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import TheBAWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheBAWScoresListGroup.vue";
import TheMBWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheMBWScoresListGroup.vue";
import TheOBWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheOBWScoresListGroup.vue";
import TheSRWScoresListGroup from "@/components/navigation/auszaehlung_wahlarten/TheSRWScoresListGroup.vue";
import TheDseListItems from "@/components/navigation/dse/TheDSEListItems.vue";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import {
  DISABLED_SUBTITLE_STIMMABGABEVERMERKE_MISSING,
  DISABLED_SUBTITLE_WAHLSCHEINE_MISSING,
  DISABLED_SUBTITLE_WAHLVORSTAND_MISSING,
  SUBTITLE_AUSZAEHLUNG_ERFASST,
  SUBTITLE_AUSZAEHLUNG_IN_ARBEIT,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const props = defineProps({
  wahl: {
    type: Object as PropType<Wahl>,
    required: true,
  },
});

const { getStimmzettelTermForWahl } = useTextFormatter();
const { isElectionFinished } = useWorkflowStore();
const {
  isAnzahlWahlscheineErfasst,
  isStimmabgabevermerkeErfasst,
  isWahlvorstandErfasst,
} = storeToRefs(useWorkflowStore());
const { isBWB, isUWB } = storeToRefs(useUserStore());

const titleStimmenZaehlen = computed(
  () => `Zählen der ${getStimmzettelTermForWahl(props.wahl)}`
);

const wahlbezirkIdForWahl = computed(() =>
  useUserStore().getWahlbezirkIdFromWahlMetaDataByWahlId(props.wahl.wahlID)
);

const isMBWAuszaehlungDisabled = computed(() => {
  if (isUWB.value) {
    return !isWahlvorstandErfasst.value || !isStimmabgabevermerkeErfasst.value;
  } else {
    return !isWahlvorstandErfasst.value || !isAnzahlWahlscheineErfasst.value;
  }
});

const isMBWAuszaehlungFinished = computed(() =>
  isElectionFinished(props.wahl.wahlID, wahlbezirkIdForWahl.value ?? "")
);

const disabledMessagePreviousStepsRequired = computed(() => {
  if (!isWahlvorstandErfasst.value) {
    return DISABLED_SUBTITLE_WAHLVORSTAND_MISSING;
  }
  if (isBWB.value && !isAnzahlWahlscheineErfasst.value) {
    return DISABLED_SUBTITLE_WAHLSCHEINE_MISSING;
  }
  if (isUWB.value && !isStimmabgabevermerkeErfasst.value) {
    return DISABLED_SUBTITLE_STIMMABGABEVERMERKE_MISSING;
  }
  if (!isMBWAuszaehlungFinished.value) {
    return SUBTITLE_AUSZAEHLUNG_IN_ARBEIT;
  }
  return SUBTITLE_AUSZAEHLUNG_ERFASST;
});
</script>
