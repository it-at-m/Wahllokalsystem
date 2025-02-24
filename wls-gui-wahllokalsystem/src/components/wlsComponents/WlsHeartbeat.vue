<template>
  <v-menu>
    <template #activator="{ props: menuActivator }">
      <v-btn
        v-bind="menuActivator"
        :icon="getIcon(isOffline)"
        variant="text"
        density="comfortable"
        size="x-large"
        :color="getColor(isOffline)"
      ></v-btn>
    </template>
    <v-card
      width="250"
      max-height="280"
    >
      <v-list class="pt-0">
        <v-list-item>
          <v-btn
            :disabled="isCheckStatusBtnDisabled"
            color="primary"
            style="width: 100%"
            @click.stop="heartbeat"
          >
            {{ checkStatusBtnText }}
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
        ></v-divider>
        <v-list-item> {{ getText(isOffline) }}</v-list-item>
      </v-list>
    </v-card>
  </v-menu>
</template>

<script setup lang="ts">
import axios from "axios";
import { ref } from "vue";
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

import { basicPostConfig } from "@/api/axios-utils";
import OfflineSyncer from "@/components/wlsComponents/OfflineSyncer.vue";
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { useInterval } from "@/composables/useInterval";

// todo: checking status activated via click. uncomment to activate periodically
/*
useInterval(() => {
  heartbeat();
}, 30000); // updates every 30 seconds
*/

defineProps({
  isOffline: { type: Boolean, default: false },
});

const emit = defineEmits(["update:isOffline"]);
const checkStatusBtnText = ref("Check online status");
const isCheckStatusBtnDisabled = ref(false);

function getIcon(isOffline: boolean) {
  return isOffline ? "$signalOffline" : "$signalOnline";
}

function getText(isOffline: boolean) {
  return isOffline
    ? "Sie sind aktuell offline. Ihre Eingaben werden lokal gespeichert und synchronisiert, sobald Sie wieder online sind."
    : "Sie sind aktuell online.";
}

function getColor(isOffline: boolean) {
  return isOffline ? "error" : "white";
}

// todo: example implementation --> api logic should be transferred to composable
async function heartbeat() {
  disableStatusBtn();
  await new Promise((resolve) => setTimeout(resolve, 0o500)); // wait half a sec to show searching message
  return await axios
    .request(
      basicPostConfig(
        "api/monitoring-service/businessActions/lastSeen/wbz-1",
        "ONLINE_ONLY"
      )
    )
    .then((response) => {
      if (response.status == 200) {
        emit("update:isOffline", false);
        enableStatusBtn();
      }
    })
    .catch(() => {
      emit("update:isOffline", true);
      enableStatusBtn();
    });
}

function disableStatusBtn() {
  isCheckStatusBtnDisabled.value = true;
  checkStatusBtnText.value = "searching signal...";
}

function enableStatusBtn() {
  isCheckStatusBtnDisabled.value = false;
  checkStatusBtnText.value = "Check online status";
}
</script>

<style>
.list-header {
  background: #f0f0f0;
}
</style>
