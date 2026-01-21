<template>
  <v-list-group value="MBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Migrationsbeirats"
      />
    </template>
    <v-list-item
      v-for="(route, index) in navigation"
      :key="index"
      :title="route.title"
      :to="
        routeWithNameAndParams(route.targetRouteName, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
        })
      "
      :disabled="route.disabled"
      :prepend-icon="getPrependIcon(route)"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import type { NavigationDefinition } from "@/plugins/router/mwbRoutes.ts";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  useMbwRoutes,
  WorflowStepStateEnum,
} from "@/plugins/router/mwbRoutes.ts";

const { routeWithNameAndParams } = useNavigationUtils();

const properties = defineProps<{
  titleStimmenZaehlen: string;
  wahlId: string;
  wahlbezirkId: string;
}>();

const { navigation } = useMbwRoutes(properties.wahlId, properties.wahlbezirkId);

function getPrependIcon(route: NavigationDefinition) {
  return route.disabled
    ? "$error"
    : route.nonDisabledState === WorflowStepStateEnum.IN_PROGRESS
      ? "$valid"
      : "$information";
}
</script>
