<template>
  <v-app-bar color="primary">
    <v-row align="center">
      <v-col
        cols="4"
        class="d-flex align-center justify-start"
      >
        <v-app-bar-nav-icon
          v-if="hasInitializationOfTasksCompletelyRun"
          @click.stop="toggleDrawer()"
        />
        <span class="navbar-text mx-2"> {{ wahltermin }} </span>
        <base-icon-wahlbezirksart class="mx-2" />
        <span class="navbar-text mx-2">
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
            eroeffnungsuhrzeitSent !== undefined &&
            schliessungsuhrzeitSent === undefined &&
            currentUserWahlbezirksArt === WahlbezirksArtEnum.UWB
          "
        />
        <wls-clock class="navbar-text mx-2" />
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
      <the-b-w-b-preparation-list-group
        v-if="currentUserWahlbezirksArt === WahlbezirksArtEnum.BWB"
      />
      <the-u-w-b-preparation-list-group
        v-if="currentUserWahlbezirksArt === WahlbezirksArtEnum.UWB"
      />
      <v-list-item
        title="Ereignisse"
        :to="ROUTE_EREIGNISSE"
      />
    </v-list>
  </v-navigation-drawer>
</template>
<script setup lang="ts">
import { useToggle } from "@vueuse/core";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import {
  VAppBar,
  VAppBarNavIcon,
  VCol,
  VList,
  VListItem,
  VNavigationDrawer,
  VRow,
} from "vuetify/components";

import TheInfoHelpIcon from "@/components/basisdaten/TheInfoHelpIcon.vue";
import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import TheWaehleranzahlCountButton from "@/components/monitoring/TheWaehleranzahlCountButton.vue";
import TheBWBPreparationListGroup from "@/components/navigation/TheBWBPreparationListGroup.vue";
import TheUWBPreparationListGroup from "@/components/navigation/TheUWBPreparationListGroup.vue";
import WlsClock from "@/components/wlsComponents/WlsClock.vue";
import WlsHeartbeat from "@/components/wlsComponents/WlsHeartbeat.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { ROUTE_EREIGNISSE, ROUTE_WAHLVORSTAND } from "@/constants.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum";

const { eroeffnungsuhrzeitSent, schliessungsuhrzeitSent } =
  storeToRefs(useWahlbezirkStore());
const {
  user,
  currentUserWahltag,
  currentUserWahlbezirkNummer,
  currentUserWahlbezirksArt,
} = storeToRefs(useUserStore());
const { hasInitializationOfTasksCompletelyRun } = storeToRefs(
  useTaskManagerStore()
);

const { toGermanDateFormat } = useDateTimeFormatter();
const [drawer, toggleDrawer] = useToggle();

const isOffline = ref(false);

const wahltermin = computed(() =>
  user ? toGermanDateFormat(currentUserWahltag.value ?? "") : ""
);
const wahlbezirknummer = computed(() =>
  user ? currentUserWahlbezirkNummer.value : ""
);
</script>

<style>
.navbar-text {
  font-size: 20px;
}
</style>
