<template>
  <base-dialog
    :visible="isDialogVisible"
    dialogtitle="Offline-Synchronisierung"
    confirmtext="Synchronisieren"
    :is-confirm-active="dirtyTasks > 0"
    canceltext="Schließen"
    icon="$offlineSync"
    @cancel="onCancelClicked"
    @confirm="onConfirmClicked"
  >
    <div v-if="dirtyTasks == 0">Alle Daten sind Aktuell.</div>
    <div v-else>
      <div>
        <v-row
          class="ml-0"
          align="center"
        >
          <v-icon
            icon="$send"
            class="mr-2"
            size="x-small"
          />
          Letzte Aktualisierung: {{ toHhMm(lastSyncUpdateTime) }}
          <v-spacer />
          <div>
            Status:
            <v-icon
              :icon="syncStatus == 'error' ? '$invalid' : '$valid'"
              :color="syncStatus == 'error' ? 'error' : 'success'"
              class="mr-2"
              size="x-small"
            />
          </div>
        </v-row>
        <v-divider
          thickness="2"
          class="my-5"
        />
        <v-row class="ml-0 mb-5">
          Nicht Synchronisierte Datensätze: {{ dirtyTasks }}
        </v-row>
      </div>
      <base-progress-linear
        :titel="
          isOfflineDataSyncing
            ? 'Fortschritt'
            : 'Letzte Synchronisierung abgeschlossen'
        "
        :is-loading="isOfflineDataSyncing"
        :current="numberOfTasksFinished"
        :total="numberOfTasksToRun"
        :tasks="[]"
        :show-tasks="false"
      />
    </div>
  </base-dialog>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { ref, watch } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const { synchronizeOfflineData, getSyncTasks } = useDataSyncStore();
const {
  isOfflineDataSyncing,
  numberOfTasksFinished,
  numberOfTasksToRun,
  lastSyncUpdateTime,
} = storeToRefs(useDataSyncStore());
const { toHhMm } = useDateTimeFormatter();

const { isDialogVisible } = defineProps<{
  isDialogVisible: boolean;
}>();

const dirtyTasks = ref(numberOfTasksToRun.value);
const syncStatus = ref("success");

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

async function initiateOfflineDataSync() {
  if (!isOfflineDataSyncing.value) {
    await synchronizeOfflineData();
    const openTasks = await getSyncTasks();
    dirtyTasks.value = openTasks.length;
    if (dirtyTasks.value > 0) {
      syncStatus.value = "error";
      // emit("syncError"); TODO
    } else {
      syncStatus.value = "success";
      // emit("syncSuccess"); TODO
    }
  }
}

function onCancelClicked(): void {
  emit("cancel");
}
async function onConfirmClicked() {
  await initiateOfflineDataSync();
}
</script>
