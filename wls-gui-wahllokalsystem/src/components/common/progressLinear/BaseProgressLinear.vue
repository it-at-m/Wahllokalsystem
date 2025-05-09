<template>
  <div>
    <v-expansion-panels class="mt-4">
      <v-expansion-panel elevation="0">
        <v-expansion-panel-title class="pl-0">
          {{ titel }} ({{ current }} / {{ total }})
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <span
            v-for="task in tasks"
            :key="task.name"
          >
            {{ task.name }},
          </span>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
    <v-progress-linear
      :striped="isLoading"
      :max="total"
      :model-value="current"
      :color="color"
    />
  </div>
</template>
<script setup lang="ts">
import type { Task } from "@/types/Task.ts";

import {
  VExpansionPanel,
  VExpansionPanels,
  VExpansionPanelText,
  VExpansionPanelTitle,
  VProgressLinear,
} from "vuetify/components";

defineProps<{
  titel: string;
  isLoading: boolean;
  current: number;
  total: number;
  tasks: Task[];
  color?: string;
}>();
</script>
