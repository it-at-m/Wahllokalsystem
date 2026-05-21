<template>
  <div>
    <v-menu>
      <template #activator="{ props: menuActivator }">
        <v-btn
          v-bind="menuActivator"
          :icon="activatorButtonIcon"
          variant="text"
          density="comfortable"
          size="x-large"
          :color="activatorButtonColor"
          data-test="button-activator-menu-online-offline"
        />
      </template>
      <v-card
        width="250"
        max-height="280"
      >
        <v-list class="pt-0">
          <v-list-item>
            <base-text-button
              :loading="isCheckingStatus"
              active
              style="width: 100%"
              @click.stop="onCheckStatusClicked"
            >
              Verbindung überprüfen
            </base-text-button>
          </v-list-item>
          <v-list-item active>
            <v-row
              class="ma-1"
              align="center"
            >
              <strong>Verbindungsstatus</strong>
              <v-spacer />
            </v-row>
          </v-list-item>
          <v-divider
            thickness="2"
            color="black"
          />
          <v-list-item> {{ onlineOfflineExplanation }}</v-list-item>
          <v-list-item>
            <base-text-button
              active
              @click="isOfflineSyncDialogVisible = true"
            >
              Daten Synchronisieren
            </base-text-button>
          </v-list-item>
        </v-list>
      </v-card>
    </v-menu>
    <the-manual-offline-data-sync-dialog
      v-model="isOfflineSyncDialogVisible"
      @cancel="isOfflineSyncDialogVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import TheManualOfflineDataSyncDialog from "@/components/common/dialogs/TheManualOfflineDataSyncDialog.vue";
import { useInterval } from "@/composables/scheduler/interval.ts";
import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";

const INTERVAL_OF_30_SECONDS_AS_MILLISECONDS = 1000 * 30;

useInterval(
  "LastSeen Interval",
  () => {
    checkConnectionStatus();
  },
  INTERVAL_OF_30_SECONDS_AS_MILLISECONDS
);

const onlineOfflineStore = useOnlineOfflineStore();
const { isCheckingStatus, isOnline } = storeToRefs(onlineOfflineStore);

const isOfflineSyncDialogVisible = ref(false);

const activatorButtonColor = computed(() =>
  isOnline.value ? "white" : "error"
);
const activatorButtonIcon = computed(() =>
  isOnline.value ? "$signalOnline" : "$signalOffline"
);

const onlineOfflineExplanation = computed(() =>
  isOnline.value
    ? "Sie sind aktuell online."
    : "Sie sind aktuell offline. Ihre Eingaben werden lokal gespeichert und synchronisiert, sobald Sie wieder online sind."
);

function checkConnectionStatus() {
  onlineOfflineStore.checkConnectionState();
}

function onCheckStatusClicked() {
  checkConnectionStatus();
}
</script>
