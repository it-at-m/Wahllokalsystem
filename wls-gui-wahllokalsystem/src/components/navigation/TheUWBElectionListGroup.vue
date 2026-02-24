<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlhandlung"
        :lines="groupActivatorListItemLines"
      >
        <template
          v-if="disabled"
          #subtitle
        >
          {{ disabledMessage }}
        </template>
        <template
          v-if="disabled"
          #prepend
        >
          <v-icon
            :icon="disabled ? '$disabled' : ''"
            size="small"
          />
        </template>
      </v-list-item>
    </template>
    <v-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
      :disabled="disabled || !isWahlvorstandErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled ? '$disabled' : isWahlumgebungErfasst ? '$valid' : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
    <v-list-item
      title="Wählerverzeichnis"
      :to="routeWithName(ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS)"
      :disabled="disabled || !isWahlumgebungErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled || !isWahlumgebungErfasst
              ? '$disabled'
              : isWaehlerverzeichnisErfasst
                ? '$valid'
                : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
      :disabled="disabled || !isWaehlerverzeichnisErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled || !isWaehlerverzeichnisErfasst
              ? '$disabled'
              : isWahleroeffnungErfasst
                ? '$valid'
                : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
    <v-list-item
      title="Stimmabgabe"
      :to="routeWithName(ROUTE_STIMMABGABE)"
      :disabled="disabled || !isWahleroeffnungErfasst"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled || !isWahleroeffnungErfasst
              ? '$disabled'
              : isStimmabgabeErfasst
                ? '$valid'
                : '$edit'
          "
          size="small"
        />
      </template>
    </v-list-item>
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
