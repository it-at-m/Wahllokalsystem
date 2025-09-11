<template>
  <v-container>
    <h1 v-if="isLoading">Offline-Daten werden heruntergeladen...</h1>
    <h1 v-else>Offline-Daten wurden heruntergeladen.</h1>
    <p
      v-if="currentlyRunningTask"
      class="my-4"
    >
      {{ currentlyRunningTask.name }}
    </p>
    <p
      v-else
      class="my-4"
    >
      Herunterladen der Daten abgeschlossen ({{ numberOfTasksFinished }} /
      {{ numberOfTasksToRun }})
    </p>
    <v-progress-linear
      :striped="isLoading"
      :max="numberOfTasksToRun"
      :model-value="numberOfTasksFinished"
    />
    <base-progress-linear
      titel="Erfolgreich heruntergeladen"
      data-test="base-progress-success"
      :is-loading="isLoading"
      :current="numberOfTasksSucceeded"
      :total="numberOfTasksToRun"
      :tasks="successfullyTasks"
      color="success"
    />
    <base-progress-linear
      titel="Fehlgeschlagen"
      data-test="base-progress-failed"
      :is-loading="isLoading"
      :current="numberOfTasksFailed"
      :total="numberOfTasksToRun"
      :tasks="failedTasks"
      color="warning"
    />
  </v-container>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";

const {
  numberOfTasksToRun,
  numberOfTasksSucceeded,
  numberOfTasksFailed,
  numberOfTasksFinished,
  currentlyRunningTask,
  successfullyTasks,
  failedTasks,
} = storeToRefs(useTaskManagerStore());

const isLoading = computed(() => {
  return (
    successfullyTasks.value.length + failedTasks.value.length !=
    numberOfTasksToRun.value
  );
});
</script>
