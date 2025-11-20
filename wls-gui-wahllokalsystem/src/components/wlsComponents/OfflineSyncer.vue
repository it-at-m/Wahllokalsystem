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
            @click="initiateOfflineDataSync"
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
import { storeToRefs } from "pinia";
import { mergeProps, ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import { useTaskManager } from "@/composables/tasks/taskManager.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const { synchronizeOfflineData } = useDataSyncStore();
const { isOfflineDataSyncing } = storeToRefs(useDataSyncStore());

const { numberOfTasksFinished, numberOfTasksToRun } = useTaskManager();

const isDialogVisible = ref(false);
const isSyncInProgress = ref(false);

async function initiateOfflineDataSync() {
  isDialogVisible.value = true;
  isSyncInProgress.value = true;

  if (!isOfflineDataSyncing) {
    await synchronizeOfflineData();
  }
  isSyncInProgress.value = false;
}

function onCloseClicked() {
  isDialogVisible.value = false;
}
</script>
