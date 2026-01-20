<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlhandlung"
        :disabled="disabled"
      />
    </template>
    <v-list-item
      title="Wahlumgebung"
      :to="routeWithName(ROUTE_WAHLUMGEBUNG)"
    />
    <v-list-item
      title="Wählerverzeichnis"
      :to="routeWithName(ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS)"
      :disabled="!isWahlumgebungErfasst"
    />
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="routeWithName(ROUTE_BEGINN_STIMMABGABE)"
    />
    <v-list-item
      title="Stimmabgabe"
      :to="routeWithName(ROUTE_STIMMABGABE)"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_STIMMABGABE,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
} from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";

defineProps({
  disabled: {
    type: Boolean,
    default: false,
  },
});

const { routeWithName } = useNavigationUtils();
const { isWahlumgebungErfasst } = storeToRefs(useStatusStore());
</script>
