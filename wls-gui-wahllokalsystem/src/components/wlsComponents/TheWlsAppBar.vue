<template>
  <v-app-bar color="primary">
    <v-row align="center">
      <v-col
        cols="4"
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
        cols="5"
        class="d-flex align-center justify-center"
      />
      <v-col
        cols="3"
        class="d-flex align-center justify-end"
      >
        <the-waehleranzahl-count-button
          v-if="
            eroeffnungsuhrzeit !== undefined &&
            schliessungsuhrzeit === undefined &&
            currentUserWahlbezirksArt === WahlbezirksArtEnum.UWB
          "
        />
        <wls-clock class="mx-2" />
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
          title="Wahlumgebung"
          :to="ROUTE_WAHLUMGEBUNG"
        />
        <v-list-item
          title="Beginn Stimmabgabe"
          :to="ROUTE_BEGINN_STIMMABGABE"
        />
        <v-list-item
          title="Wahlhandlung"
          :to="ROUTE_WAHLSCHLIESSUNG"
        />
      </v-list-group>
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
  VListGroup,
  VListItem,
  VNavigationDrawer,
  VRow,
} from "vuetify/components";

import TheInfoHelpIcon from "@/components/basisdaten/TheInfoHelpIcon.vue";
import BaseIconWahlbezirksart from "@/components/common/icons/BaseIconWahlbezirksart.vue";
import TheWaehleranzahlCountButton from "@/components/monitoring/TheWaehleranzahlCountButton.vue";
import WlsClock from "@/components/wlsComponents/WlsClock.vue";
import WlsHeartbeat from "@/components/wlsComponents/WlsHeartbeat.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_EREIGNISSE,
  ROUTE_WAHLSCHLIESSUNG,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORSTAND,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum";

const { eroeffnungsuhrzeit, schliessungsuhrzeit } =
  storeToRefs(useWahlbezirkStore());

const { toGermanDateFormat } = useDateTimeFormatter();
const {
  user,
  currentUserWahltag,
  currentUserWahlbezirkNummer,
  currentUserWahlbezirksArt,
} = storeToRefs(useUserStore());

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
