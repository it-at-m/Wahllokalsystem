<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <base-workflow-list-item
        v-bind="props"
        title="Wahlhandlung"
        :disabled="disabled && !areAllElectionsFinished"
        :subtitle="areAllElectionsFinished ? '' : disabledMessage"
        :is-workflow-step-finished="
          isWahlhandlungErfasst || areAllElectionsFinished
        "
        list-group-activator
      />
    </template>
    <base-workflow-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="
        (disabled || !isWahlvorstandErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isWahlumgebungErfasst || areAllElectionsFinished
      "
    />
    <base-workflow-list-item
      title="Wählerverzeichnis"
      :to="routeWithName(ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS)"
      :disabled="
        (disabled || !isWahlumgebungErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isWaehlerverzeichnisErfasst || areAllElectionsFinished
      "
    />
    <base-workflow-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="
        (disabled || !isWaehlerverzeichnisErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isWahleroeffnungErfasst || areAllElectionsFinished
      "
    />
    <base-workflow-list-item
      title="Stimmabgabe"
      :to="routeWithName(ROUTE_STIMMABGABE)"
      :disabled="
        (disabled || !isWahleroeffnungErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isStimmabgabeErfasst || areAllElectionsFinished
      "
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseWorkflowListItem from "@/components/navigation/common/BaseWorkflowListItem.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_STIMMABGABE,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
} from "@/constants.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

defineProps({
  disabled: {
    type: Boolean,
    default: false,
  },
  disabledMessage: {
    type: String,
    default: "",
  },
});

const { routeWithName } = useNavigationUtils();
const {
  isWahlvorstandErfasst,
  isWahlumgebungErfasst,
  isWaehlerverzeichnisErfasst,
  isWahleroeffnungErfasst,
  isStimmabgabeErfasst,
  isWahlhandlungErfasst,
  areAllElectionsFinished,
} = storeToRefs(useWorkflowStore());
</script>
