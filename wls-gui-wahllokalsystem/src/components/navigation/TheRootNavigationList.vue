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
      <the-b-w-b-election-list-group
        v-if="isBWB"
        :disabled="!isWahlvorstandErfasst"
        :disabled-message="
          isWahlvorstandErfasst ? '' : DISABLED_SUBTITLE_WAHLVORSTAND_REQUIRED
        "
      />
      <the-u-w-b-election-list-group
        v-if="isUWB"
        :disabled="!isWahlvorstandErfasst"
        :disabled-message="
          isWahlvorstandErfasst ? '' : DISABLED_SUBTITLE_WAHLVORSTAND_REQUIRED
        "
      />
      <v-list-item
        title="Ereignisse"
        :to="routeWithName(ROUTE_EREIGNISSE)"
      />
    </v-list-group>
    <the-scores-list-group />
  </v-list>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import TheBWBElectionListGroup from "@/components/navigation/TheBWBElectionListGroup.vue";
import TheScoresListGroup from "@/components/navigation/TheScoresListGroup.vue";
import TheUWBElectionListGroup from "@/components/navigation/TheUWBElectionListGroup.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  DISABLED_SUBTITLE_WAHLVORSTAND_REQUIRED,
  ROUTE_EREIGNISSE,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { routeWithName } = useNavigationUtils();
const { isUWB, isBWB } = storeToRefs(useUserStore());
const { isWahlvorstandErfasst } = storeToRefs(useWorkflowStore());
</script>
