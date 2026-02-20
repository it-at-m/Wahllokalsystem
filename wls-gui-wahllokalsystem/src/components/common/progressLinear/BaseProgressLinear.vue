<template>
  <div>
    <v-expansion-panels class="mt-4">
      <v-expansion-panel elevation="0">
        <v-expansion-panel-title class="pl-0">
          {{ titel }} ({{ current }} / {{ total }})
        </v-expansion-panel-title>
        <v-expansion-panel-text data-test="expansion-panel-tasklist">
          {{ tasks.map((task) => task.name).join(", ") }}
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
import type { Task } from "@/types/tasks/Task.ts";

defineProps<{
  titel: string;
  isLoading: boolean;
  current: number;
  total: number;
  tasks: Task[];
  color?: string;
}>();
</script>
