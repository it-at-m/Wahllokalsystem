<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        :title="disabled ? '⛔ Wahlhandlung' : 'Wahlhandlung'"
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
      :title="
        disabled
          ? 'Wahlumgebung'
          : isWahlumgebungErfasst
            ? '✅ Wahlumgebung'
            : '✏️ Wahlumgebung'
      "
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="disabled || !isWahlvorstandErfasst"
    />
    <v-list-item
      :title="
        disabled || !isWahlumgebungErfasst
          ? 'Wählerverzeichnis'
          : isWaehlerverzeichnisErfasst
            ? '✅ Wählerverzeichnis'
            : '✏️ Wählerverzeichnis'
      "
      :to="routeWithName(ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS)"
      :disabled="disabled || !isWahlumgebungErfasst"
    />
    <v-list-item
      :title="
        disabled || !isWaehlerverzeichnisErfasst
          ? 'Beginn Stimmabgabe'
          : isWahleroeffnungErfasst
            ? '✅ Beginn Stimmabgabe'
            : '✏️ Beginn Stimmabgabe'
      "
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="disabled || !isWaehlerverzeichnisErfasst"
    />
    <v-list-item
      :title="
        disabled || !isWahleroeffnungErfasst
          ? 'Stimmabgabe'
          : isStimmabgabeErfasst
            ? '✅ Stimmabgabe'
            : '✏️ Stimmabgabe'
      "
      :to="routeWithName(ROUTE_STIMMABGABE)"
      :disabled="disabled || !isWahleroeffnungErfasst"
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
  isWahlvorstandErfasst,
  isWahlumgebungErfasst,
  isWaehlerverzeichnisErfasst,
  isWahleroeffnungErfasst,
  isStimmabgabeErfasst,
} = storeToRefs(useWorkflowStore());

const groupActivatorListItemLines = computed(() =>
  properties.disabledMessage && properties.disabled ? false : "one"
);
</script>
