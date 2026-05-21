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
    <div>
      <v-icon
        icon="$send"
        class="mr-2"
        size="x-small"
      />
      Letzte Aktualisierung:
      {{
        toHhMm(lastSyncUpdateTime) ||
        "Es wurde noch keine Synchronisierung durchgeführt."
      }}

      <v-divider
        thickness="2"
        class="my-5"
      />
    </div>
    <div v-if="!hasDirtyTasks">
      <v-icon
        icon="$valid"
        color="success"
        class="mr-2"
        size="x-small"
      />
      Alle Daten sind Aktuell.
    </div>
    <div v-else>
      <base-progress-linear
        class="my-5"
        :titel="
          isOfflineDataSyncing
            ? 'Fortschritt'
            : 'Synchronisierung abgeschlossen'
        "
        :is-loading="isOfflineDataSyncing"
        :current="numberOfTasksFinished"
        :total="numberOfTasksToRun"
        :tasks="[]"
        :show-tasks="false"
      />
      <v-row
        v-if="!isOfflineDataSyncing"
        align="center"
        class="my-1 ml-0"
      >
        <v-icon
          icon="$invalid"
          color="error"
          class="mr-2"
          size="x-small"
        />
        Fehlgeschlagene Datensätze: {{ dirtyTasks }}
      </v-row>
    </div>
  </base-dialog>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

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

const isDialogVisible = defineModel("modelValue", {
  type: Boolean,
  required: true,
});

const dirtyTasks = ref(numberOfTasksToRun.value);
const hasDirtyTasks = computed(() => dirtyTasks.value > 0);

watch(
  () => isDialogVisible.value,
  async () => {
    if (isDialogVisible.value) {
      await initiateOfflineDataSync();
    }
  }
);

const emit = defineEmits<{
  cancel: [];
}>();

async function initiateOfflineDataSync() {
  if (!isOfflineDataSyncing.value) {
    await synchronizeOfflineData();
    const openTasks = await getSyncTasks();
    dirtyTasks.value = openTasks.length;
  }
}

function onCancelClicked(): void {
  emit("cancel");
}
async function onConfirmClicked() {
  if (hasDirtyTasks.value) {
    await initiateOfflineDataSync();
  } else {
    isDialogVisible.value = false;
  }
}
</script>
