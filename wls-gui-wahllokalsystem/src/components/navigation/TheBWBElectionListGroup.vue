<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlbriefzulassung"
        :lines="groupActivatorListItemLines"
      >
        <template
          v-if="disabled"
          #subtitle
        >
          {{ disabledMessage }}
        </template>
      </v-list-item>
    </template>
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="disabled || !isWahlvorstandErfasst"
    />
    <v-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="disabled || !isWahleroeffnungErfasst"
    />
    <v-list-item
      title="Wahlbriefe erfassen"
      :to="routeWithName(ROUTE_ERFASSUNG_WAHLBRIEFE)"
      :disabled="disabled || !isWahlumgebungErfasst"
    />
    <v-list-item
      title="Wahlbriefe zulassen"
      :to="routeWithName(ROUTE_WAHLBRIEFE_ZULASSEN)"
      :disabled="disabled || !isWahlbriefeErfassenErfasst"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLUMGEBUNG,
} from "@/constants.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const properties = defineProps({
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
  isWahlumgebungErfasst,
  isWahleroeffnungErfasst,
  isWahlbriefeErfassenErfasst,
  isWahlvorstandErfasst,
} = storeToRefs(useWorkflowStore());

const groupActivatorListItemLines = computed(() =>
  properties.disabledMessage && properties.disabled ? false : "one"
);
</script>
