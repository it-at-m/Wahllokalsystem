<template>
  <v-container>
    <h1 v-if="isLoading">Offline-Daten werden heruntergeladen...</h1>
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
      :striped="isLoading"
      :max="numberOfTasksToRun"
      :model-value="numberOfSuccessfulTasks + numberOfFailedTasks"
    />
    <base-progress-linear
      titel="Erfolgreich heruntergeladen"
      :is-loading="isLoading"
      :current="numberOfSuccessfulTasks"
      :total="numberOfTasksToRun"
      :tasks="successfullyTasks"
    />
    <base-progress-linear
      titel="Fehlgeschlagen"
      :is-loading="isLoading"
      :current="numberOfFailedTasks"
      :total="numberOfTasksToRun"
      :tasks="failedTasks"
    />
  </v-container>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";
import { VContainer, VProgressLinear } from "vuetify/components";

import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";

const {
  numberOfSuccessfulTasks,
  numberOfFailedTasks,
  numberOfTasksToRun,
  currentlyRunningTask,
  successfullyTasks,
  failedTasks,
} = storeToRefs(useTaskManagerStore());

const isLoading = computed(() => {
  return (
    numberOfSuccessfulTasks.value + numberOfFailedTasks.value !=
    numberOfTasksToRun.value
  );
});
</script>

<style scoped></style>
