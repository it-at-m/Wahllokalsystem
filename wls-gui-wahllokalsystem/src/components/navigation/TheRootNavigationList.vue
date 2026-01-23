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
        :disabled="commonActionsDisabledMessageOrFalse"
      />
      <the-u-w-b-election-list-group
        v-if="isUWB"
        :disabled="commonActionsDisabledMessageOrFalse"
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
import { computed } from "vue";

import TheBWBElectionListGroup from "@/components/navigation/TheBWBElectionListGroup.vue";
import TheScoresListGroup from "@/components/navigation/TheScoresListGroup.vue";
import TheUWBElectionListGroup from "@/components/navigation/TheUWBElectionListGroup.vue";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_EREIGNISSE,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { routeWithName } = useNavigationUtils();
const { isUWB, isBWB } = storeToRefs(useUserStore());
const { isWahlvorstandErfasst } = storeToRefs(useStatusStore());

const commonActionsDisabledMessageOrFalse = computed(() =>
  !isWahlvorstandErfasst.value ? "Erst Anwesenheit erfassen" : false
);
</script>
