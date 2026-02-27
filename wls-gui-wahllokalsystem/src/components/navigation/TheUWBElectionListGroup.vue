<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <base-workflow-list-item
        v-bind="props"
        title="Wahlhandlung"
        :disabled="disabled"
        :subtitle="disabledMessage"
        :is-workflow-step-finished="isWahlhandlungErfasst"
        list-group-activator
      />
    </template>
    <base-workflow-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="disabled || !isWahlvorstandErfasst"
      :is-workflow-step-finished="isWahlumgebungErfasst"
    />
    <base-workflow-list-item
      title="Wählerverzeichnis"
      :to="routeWithName(ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS)"
      :disabled="disabled || !isWahlumgebungErfasst"
      :is-workflow-step-finished="isWaehlerverzeichnisErfasst"
    />
    <base-workflow-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="disabled || !isWaehlerverzeichnisErfasst"
      :is-workflow-step-finished="isWahleroeffnungErfasst"
    />
    <base-workflow-list-item
      title="Stimmabgabe"
      :to="routeWithName(ROUTE_STIMMABGABE)"
      :disabled="disabled || !isWahleroeffnungErfasst"
      :is-workflow-step-finished="isStimmabgabeErfasst"
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
} = storeToRefs(useWorkflowStore());
</script>
