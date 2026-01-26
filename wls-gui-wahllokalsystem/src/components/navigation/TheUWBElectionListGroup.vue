<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlhandlung"
        :disabled="isGroupDisabled"
        :lines="groupActivatorListItemLines"
      >
        <template
          v-if="disabledMessage"
          #subtitle
        >
          {{ disabledMessage }}
        </template>
      </v-list-item>
    </template>
    <v-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="isGroupDisabled"
    />
    <v-list-item
      title="Wählerverzeichnis"
      :to="routeWithName(ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS)"
      :disabled="isGroupDisabled || !isWahlumgebungErfasst"
    />
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="isGroupDisabled"
    />
    <v-list-item
      title="Stimmabgabe"
      :to="routeWithName(ROUTE_STIMMABGABE)"
      :disabled="isGroupDisabled"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_STIMMABGABE,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
} from "@/constants.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { disabled } = defineProps({
  disabled: {
    type: [Boolean, String],
    default: false,
  },
});

const { routeWithName } = useNavigationUtils();
const { isWahlumgebungErfasst } = storeToRefs(useWorkflowStore());

const isGroupDisabled = computed(() => !!disabled);

const disabledMessage = computed(() => {
  return typeof disabled === "string" ? disabled : null;
});

const groupActivatorListItemLines = computed(() =>
  disabledMessage.value ? false : "one"
);
</script>
