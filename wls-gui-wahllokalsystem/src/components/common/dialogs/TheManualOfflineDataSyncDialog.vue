<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Offline-Synchronisierung"
    confirmtext="Synchronisieren"
    canceltext="Schließen"
    icon="$offlineSync"
    @cancel="onCancelClicked"
    @confirm="onConfirmClicked"
  >
    <base-progress-linear
      v-if="openTasks.length > 0"
      :titel="
        isOfflineDataSyncing ? 'Fortschritt' : 'Zuletzt synchronisierte Daten'
      "
      :is-loading="isOfflineDataSyncing"
      :current="numberOfTasksFinished"
      :total="numberOfTasksToRun"
      :tasks="openTasks"
      :show-tasks="false"
    />
    <div v-else>Alle Daten sind Aktuell.</div>
  </base-dialog>
</template>
<script setup lang="ts">
import type { Task } from "@/types/tasks/Task.ts";

import { storeToRefs } from "pinia";
import { ref, watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const { synchronizeOfflineData, getSyncTasks } = useDataSyncStore();
const { isOfflineDataSyncing, numberOfTasksFinished, numberOfTasksToRun } =
  storeToRefs(useDataSyncStore());

const { isDialogVisible } = defineProps<{
  isDialogVisible: boolean;
}>();

watch(
  () => isDialogVisible,
  async () => {
    if (isDialogVisible) {
      await initiateOfflineDataSync();
    }
  }
);

const emit = defineEmits<{
  cancel: [];
  syncSuccess: [];
  syncError: [];
}>();

const openTasks = ref<Task[]>([]);

async function initiateOfflineDataSync() {
  if (!isOfflineDataSyncing.value) {
    await synchronizeOfflineData();
    const newTasks = await getSyncTasks();
    if (newTasks.length > 0) {
      openTasks.value = newTasks;
    }
  }
}

function onCancelClicked(): void {
  emit("cancel");
}
async function onConfirmClicked() {
  await synchronizeOfflineData();
  await initiateOfflineDataSync();
}
</script>
