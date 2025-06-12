<template>
  <v-app>
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          cols="3"
          class="d-flex align-center justify-start"
        >
          <v-app-bar-nav-icon @click.stop="toggleDrawer()" />
          <span class="navbar-text mx-2"> {{ wahltermin }} </span>
          <base-icon-wahlbezirksart class="mx-2" />
          <span class="navbar-text mx-2">
            Wahlbezirk {{ wahlbezirknummer }}
          </span>
        </v-col>
        <v-col
          cols="6"
          class="d-flex align-center justify-center"
        />
        <v-col
          cols="3"
          class="d-flex align-center justify-end"
        >
          <wls-clock />
          <wls-heartbeat v-model:is-offline="isOffline" />
          <the-info-help-icon />
        </v-col>
      </v-row>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer">
      <v-list>
        <v-list-item
          title="Home"
          :to="'/'"
        />
        <v-list-item
          title="Wahlvorstand"
          :to="ROUTE_WAHLVORSTAND"
        />
        <v-list-group value="Wahlvorbereitung">
          <template #activator="{ props }">
            <v-list-item
              v-bind="props"
              title="Wahlvorbereitung"
            />
          </template>
          <v-list-item
            title="Beginn Stimmabgabe"
            :to="ROUTE_BEGINN_STIMMABGABE"
          />
          <v-list-item
            title="Wahlschliessung"
            :to="ROUTE_WAHLSCHLIESSUNG"
          />
        </v-list-group>
        <v-list-item
          title="Ereignisse"
          :to="ROUTE_EREIGNISSE"
        />
      </v-list>
    </v-navigation-drawer>
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
  </v-app>
</template>

<script setup lang="ts">
import { useToggle } from "@vueuse/core";
import localforage from "localforage";
import { storeToRefs } from "pinia";
import { onMounted, onUnmounted, ref } from "vue";
import {
  VApp,
  VAppBar,
  VAppBarNavIcon,
  VCol,
  VContainer,
  VFadeTransition,
  VList,
  VListGroup,
  VListItem,
  VMain,
  VNavigationDrawer,
  VRow,
} from "vuetify/components";

import TheInfoHelpIcon from "@/components/basisdaten/TheInfoHelpIcon.vue";
import TheBroadcastReadConfirmationDialog from "@/components/broadcast/TheBroadcastReadConfirmationDialog.vue";
import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import WlsClock from "@/components/wlsComponents/WlsClock.vue";
import WlsHeartbeat from "@/components/wlsComponents/WlsHeartbeat.vue";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useMonitoringCronjobService } from "@/composables/monitoring/monitoringCronjobService.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_WAHLSCHLIESSUNG,
  ROUTE_WAHLVORSTAND,
} from "@/constants";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

const { loadEreignisse } = useEreignisStore();
const { loadUser } = useUserStore();
const { forceLoadWahlvorstand } = useWahlvorstandStore();
const { initTasks } = useTaskManagerStore();
const { loadWaehler } = useMonitoringStore();

const { startBroadcastMessageInterval, stopBroadcastMessageInterval } =
  useBroadcastCronjobService();
const { startWahlbeteiligungInterval, stopWahlbeteiligungInterval } =
  useMonitoringCronjobService();

const { toLocalDateFormat } = useDateTimeFormatter();

const { currentUserWahltag, currentUserWahlbezirkNummer } =
  storeToRefs(useUserStore());

const [drawer, toggleDrawer] = useToggle();
const isOffline = ref(false);
const wahltermin = ref<string | undefined>(undefined);
const wahlbezirknummer = ref<string | undefined>(undefined);

onMounted(async () => {
  await loadUser().then(() => {
    forceLoadWahlvorstand();
    startBroadcastMessageInterval();
    initTasks();
    loadEreignisse();
    loadWaehler();
    startWahlbeteiligungInterval();

    if (currentUserWahltag.value) {
      wahltermin.value = toLocalDateFormat(currentUserWahltag.value);
    }
    if (currentUserWahlbezirkNummer.value) {
      wahlbezirknummer.value = currentUserWahlbezirkNummer.value;
    }
  });

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
  stopWahlbeteiligungInterval();
});
</script>

<style>
.main {
  background-color: white;
}

.navbar-text {
  font-size: 20px;
}
</style>
