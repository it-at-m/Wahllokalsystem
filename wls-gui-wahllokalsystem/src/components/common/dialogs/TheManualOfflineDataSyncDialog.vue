<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Offline-Synchronisierung"
    :confirmtext="hasTasksToRun ? 'Synchronisieren' : 'Schließen'"
    :is-confirm-loading="isOfflineDataSyncing"
    :canceltext="hasTasksToRun ? 'Schließen' : ''"
    icon="$offlineSync"
    @cancel="onCancelClicked"
    @confirm="onConfirmClicked"
  >
    <the-offline-data-sync-widget />
  </base-dialog>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import TheOfflineDataSyncWidget from "@/components/common/widgets/TheOfflineDataSyncWidget.vue";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const dataSyncStore = useDataSyncStore();
const { synchronizeOfflineData } = dataSyncStore;
const { isOfflineDataSyncing, hasTasksToRun } = storeToRefs(dataSyncStore);

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

watch(
  () => isDialogVisible.value,
  async () => {
    if (!isDialogVisible.value) return;

    await synchronizeOfflineData();
  }
);

function onCancelClicked(): void {
  isDialogVisible.value = false;
}

async function onConfirmClicked() {
  if (hasTasksToRun.value) {
    await synchronizeOfflineData();
  } else {
    isDialogVisible.value = false;
  }
}
</script>
