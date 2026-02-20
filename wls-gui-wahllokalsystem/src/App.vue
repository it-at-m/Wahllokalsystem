<template>
  <v-app>
    <the-wls-app-bar />
    <v-main>
      <v-container fluid>
        <router-view v-slot="{ route, Component }">
          <v-fade-transition mode="out-in">
            <!-- Keep alive is fundamental for our app, to work correctly - see doc for frontend architecture -->
            <keep-alive>
              <component
                :is="Component"
                :key="route.fullPath"
              >
                <!-- :key attribute is fundamental for our app, to work correctly with keep alive - see doc for frontend architecture -->
              </component>
            </keep-alive>
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
    <the-testseite-drucken-dialog v-if="showTestdruckDialog" />
    <the-broadcast-read-confirmation-dialog />
    <the-wahlvorstand-anwesenheits-check-popup-dialog
      v-if="isUWB && isTimeToCheckAnwesenheitInFuture"
      data-test="wahlvorstand-anwesenheits-check-popup-dialog"
    />
    <the-wahlschluss-check-popup-dialog
      v-if="isTimeToCheckWahlschlussInFuture"
    />
  </v-app>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, onMounted, onUnmounted, ref } from "vue";

import TheBroadcastReadConfirmationDialog from "@/components/broadcast/TheBroadcastReadConfirmationDialog.vue";
import TheWahlschlussCheckPopupDialog from "@/components/wahlhandlung/TheWahlschlussCheckPopupDialog.vue";
import TheWahlvorstandAnwesenheitsCheckPopupDialog from "@/components/wahlvorstand/TheWahlvorstandAnwesenheitsCheckPopupDialog.vue";
import TheTestseiteDruckenDialog from "@/components/wlsComponents/TheTestseiteDruckenDialog.vue";
import TheWlsAppBar from "@/components/wlsComponents/TheWlsAppBar.vue";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useIndexDB } from "@/composables/indexDB/indexDB.ts";
import { useServiceWorkerPinSyncer } from "@/composables/serviceWorker/serviceWorkerPinSyncer.ts";
import { useServiceWorkerUtils } from "@/composables/serviceWorker/serviceWorkerUtils.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useInitTaskManagerStore } from "@/stores/initTaskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { awaitServiceWorkerActive } = useServiceWorkerUtils();
const { syncPin } = useServiceWorkerPinSyncer();

const { loadUser } = useUserStore();
const { dateTimeToCheckAnwesenheit, dateTimeToCheckWahlschluss } = storeToRefs(
  useInfomanagementStore()
);
const { isUWB } = storeToRefs(useUserStore());
const { initTasks } = useInitTaskManagerStore();
const { wahlenActions } = useWahlenStore();
const { isTodayOrFuture } = useDateTimeUtils();

const { startBroadcastMessageInterval, stopBroadcastMessageInterval } =
  useBroadcastCronjobService();

const isTimeToCheckAnwesenheitInFuture = computed(() =>
  dateTimeToCheckAnwesenheit.value
    ? isTodayOrFuture(dateTimeToCheckAnwesenheit.value)
    : false
);
const showTestdruckDialog = ref(false);

const isTimeToCheckWahlschlussInFuture = computed(() =>
  dateTimeToCheckWahlschluss.value
    ? isTodayOrFuture(dateTimeToCheckWahlschluss.value)
    : false
);

const indexDBSingleton = useIndexDB();

onMounted(async () => {
  // config for service worker indexed db (same config as in wahl-worker.js !)
  indexDBSingleton.setupIndexDB();

  try {
    await loadUser();
    await awaitServiceWorkerActive();
    await syncPin();
    await wahlenActions.initWahlen();
    startBroadcastMessageInterval();
    await initTasks();

    showTestdruckDialog.value = true;
  } catch (error) {
    console.debug(error);
  }
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
