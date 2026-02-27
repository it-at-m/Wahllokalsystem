<template>
  <v-list-group value="MBW_Scores">
    <template #activator="{ props }">
      <base-workflow-list-item
        v-bind="props"
        title="Wahl des Migrationsbeirats"
        :disabled="disabled"
        :subtitle="disabledMessage"
        :is-workflow-step-finished="isElectionFinished(wahlId, wahlbezirkId)"
        list-group-activator
      />
    </template>
    <base-workflow-list-item
      v-for="(route, index) in navigation"
      :key="index"
      :title="route.title"
      :to="route.targetRoute"
      :disabled="disabled || route.disabled"
      :is-workflow-step-finished="
        getWorkflowStateForRoute(wahlId, wahlbezirkId, route.targetRoute.name)
      "
    />
  </v-list-group>
</template>

<script setup lang="ts">
import BaseWorkflowListItem from "@/components/navigation/common/BaseWorkflowListItem.vue";
import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

const { isElectionFinished, getWorkflowStateForRoute } = useWorkflowStore();

const { wahlbezirkId, wahlId, disabled } = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  disabled: boolean;
  disabledMessage: string;
}>();

const { navigation } = useMbwNavigationService(wahlId, wahlbezirkId);
</script>
