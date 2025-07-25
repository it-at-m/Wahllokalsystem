<template>
  <v-app>
    <the-wls-app-bar />
    <v-main>
      <v-container fluid>
        <router-view v-slot="{ Component }">
          <v-fade-transition mode="out-in">
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
    <the-broadcast-read-confirmation-dialog />
    <the-wahlvorstand-anwesenheits-check-popup-dialog
      v-if="currentUserWahlbezirksArt === WahlbezirksArtEnum.UWB"
    />
  </v-app>
</template>

<script setup lang="ts">
import localforage from "localforage";
import { storeToRefs } from "pinia";
import { onMounted, onUnmounted } from "vue";
import { VApp, VContainer, VFadeTransition, VMain } from "vuetify/components";

import TheBroadcastReadConfirmationDialog from "@/components/broadcast/TheBroadcastReadConfirmationDialog.vue";
import TheWahlvorstandAnwesenheitsCheckPopupDialog from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitsCheckPopupDialog.vue";
import TheWlsAppBar from "@/components/wlsComponents/TheWlsAppBar.vue";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { loadEreignisse } = useEreignisStore();
const { loadUser } = useUserStore();
const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
const { initTasks } = useTaskManagerStore();
const { loadWaehler } = useMonitoringStore();
const { initWahlen } = useWahlenStore();
const { loadPflegeWaehlerverzeichnis } = useWahlbezirkStore();

const { startBroadcastMessageInterval, stopBroadcastMessageInterval } =
  useBroadcastCronjobService();

onMounted(async () => {
  try {
    await loadUser();
    await initWahlen();
    startBroadcastMessageInterval();
    await initTasks();
    loadEreignisse();
    loadWaehler();
    loadPflegeWaehlerverzeichnis();
  } catch (error) {
    console.debug(error);
  }

  // config for service worker indexed db (same config as in wahl-worker.js !)
  localforage.config({
    driver: localforage.INDEXEDDB,
    name: "wahldb",
    version: 1.0,
    storeName: "wahlstore",
    description: "store for wahlnumber",
  });
});

onUnmounted(() => {
  stopBroadcastMessageInterval();
});
</script>

<style>
@import "@fontsource/roboto/400.css";

.main {
  background-color: white;
}
</style>
