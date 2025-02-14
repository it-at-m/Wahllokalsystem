<template>
  <v-menu>
    <template #activator="{ props: menu }">
      <v-btn
        v-bind="menu"
        :icon="getIcon(isOffline)"
        variant="text"
        density="comfortable"
        size="x-large"
        :color="getColor(isOffline)"
      ></v-btn>
    </template>
    <v-card
      width="250"
      max-height="200"
    >
      <v-list class="pt-0">
        <v-list-item class="list-header">
          <strong>Verbindungsstatus</strong>
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
import {
  VBtn,
  VCard,
  VDivider,
  VList,
  VListItem,
  VMenu,
} from "vuetify/components";

import { basicPostConfig } from "@/api/axios-utils";
import { useInterval } from "@/composables/useInterval";

useInterval(() => {
  heartbeat();
}, 30000); // updates every 30 seconds

defineProps({
  isOffline: { type: Boolean, default: false },
});

const emit = defineEmits(["update:isOffline"]);

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
      }
    })
    .catch(() => {
      emit("update:isOffline", true);
    });
}
</script>

<style>
.list-header {
  background: #f0f0f0;
}
</style>
