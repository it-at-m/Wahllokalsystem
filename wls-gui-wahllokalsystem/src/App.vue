<template>
  <v-app>
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          cols="3"
          class="d-flex align-center justify-start"
        >
          <v-app-bar-nav-icon @click.stop="toggleDrawer()" />
          <router-link to="/">
            <v-toolbar-title class="font-weight-bold">
              <span class="text-white">WLS</span>
            </v-toolbar-title>
          </router-link>
        </v-col>
        <v-col
          cols="6"
          class="d-flex align-center justify-center"
        />
        <v-col
          cols="3"
          class="d-flex align-center justify-end"
        >
          <!-- heartbeat uses v-model for two-way-binding -->
          <wls-heartbeat v-model:is-offline="isOffline" />
          <v-tooltip
            location="bottom"
            text="Routing Examples"
          >
            <template #activator="{ props }">
              <router-link
                v-bind="props"
                :to="{ name: EXAMPLE_ROUTES_NEWROUTE }"
              >
                <v-btn
                  icon="$routes"
                  variant="text"
                  density="comfortable"
                  size="x-large"
                  color="white"
                />
              </router-link>
            </template>
          </v-tooltip>
          <v-tooltip
            location="bottom"
            text="Datenvalidierung Examples"
          >
            <template #activator="{ props }">
              <router-link
                v-bind="props"
                :to="{ name: EXAMPLE_VALIDATION }"
              >
                <v-btn
                  icon="$textBoxCheck"
                  variant="text"
                  density="comfortable"
                  size="x-large"
                  color="white"
                />
              </router-link>
            </template>
          </v-tooltip>
          <v-tooltip
            location="bottom"
            text="Toast Examples"
          >
            <template #activator="{ props }">
              <router-link
                v-bind="props"
                :to="{ name: TOAST }"
              >
                <v-btn
                  icon="$toaster"
                  variant="text"
                  density="comfortable"
                  size="x-large"
                  color="white"
                />
              </router-link>
            </template>
          </v-tooltip>
          <v-tooltip
            location="bottom"
            text="printing example"
          >
            <template #activator="{ props }">
              <router-link
                v-bind="props"
                :to="{ name: PRINT_EXAMPLE }"
              >
                <v-btn
                  icon="$printer"
                  variant="text"
                  density="comfortable"
                  size="x-large"
                  color="white"
                />
              </router-link>
            </template>
          </v-tooltip>
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
import { onMounted, onUnmounted, ref } from "vue";
import {
  VApp,
  VAppBar,
  VAppBarNavIcon,
  VBtn,
  VCol,
  VContainer,
  VFadeTransition,
  VList,
  VListGroup,
  VListItem,
  VMain,
  VNavigationDrawer,
  VRow,
  VToolbarTitle,
  VTooltip,
} from "vuetify/components";

import { getUser } from "@/api/user-client.ts";
import TheBroadcastReadConfirmationDialog from "@/components/broadcast/TheBroadcastReadConfirmationDialog.vue";
import WlsHeartbeat from "@/components/wlsComponents/WlsHeartbeat.vue";
import { useBroadcastCronjobService } from "@/composables/broadcast/broadcastCronjobService.ts";
import {
  EXAMPLE_ROUTES_NEWROUTE,
  EXAMPLE_VALIDATION,
  PRINT_EXAMPLE,
  ROUTE_EREIGNISSE,
  ROUTE_WAHLSCHLIESSUNG,
  ROUTE_WAHLVORSTAND,
  TOAST,
} from "@/constants";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { User, UserLocalDevelopment } from "@/types/User.ts";

const { loadEreignisse } = useEreignisStore();
const { setUser } = useUserStore();
const { loadWahlvorstand } = useWahlvorstandStore();
const { initTasks } = useTaskManagerStore();

const { startBroadcastMessageInterval, stopBroadcastMessageInterval } =
  useBroadcastCronjobService();

const [drawer, toggleDrawer] = useToggle();
const isOffline = ref(false);

onMounted(() => {
  loadUser();

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

function loadUser(): void {
  getUser()
    .then((user: User) => setUser(user))
    .catch(() => {
      if (import.meta.env.DEV) {
        setUser(UserLocalDevelopment());
      } else {
        setUser(null);
      }
    })
    .then(() => {
      loadWahlvorstand();
      startBroadcastMessageInterval();
      initTasks();
      loadEreignisse();
    });
}
</script>

<style>
.main {
  background-color: white;
}
</style>
