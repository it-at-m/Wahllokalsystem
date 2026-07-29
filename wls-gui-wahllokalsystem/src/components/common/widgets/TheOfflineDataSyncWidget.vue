<template>
  <div>
    <div>
      <v-icon
        icon="$send"
        class="mr-2"
        size="x-small"
      />
      Letzte Synchronisierung:
      {{
        toHhMm(lastSyncUpdateTime) ||
        "Es wurde noch keine Synchronisierung durchgeführt."
      }}
      <v-divider
        thickness="2"
        class="my-5"
      />
    </div>
    <div v-if="!hasTasksToRun || !hasDirtyTasksAfterSync">
      <v-icon
        icon="$valid"
        color="success"
        class="mr-2"
        size="x-small"
      />
      Alle Daten sind aktuell.
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
        is-indeterminate-for-first-task
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
        Fehlgeschlagene Datensätze: {{ numberOfDirtyTasksAfterSync }}
      </v-row>
    </div>
  </div>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseProgressLinear from "@/components/common/progressLinear/BaseProgressLinear.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDataSyncStore } from "@/stores/dataSyncStore.ts";

const {
  hasDirtyTasksAfterSync,
  hasTasksToRun,
  isOfflineDataSyncing,
  numberOfDirtyTasksAfterSync,
  numberOfTasksFinished,
  numberOfTasksToRun,
  lastSyncUpdateTime,
} = storeToRefs(useDataSyncStore());
const { toHhMm } = useDateTimeFormatter();
</script>
