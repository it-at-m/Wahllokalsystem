<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Offline-Synchronisierung"
    :confirmtext="hasDirtyTasks ? 'Synchronisieren' : 'Schließen'"
    :is-confirm-loading="isOfflineDataSyncing"
    :canceltext="hasDirtyTasks ? 'Schließen' : ''"
    icon="$offlineSync"
    @cancel="onCancelClicked"
    @confirm="onConfirmClicked"
  >
    <base-offline-data-sync-widget :model-value="dirtyTasks" />
  </base-dialog>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseOfflineDataSyncWidget from "@/components/common/widgets/BaseOfflineDataSyncWidget.vue";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const { synchronizeOfflineData, getSyncTasks } = useDataSyncStore();
const { isOfflineDataSyncing } = storeToRefs(useDataSyncStore());

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const dirtyTasks = ref(0);
const hasDirtyTasks = computed(() => dirtyTasks.value > 0);

watch(
  () => isDialogVisible.value,
  async () => {
    if (!isDialogVisible.value) return;

    await updateDirtyTasks();
    await synchronizeData();
  }
);

async function updateDirtyTasks() {
  const openTasks = await getSyncTasks();
  dirtyTasks.value = openTasks.length;
}

async function synchronizeData() {
  if (isOfflineDataSyncing.value) return;

  await synchronizeOfflineData();
  await updateDirtyTasks();
}

function onCancelClicked(): void {
  isDialogVisible.value = false;
}

async function onConfirmClicked() {
  if (hasDirtyTasks.value) {
    await synchronizeData();
  } else {
    isDialogVisible.value = false;
  }
}
</script>
