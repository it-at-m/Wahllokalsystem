<template>
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
            color="primary"
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
            <offline-syncer />
          </v-row>
        </v-list-item>
        <v-divider
          thickness="2"
          color="black"
        />
        <v-list-item> {{ onlineOfflineExplanation }}</v-list-item>
      </v-list>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import OfflineSyncer from "@/components/wlsComponents/OfflineSyncer.vue";
import { useInterval } from "@/composables/useInterval";
import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";

const INTERVAL_OF_30_SECONDS_AS_MILLISECONDS = 1000 * 30;

useInterval(() => {
  checkConnectionStatus();
}, INTERVAL_OF_30_SECONDS_AS_MILLISECONDS);

const onlineOfflineStore = useOnlineOfflineStore();
const { isCheckingStatus, isOnline } = storeToRefs(onlineOfflineStore);

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
