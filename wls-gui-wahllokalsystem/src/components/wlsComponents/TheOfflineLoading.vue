<template>
  <v-container>
    <h1 v-if="progressBarActive">Offline-Daten werden heruntergeladen...</h1>
    <h1 v-else>Offline-Daten wurden heruntergeladen.</h1>
    <p
      v-if="currentlyRunningTask"
      class="mt-4"
    >
      {{ currentlyRunningTask.name }}
    </p>
    <p
      v-else
      class="mt-4"
    >
      Herunterladen der Daten abgeschlossen ({{
        numberOfSuccessfulTasks + numberOfFailedTasks
      }}
      / {{ numberOfTasksToRun }})
    </p>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfSuccessfulTasks + numberOfFailedTasks"
    />
    <v-expansion-panels class="mt-4">
      <v-expansion-panel elevation="0">
        <v-expansion-panel-title class="pl-0">
          Erfolgreich heruntergeladen ({{ numberOfSuccessfulTasks }} /
          {{ numberOfTasksToRun }})
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <span
            v-for="successfulTask in successfullyTasks"
            :key="successfulTask.name"
            >{{ successfulTask.name }}</span
          >
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfSuccessfulTasks"
    />
    <v-expansion-panels class="mt-4">
      <v-expansion-panel elevation="0">
        <v-expansion-panel-title class="pl-0">
          Fehlgeschlagen ({{ numberOfFailedTasks }} / {{ numberOfTasksToRun }})
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <span
            v-for="failedTask in failedTasks"
            :key="failedTask.name"
            >{{ failedTask.name }}</span
          >
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfFailedTasks"
    />
  </v-container>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";
import {
  VContainer,
  VExpansionPanel,
  VExpansionPanels,
  VExpansionPanelText,
  VExpansionPanelTitle,
  VProgressLinear,
} from "vuetify/components";

import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";

const {
  numberOfSuccessfulTasks,
  numberOfFailedTasks,
  numberOfTasksToRun,
  currentlyRunningTask,
  successfullyTasks,
  failedTasks,
} = storeToRefs(useTaskManagerStore());

const progressBarActive = computed(() => {
  return (
    numberOfSuccessfulTasks.value + numberOfFailedTasks.value !=
    numberOfTasksToRun.value
  );
});
</script>

<style scoped></style>
