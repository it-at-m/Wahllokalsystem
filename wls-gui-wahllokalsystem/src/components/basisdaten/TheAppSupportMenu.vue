<template>
  <v-menu>
    <template #activator="{ props: menuActivator }">
      <v-btn
        v-bind="menuActivator"
        aria-label="Hilfe und Support öffnen"
        title="Hilfe und Support"
        icon="$help"
        variant="text"
        density="comfortable"
        size="x-large"
        color="white"
      />
    </template>
    <v-card width="275">
      <v-list class="pt-0>
        <v-list-item class="list-header">
          <strong>Hilfe und Support</strong>
        </v-list-item>
        <v-divider
          thickness="2"
          color="black"
        />
        <base-info-help-list-item
          v-for="item in infoHelpData"
          :key="item.title"
          :icon="item.icon"
          :title="item.title"
          :text="item.text"
          :callback="item.callback"
        />
      </v-list>
    </v-card>
  </v-menu>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import BaseInfoHelpListItem from "@/components/basisdaten/BaseInfoHelpListItem.vue";
import { useHandbuchService } from "@/composables/basisdaten/handbuchService.ts";
import { useHelpIconCallbacks } from "@/composables/basisdaten/helpIconCallbacks.ts";
import { WAHLHOTLINE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { downloadHandbuch } = useHandbuchService();

const {
  openWahlraumfinder,
  openWaehlerverzeichnis,
  isWaehlerverzeichnisUrlAvailable,
  isWahlraumfinderUrlAvailable,
  startFernzugriff,
  printTestdruck,
} = useHelpIconCallbacks();

const { isUWB } = storeToRefs(useUserStore());

const WAHLHOTLINE_TITLE = "Wahl-Hotline";
const WAEHLERVERZEICHNIS_TITLE = "Wählerverzeichnis";
const WAHLRAUMFINDER_TITLE = "Wahlraumfinder";

const infoHelpData = computed(() => {
  const allItems = [
    { icon: "$phone", title: WAHLHOTLINE_TITLE, text: WAHLHOTLINE },
    {
      icon: "$fileDocument",
      title: "Schulungsunterlagen",
      callback: downloadHandbuch,
    },
    {
      icon: "$mapSearch",
      title: WAHLRAUMFINDER_TITLE,
      text: "Zuständigen Wahlraum suchen",
      callback: openWahlraumfinder,
    },
    {
      icon: "$notebookEditOutline",
      title: WAEHLERVERZEICHNIS_TITLE,
      callback: openWaehlerverzeichnis,
    },
    {
      icon: "$remoteDesktop",
      title: "Fernzugriff starten",
      text: "Zugriff durch Wahlamt erlauben",
      callback: startFernzugriff,
    },
    {
      icon: "$printer",
      title: "Testdruck",
      text: "Testseite ausdrucken",
      callback: printTestdruck,
    },
  ];

  return allItems.filter((item) => {
    if (item.title === WAHLHOTLINE_TITLE) {
      return isUWB.value;
    } else if (item.title === WAHLRAUMFINDER_TITLE) {
      return isWahlraumfinderUrlAvailable();
    } else if (item.title === WAEHLERVERZEICHNIS_TITLE) {
      return isWaehlerverzeichnisUrlAvailable() && isUWB.value;
    }
    //Rest wird immer angezeigt
    return true;
  });
});
</script>
