<template>
  <v-list-item
    key="team"
    class="py-2"
  >
    <v-list-item-title class="font-weight-medium">
      {{ teamEntry.teamID }}
    </v-list-item-title>

    <template #append>
      <div
        class="d-flex align-center justify-end"
        :style="'min-width: ' + minWidth"
      >
        <div class="flex-grow-1 text-center me-3">
          <v-icon
            :icon="statusConfig[teamEntry.status]?.icon"
            :color="statusConfig[teamEntry.status]?.color"
            size="large"
          />
        </div>

        <div
          class="text-right text-body-2 font-weight-medium text-capitalize"
          style="width: 110px"
        >
          {{ statusModelEnumToDisplayString(teamEntry.status) }}
        </div>
      </div>
    </template>
  </v-list-item>
</template>
<script setup lang="ts">
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/StimmzettelerfassungTeamStatusEntry.ts";

import { type PropType } from "vue";

import { useStimmzettelerfassungTeamStatusMapper } from "@/composables/dse/stimmzettelerfassungTeamStatusMapper.ts";

const { statusModelEnumToDisplayString, statusConfig } =
  useStimmzettelerfassungTeamStatusMapper();

defineProps({
  teamEntry: {
    type: Object as PropType<StimmzettelerfassungTeamStatusEntry>,
    required: true,
  },
  minWidth: {
    type: String,
    required: true,
  },
});
</script>
