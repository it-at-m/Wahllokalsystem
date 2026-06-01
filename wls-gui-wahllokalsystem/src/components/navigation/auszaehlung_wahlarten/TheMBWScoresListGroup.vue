<template>
  <v-list-group value="MBW_Scores">
    <template #activator="{ props }">
      <base-workflow-list-item
        v-bind="props"
        title="Wahl des Migrationsbeirats"
        :disabled="disabled && !areAllElectionsFinished"
        :subtitle="areAllElectionsFinished ? '' : disabledMessage"
        :is-workflow-step-finished="isWahlFinished || areAllElectionsFinished"
        list-group-activator
      />
    </template>
    <base-workflow-list-item
      v-for="(route, index) in navigation"
      :key="index"
      :title="route.title"
      :to="route.targetRoute"
      :disabled="(disabled || route.disabled) && !areAllElectionsFinished"
      :is-workflow-step-finished="
        getWorkflowStateForRoute(
          wahlId,
          wahlbezirkId,
          route.targetRoute.name
        ) || areAllElectionsFinished
      "
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseWorkflowListItem from "@/components/navigation/common/BaseWorkflowListItem.vue";
import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { getWorkflowStateForRoute } = useWorkflowStore();

const { wahlbezirkId, wahlId, disabled } = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  disabled: boolean;
  disabledMessage: string;
  isWahlFinished: boolean;
}>();

const { navigation } = useMbwNavigationService(wahlId, wahlbezirkId);
const { areAllElectionsFinished } = storeToRefs(useWorkflowStore());
</script>
