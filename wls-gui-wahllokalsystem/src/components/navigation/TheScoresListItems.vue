<template>
  <div>
    <base-divider-list-item title="Ergebnisermittlung" />
    <base-workflow-list-item
      v-if="isBWB"
      title="Wahlscheine"
      :disabled="isWahlscheineDisabled && !areAllElectionsFinished"
      :subtitle="areAllElectionsFinished ? '' : subtitleWahlscheine"
      :to="routeWithName(ROUTE_WAHLSCHEINE)"
      :is-workflow-step-finished="
        isAnzahlWahlscheineErfasst || areAllElectionsFinished
      "
    />
    <base-workflow-list-item
      v-if="isUWB"
      title="Stimmabgabevermerke"
      :disabled="isStimmabgabevermerkeDisabled && !areAllElectionsFinished"
      :subtitle="areAllElectionsFinished ? '' : subtitleStimmabgabevermerke"
      :to="routeWithName(ROUTE_STIMMABGABEVERMERKE)"
      :is-workflow-step-finished="
        isStimmabgabevermerkeErfasst || areAllElectionsFinished
      "
    />
    <the-scores-list-group-selector
      v-for="wahl in wahlenState.wahlen"
      :key="wahl.wahlID"
      :wahl="wahl"
    />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseDividerListItem from "@/components/navigation/common/BaseDividerListItem.vue";
import BaseWorkflowListItem from "@/components/navigation/common/BaseWorkflowListItem.vue";
import TheScoresListGroupSelector from "@/components/navigation/TheScoresListGroupSelector.vue";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import {
  DISABLED_SUBTITLE_WAHLBRIEFZULASSUNG_MISSING,
  DISABLED_SUBTITLE_WAHLHANDLUNG_MISSING,
  DISABLED_SUBTITLE_WAHLVORSTAND_MISSING,
  ROUTE_STIMMABGABEVERMERKE,
  ROUTE_WAHLSCHEINE,
  SUBTITLE_WAEHLERANZAHL_ERFASST,
  SUBTITLE_WAEHLERANZAHL_IN_ARBEIT,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { routeWithName } = useNavigationService();

const { isBWB, isUWB } = storeToRefs(useUserStore());
const { wahlenState } = storeToRefs(useWahlenStore());
const {
  isWahlvorstandErfasst,
  isWahlbriefzulassungErfasst,
  isWahlhandlungErfasst,
  isStimmabgabevermerkeErfasst,
  isAnzahlWahlscheineErfasst,
  areAllElectionsFinished,
} = storeToRefs(useWorkflowStore());

const isStimmabgabevermerkeDisabled = computed(
  () => !isWahlvorstandErfasst.value || !isWahlhandlungErfasst.value
);
const isWahlscheineDisabled = computed(
  () => !isWahlvorstandErfasst.value || !isWahlbriefzulassungErfasst.value
);

const subtitleWahlscheine = computed(() => {
  if (!isWahlvorstandErfasst.value) {
    return DISABLED_SUBTITLE_WAHLVORSTAND_MISSING;
  }
  if (!isWahlbriefzulassungErfasst.value) {
    return DISABLED_SUBTITLE_WAHLBRIEFZULASSUNG_MISSING;
  }
  if (!isAnzahlWahlscheineErfasst.value) {
    return SUBTITLE_WAEHLERANZAHL_IN_ARBEIT;
  }
  return SUBTITLE_WAEHLERANZAHL_ERFASST;
});

const subtitleStimmabgabevermerke = computed(() => {
  if (!isWahlvorstandErfasst.value) {
    return DISABLED_SUBTITLE_WAHLVORSTAND_MISSING;
  }
  if (!isWahlhandlungErfasst.value) {
    return DISABLED_SUBTITLE_WAHLHANDLUNG_MISSING;
  }
  if (!isStimmabgabevermerkeErfasst.value) {
    return SUBTITLE_WAEHLERANZAHL_IN_ARBEIT;
  }
  return SUBTITLE_WAEHLERANZAHL_ERFASST;
});
</script>
