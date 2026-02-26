<template>
  <v-list-group value="Ergebnisse">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Ergebnisermittlung"
      />
    </template>
    <v-list-item
      v-if="isBWB"
      title="Wahlscheine"
      :disabled="isWahlscheineDisabled"
      :to="routeWithName(ROUTE_WAHLSCHEINE)"
      :lines="groupActivatorListItemLines"
    >
      <template
        v-if="isWahlscheineDisabled"
        #subtitle
      >
        {{ disabledMessagePreviousStepsRequired }}
      </template>
    </v-list-item>
    <v-list-item
      v-if="isUWB"
      title="Stimmabgabevermerke"
      :disabled="isStimmabgabevermerkeDisabled"
      :to="routeWithName(ROUTE_STIMMABGABEVERMERKE)"
      :lines="groupActivatorListItemLines"
    >
      <template
        v-if="isStimmabgabevermerkeDisabled"
        #subtitle
      >
        {{ disabledMessagePreviousStepsRequired }}
      </template>
    </v-list-item>
    <the-scores-list-group-selector
      v-for="wahl in wahlenState.wahlen"
      :key="wahl.wahlID"
      :wahl="wahl"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import TheScoresListGroupSelector from "@/components/navigation/TheScoresListGroupSelector.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_STIMMABGABEVERMERKE, ROUTE_WAHLSCHEINE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { routeWithName } = useNavigationUtils();

const { isBWB, isUWB } = storeToRefs(useUserStore());
const { wahlenState } = storeToRefs(useWahlenStore());
const {
  isWahlvorstandErfasst,
  isWahlbriefzulassungErfasst,
  isWahlhandlungErfasst,
} = storeToRefs(useWorkflowStore());

const DISABLED_SUBTITLE_WAHLVORSTAND_MISSING =
  "Nicht genügend Mitglieder anwesend.";
const DISABLED_SUBTITLE_WAHLHANDLUNG_MISSING =
  "Wahlhandlung muss abgeschlossen sein.";
const DISABLED_SUBTITLE_WAHLBRIEFZULASSUNG_MISSING =
  "Wahlbriefzulassung muss abgeschlossen sein.";

const isStimmabgabevermerkeDisabled = computed(
  () => !isWahlvorstandErfasst.value || !isWahlhandlungErfasst.value
);
const isWahlscheineDisabled = computed(
  () => !isWahlvorstandErfasst.value || !isWahlbriefzulassungErfasst.value
);

const isWahlscheineOrStimmabgabevermerkeDisabled = computed(() =>
  isUWB.value
    ? isStimmabgabevermerkeDisabled.value
    : isWahlscheineDisabled.value
);

const disabledMessagePreviousStepsRequired = computed(() => {
  if (!isWahlvorstandErfasst.value) {
    return DISABLED_SUBTITLE_WAHLVORSTAND_MISSING;
  } else if (isUWB.value && !isWahlhandlungErfasst.value) {
    return DISABLED_SUBTITLE_WAHLHANDLUNG_MISSING;
  } else if (isBWB.value && !isWahlbriefzulassungErfasst.value) {
    return DISABLED_SUBTITLE_WAHLBRIEFZULASSUNG_MISSING;
  } else {
    return "";
  }
});

const groupActivatorListItemLines = computed(() =>
  isWahlscheineOrStimmabgabevermerkeDisabled.value ? false : "one"
);
</script>
