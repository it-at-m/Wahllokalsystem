<template>
  <v-dialog
    :model-value="isDialogVisible"
    max-width="400px"
    persistent
  >
    <v-card>
      <v-card-title> Synchronisierung </v-card-title>
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
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { ref, watch } from "vue";

import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const { synchronizeOfflineData, getSyncTasks } = useDataSyncStore();
const { isOfflineDataSyncing, numberOfTasksFinished, numberOfTasksToRun } =
  storeToRefs(useDataSyncStore());

const props = defineProps<{
  isDialogVisible: boolean;
}>();

const emit = defineEmits<{
  syncSuccess: [];
  syncError: [];
}>();

watch(
  () => props.isDialogVisible,
  async () => {
    if (props.isDialogVisible) {
      await initiateOfflineDataSync();
    }
  }
);

const isSyncInProgress = ref(false);

async function initiateOfflineDataSync() {
  isSyncInProgress.value = true;

  if (!isOfflineDataSyncing.value) {
    await synchronizeOfflineData();
    const openTasks = await getSyncTasks();
    if (openTasks.length > 0) {
      emit("syncError");
    } else {
      emit("syncSuccess");
    }
  }

  isSyncInProgress.value = false;
}
</script>
