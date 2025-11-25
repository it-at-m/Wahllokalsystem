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
            data-test="button-sync-offline-data"
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
        <base-text-button @click="onCloseClicked">Schließen</base-text-button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
import { mergeProps, ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import { useDataSyncer } from "@/composables/indexDB/dataSyncer.ts";
import { useTaskManager } from "@/composables/tasks/taskManager.ts";
import {useUserStore} from "@/stores/userStore.ts";

const { getSyncTasks } = useDataSyncer();
const { cryptoKey, iv } = useUserStore();

const { setTasks, numberOfTasksFinished, numberOfTasksToRun, runAllTasks } =
  useTaskManager();

const isDialogVisible = ref(false);
const isSyncInProgress = ref(false);

async function synchronizeOfflineData() {
  isDialogVisible.value = true;
  isSyncInProgress.value = true;

  setTasks(await getSyncTasks(cryptoKey, iv));
  await runAllTasks();

  isSyncInProgress.value = false;
}

function onCloseClicked() {
  isDialogVisible.value = false;
}
</script>
