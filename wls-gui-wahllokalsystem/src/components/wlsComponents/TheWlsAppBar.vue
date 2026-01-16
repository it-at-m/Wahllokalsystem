<template>
  <div>
    <v-app-bar
      v-if="isUserLoggedIn"
      color="primary"
    >
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
    <v-app-bar
      v-else
      color="primary"
    >
      <v-col
        cols="12"
        class="d-flex align-center justify-end"
      >
        <wls-clock class="navbar-text mx-2 mt-1" />
      </v-col>
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
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useInitTaskManagerStore } from "@/stores/initTaskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { eroeffnungsuhrzeitState, schliessungsuhrzeitState } =
  storeToRefs(useWahlbezirkStore());

const { toGermanDate } = useDateTimeFormatter();
const {
  user,
  currentUserWahltag,
  currentUserWahlbezirkNummer,
  isUWB,
  isUserLoggedIn,
} = storeToRefs(useUserStore());
const { hasAllTasksRun } = storeToRefs(useInitTaskManagerStore());

const [drawer, toggleDrawer] = useToggle();
const { logout } = useLogoutService();
const { addNotification } = useUserNotificationService();

const wahltermin = computed(() =>
  user ? toGermanDate(currentUserWahltag.value ?? "") : ""
);
const wahlbezirknummer = computed(() =>
  user ? currentUserWahlbezirkNummer.value : ""
);

async function onLogoutClicked() {
  try {
    await logout();
  } catch {
    addNotification(
      "Logout fehlgeschlagen. Bitte versuchen Sie es später erneut.",
      UserNotificationCategoryEnum.ERROR
    );
  }
}
</script>

<style>
.navbar-text {
  font-size: 20px;
}
</style>
