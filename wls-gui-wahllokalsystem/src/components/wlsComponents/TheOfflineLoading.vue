<template>
  <v-container>
    <h1 v-if="progressBarActive">Offline-Daten werden heruntergeladen...</h1>
    <h1 v-else>Offline-Daten wurden heruntergeladen.</h1>
    <p v-if="currentlyRunningTask">{{ currentlyRunningTask.name }}</p>
    <p v-else>
      Herunterladen der Daten abgeschlossen ({{
        numberOfSuccessfullTasks + numberOfFailedTasks
      }}
      / {{ numberOfTasksToRun }})
    </p>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfSuccessfullTasks + numberOfFailedTasks"
    />
    <p>
      Erfolgreich heruntergeladen ({{ numberOfSuccessfullTasks }} /
      {{ numberOfTasksToRun }})
    </p>
    <v-progress-linear
      color="primary"
      :striped="progressBarActive"
      :max="numberOfTasksToRun"
      :model-value="numberOfSuccessfullTasks"
    />
    <p>Fehlgeschlagen ({{ numberOfFailedTasks }} / {{ numberOfTasksToRun }})</p>
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
import { VContainer, VProgressLinear } from "vuetify/components";

import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";

const {
  numberOfSuccessfullTasks,
  numberOfFailedTasks,
  numberOfTasksToRun,
  currentlyRunningTask,
} = storeToRefs(useTaskManagerStore());

const progressBarActive = computed(() => {
  return (
    numberOfSuccessfullTasks.value + numberOfFailedTasks.value !=
    numberOfTasksToRun.value
  );
});
</script>

<style scoped></style>
