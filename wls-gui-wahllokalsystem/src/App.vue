<template>
  <v-app>
    <the-wls-app-bar />
    <v-main>
      <v-container fluid>
        <router-view v-slot="{ Component }">
          <v-fade-transition mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
    <the-broadcast-read-confirmation-dialog />
    <the-wahlvorstand-anwesenheits-check-popup-dialog
      v-if="isUWB"
      data-test="wahlvorstand-anwesenheits-check-popup-dialog"
    />
  </v-app>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { onMounted, onUnmounted } from "vue";

import TheBroadcastReadConfirmationDialog from "@/components/broadcast/TheBroadcastReadConfirmationDialog.vue";
import TheWahlvorstandAnwesenheitsCheckPopupDialog from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitsCheckPopupDialog.vue";
import TheWlsAppBar from "@/components/wlsComponents/TheWlsAppBar.vue";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { loadUser } = useUserStore();
const { isUWB } = storeToRefs(useUserStore());
const { initTasks } = useTaskManagerStore();
const { wahlenActions, beanstandeteWahlbriefeActions } = useWahlenStore();

const { startBroadcastMessageInterval, stopBroadcastMessageInterval } =
  useBroadcastCronjobService();

const { setupIndexDB } = useIndexDB();

onMounted(async () => {
  try {
    await loadUser();
    await wahlenActions.initWahlen();
    startBroadcastMessageInterval();
    await initTasks();
    await beanstandeteWahlbriefeActions.initBeanstandeteWahlbriefe();
  } catch (error) {
    console.debug(error);
  }

  // config for service worker indexed db (same config as in wahl-worker.js !)
  setupIndexDB();
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
