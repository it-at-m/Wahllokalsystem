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
    <v-card
      width="275"
      height="395"
    >
      <v-list class="pt-0">
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
import { computed } from "vue";

import BaseInfoHelpListItem from "@/components/basisdaten/BaseInfoHelpListItem.vue";
import { useHandbuchService } from "@/composables/basisdaten/handbuchService.ts";
import { useHelpIconCallbacks } from "@/composables/basisdaten/helpIconCallbacks.ts";
import { WAHLHOTLINE } from "@/constants.ts";

const { downloadHandbuch } = useHandbuchService();

const {
  openWahlraumfinder,
  openWaehlerverzeichnis,
  isWaehlerverzeichnisUrlAvailable,
  startFernzugriff,
  printTestdruck,
} = useHelpIconCallbacks();

const WAEHLERVERZEICHNIS_TITLE = "Wählerverzeichnis";

const infoHelpData = computed(() => {
  const allItems = [
    { icon: "$phone", title: "Wahlhotline", text: WAHLHOTLINE },
    {
      icon: "$fileDocument",
      title: "Schulungsunterlagen",
      callback: downloadHandbuch,
    },
    {
      icon: "$mapSearch",
      title: "Wahlraumfinder",
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
    //Filter bei Wählerverzeichnis nach Vorhandensein der URL
    if (item.title === WAEHLERVERZEICHNIS_TITLE) {
      return isWaehlerverzeichnisUrlAvailable();
    }
    //Rest wird immer angezeigt
    return true;
  });
});
</script>
