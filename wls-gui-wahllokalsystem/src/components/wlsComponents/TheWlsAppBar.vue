<template>
  <div>
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          cols="4"
          class="d-flex align-center justify-start"
        >
          <v-app-bar-nav-icon
            v-if="hasAllTasksRun"
            @click.stop="toggleDrawer()"
          />
          <span class="navbar-text mx-2"> {{ wahltermin }} </span>
          <base-icon-wahlbezirksart class="mx-2" />
          <span
            class="navbar-text mx-2"
            style="white-space: nowrap"
          >
            Wahlbezirk {{ wahlbezirknummer }}
          </span>
        </v-col>
        <v-col
          cols="5"
          class="d-flex align-center justify-center"
        />
        <v-col
          cols="3"
          class="d-flex align-center justify-end"
        >
          <the-waehleranzahl-count-button
            v-if="
              eroeffnungsuhrzeitState.eroeffnungsuhrzeitSent !== undefined &&
              schliessungsuhrzeitState.schliessungsuhrzeitSent === undefined &&
              isUWB
            "
          />
          <wls-clock class="navbar-text mx-2 mt-1" />
          <the-wls-online-offline-menu />
          <the-info-help-icon />
          <v-btn
            icon="$logout"
            variant="text"
            density="comfortable"
            size="x-large"
            @click="onLogoutClicked"
          />
        </v-col>
      </v-row>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer">
      <the-root-navigation-list />
    </v-navigation-drawer>
  </div>
</template>
<script setup lang="ts">
import { useToggle } from "@vueuse/core";
import { storeToRefs } from "pinia";
import { computed } from "vue";

import TheInfoHelpIcon from "@/components/basisdaten/TheInfoHelpIcon.vue";
import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import TheWaehleranzahlCountButton from "@/components/monitoring/TheWaehleranzahlCountButton.vue";
import TheRootNavigationList from "@/components/navigation/TheRootNavigationList.vue";
import TheWlsOnlineOfflineMenu from "@/components/wlsComponents/TheWlsOnlineOfflineMenu.vue";
import WlsClock from "@/components/wlsComponents/WlsClock.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogoutService } from "@/composables/user/logoutService.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { eroeffnungsuhrzeitState, schliessungsuhrzeitState } =
  storeToRefs(useWahlbezirkStore());

const { toGermanDate } = useDateTimeFormatter();
const { user, currentUserWahltag, currentUserWahlbezirkNummer, isUWB } =
  storeToRefs(useUserStore());
const { hasAllTasksRun } = storeToRefs(useTaskManagerStore());

const [drawer, toggleDrawer] = useToggle();
const { logout } = useLogoutService();

const wahltermin = computed(() =>
  user ? toGermanDate(currentUserWahltag.value ?? "") : ""
);
const wahlbezirknummer = computed(() =>
  user ? currentUserWahlbezirkNummer.value : ""
);

function onLogoutClicked() {
  logout();
}
</script>

<style>
.navbar-text {
  font-size: 20px;
}
</style>
