<template>
  <v-list class="pt-0">
    <base-divider-list-item title="Allgemein" />
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
    <the-scores-list-items />
  </v-list>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseDividerListItem from "@/components/navigation/common/BaseDividerListItem.vue";
import TheBWBElectionListGroup from "@/components/navigation/TheBWBElectionListGroup.vue";
import TheScoresListItems from "@/components/navigation/TheScoresListItems.vue";
import TheUWBElectionListGroup from "@/components/navigation/TheUWBElectionListGroup.vue";
import {
  DISABLED_SUBTITLE_WAHLVORSTAND_REQUIRED,
  ROUTE_EREIGNISSE,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { useNavigationService } from "../../composables/navigation/navigationService.ts";

const { routeWithName } = useNavigationService();
const { isUWB, isBWB } = storeToRefs(useUserStore());
const { isWahlvorstandErfasst } = storeToRefs(useWorkflowStore());
</script>
