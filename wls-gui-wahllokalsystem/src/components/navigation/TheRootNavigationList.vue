<template>
  <v-list class="pt-0">
    <v-list-group
      value="Allgemein"
      class="bg-primary"
    >
      <template #activator="{ props }">
        <v-list-item
          v-bind="props"
          title="Allgemein"
        />
      </template>
      <v-list-item
        title="Home"
        :to="routeWithName(ROUTES_HOME)"
      />
      <v-list-item
        title="Wahlvorstand"
        :to="routeWithName(ROUTE_WAHLVORSTAND)"
      />
      <the-b-w-b-election-list-group v-if="isBWB" />
      <the-u-w-b-election-list-group v-if="isUWB" />
      <v-list-item
        title="Ereignisse"
        :to="routeWithName(ROUTE_EREIGNISSE)"
      />
    </v-list-group>
    <the-kommunalwahlen-scores-list-group />
  </v-list>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheBWBElectionListGroup from "@/components/navigation/TheBWBElectionListGroup.vue";
import TheKommunalwahlenScoresListGroup from "@/components/navigation/TheKommunalwahlenScoresListGroup.vue";
import TheUWBElectionListGroup from "@/components/navigation/TheUWBElectionListGroup.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_EREIGNISSE,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { routeWithName } = useNavigationUtils();
const { isUWB, isBWB } = storeToRefs(useUserStore());
</script>
