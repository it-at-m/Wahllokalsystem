<template>
  <div>
    <p
      v-if="!tasks?.length"
      class="my-3"
    >
      {{ titel }} ({{ current }} / {{ total }})
    </p>
    <v-expansion-panels v-else>
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
  tasks?: Task[];
  color?: string;
}>();
</script>
