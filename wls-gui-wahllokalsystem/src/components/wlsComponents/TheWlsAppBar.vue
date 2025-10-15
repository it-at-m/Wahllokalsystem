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
        </v-col>
      </v-row>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer">
      <v-list class="pt-0">
        <v-list-group
          value="Allgemein"
          class="bg-primary"
        >
          <template #activator="{ props }">
            <v-list-item
              v-bind="props"
              title="Allgemein"
            />
          </template>
          <v-list-item
            title="Home"
            :to="routeWithName(ROUTES_HOME)"
          />
          <v-list-item
            title="Wahlvorstand"
            :to="routeWithName(ROUTE_WAHLVORSTAND)"
          />
          <the-b-w-b-election-list-group v-if="isBWB" />
          <the-u-w-b-election-list-group v-if="isUWB" />
          <v-list-item
            title="Ereignisse"
            :to="routeWithName(ROUTE_EREIGNISSE)"
          />
        </v-list-group>
        <the-kommunalwahlen-scores-list-group />
      </v-list>
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
import TheBWBElectionListGroup from "@/components/navigation/TheBWBElectionListGroup.vue";
import TheKommunalwahlenScoresListGroup from "@/components/navigation/TheKommunalwahlenScoresListGroup.vue";
import TheUWBElectionListGroup from "@/components/navigation/TheUWBElectionListGroup.vue";
import TheWlsOnlineOfflineMenu from "@/components/wlsComponents/TheWlsOnlineOfflineMenu.vue";
import WlsClock from "@/components/wlsComponents/WlsClock.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_EREIGNISSE,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants.ts";
import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { eroeffnungsuhrzeitState, schliessungsuhrzeitState } =
  storeToRefs(useWahlbezirkStore());

const { toGermanDate } = useDateTimeFormatter();
const { user, currentUserWahltag, currentUserWahlbezirkNummer, isUWB, isBWB } =
  storeToRefs(useUserStore());
const { hasAllTasksRun } = storeToRefs(useTaskManagerStore());
const { routeWithName } = useNavigationUtils();
const [drawer, toggleDrawer] = useToggle();
const wahltermin = computed(() =>
  user ? toGermanDate(currentUserWahltag.value ?? "") : ""
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
