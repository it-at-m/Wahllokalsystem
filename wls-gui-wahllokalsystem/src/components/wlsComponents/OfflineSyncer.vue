<template>
  <v-dialog
    v-model="isDialogVisible"
    max-width="400px"
    persistent
  >
    <template #activator="{ props: dialogActivator }">
      <v-tooltip text="sync offline data">
        <template #activator="{ props: tooltipActivator }">
          <v-btn
            v-bind="mergeProps(dialogActivator, tooltipActivator)"
            icon="$reload"
            class="px-0"
            size="x-small"
            color="primary"
            :loading="isSyncInProgress"
            @click="synchronizeOfflineData"
          />
        </template>
      </v-tooltip>
    </template>

    <v-card>
      <v-card-title> Synchronizing </v-card-title>
      <v-card-text>
        <div>
          {{ numberOfTasksFinished }} von {{ numberOfTasksToRun }} abgeschlossen
        </div>
        <v-progress-linear
          :model-value="numberOfTasksFinished"
          :max="numberOfTasksToRun"
          :striped="isSyncInProgress"
        />
      </v-card-text>
      <v-card-actions>
        <v-btn @click="onCloseClicked">Schließen</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
import { mergeProps, ref } from "vue";

import { useTaskManager } from "@/composables/common/taskManager.ts";
import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";

const { getSyncTasks } = useDataSyncer();

const { setTasks, numberOfTasksFinished, numberOfTasksToRun, runAllTasks } =
  useTaskManager();

const isDialogVisible = ref(false);
const isSyncInProgress = ref(false);

async function synchronizeOfflineData() {
  isDialogVisible.value = true;
  isSyncInProgress.value = true;

  setTasks(await getSyncTasks());
  await runAllTasks();

  isSyncInProgress.value = false;
}

function onCloseClicked() {
  isDialogVisible.value = false;
}
</script>
