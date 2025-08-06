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
      />
    </template>
    <v-card
      width="250"
      max-height="280"
    >
      <v-list class="pt-0">
        <v-list-item>
          <v-btn
            :loading="isCheckingStatus"
            color="primary"
            style="width: 100%"
            @click.stop="onCheckStatusClicked"
          >
            Check online status
          </v-btn>
        </v-list-item>
        <v-list-item class="list-header">
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
import axios from "axios";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import {
  VBtn,
  VCard,
  VDivider,
  VList,
  VListItem,
  VMenu,
  VRow,
  VSpacer,
} from "vuetify/components";

import OfflineSyncer from "@/components/wlsComponents/OfflineSyncer.vue";
import { useInterval } from "@/composables/useInterval";
import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";

const INTERVAL_OF_30_SECONDS_AS_MILLISECONDS = 1000 * 60 * 30;

useInterval(() => {
  checkConnectionStatus();
}, INTERVAL_OF_30_SECONDS_AS_MILLISECONDS); // updates every 30 seconds

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

<style>
.list-header {
  background: #f0f0f0;
}
</style>
