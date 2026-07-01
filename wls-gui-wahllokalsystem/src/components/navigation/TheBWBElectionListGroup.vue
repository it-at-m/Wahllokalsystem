<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <base-workflow-list-item
        v-bind="props"
        title="Wahlbriefzulassung"
        :disabled="disabled && !areAllElectionsFinished"
        :subtitle="areAllElectionsFinished ? '' : disabledMessage"
        :is-workflow-step-finished="
          isWahlbriefzulassungErfasst || areAllElectionsFinished
        "
        list-group-activator
      />
    </template>
    <base-workflow-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="
        (disabled || !isWahlvorstandErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isWahleroeffnungErfasst || areAllElectionsFinished
      "
    />
    <base-workflow-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="
        (disabled || !isWahleroeffnungErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isWahlumgebungErfasst || areAllElectionsFinished
      "
    />
    <base-workflow-list-item
      title="Wahlbriefe erfassen"
      :to="routeWithName(ROUTE_ERFASSUNG_WAHLBRIEFE)"
      :disabled="
        (disabled || !isWahlumgebungErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isWahlbriefeErfassenErfasst || areAllElectionsFinished
      "
    />
    <base-workflow-list-item
      title="Wahlbriefe zulassen"
      :to="routeWithName(ROUTE_WAHLBRIEFE_ZULASSEN)"
      :disabled="
        (disabled || !isWahlbriefeErfassenErfasst) && !areAllElectionsFinished
      "
      :is-workflow-step-finished="
        isWahlbriefeZulassenErfasst || areAllElectionsFinished
      "
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseWorkflowListItem from "@/components/navigation/common/BaseWorkflowListItem.vue";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLUMGEBUNG,
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

const { routeWithName } = useNavigationService();
const {
  areAllElectionsFinished,
  isWahlumgebungErfasst,
  isWahleroeffnungErfasst,
  isWahlbriefeErfassenErfasst,
  isWahlbriefeZulassenErfasst,
  isWahlvorstandErfasst,
  isWahlbriefzulassungErfasst,
} = storeToRefs(useWorkflowStore());
</script>
