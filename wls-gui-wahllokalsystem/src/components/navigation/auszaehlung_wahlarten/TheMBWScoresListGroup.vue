<template>
  <v-list-group value="MBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahl des Migrationsbeirats"
        :lines="groupActivatorListItemLines"
      >
        <template
          v-if="disabled"
          #subtitle
        >
          {{ disabledMessage }}
        </template>
        <template #prepend>
          <v-icon
            :icon="
              disabled
                ? '$disabled'
                : isElectionFinished(wahlId, wahlbezirkId)
                  ? '$valid'
                  : '$edit'
            "
            size="small"
          />
        </template>
      </v-list-item>
    </template>
    <v-list-item
      v-for="(route, index) in navigation"
      :key="index"
      :title="route.title"
      :to="route.targetRoute"
      :disabled="disabled || route.disabled"
    >
      <template #prepend>
        <v-icon
          :icon="
            disabled || route.disabled
              ? '$disabled'
              : getWorkflowStateForRoute(
                    wahlId,
                    wahlbezirkId,
                    route.targetRoute.name
                  )
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
import { computed } from "vue";

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
const groupActivatorListItemLines = computed(() => (disabled ? false : "one"));
</script>
