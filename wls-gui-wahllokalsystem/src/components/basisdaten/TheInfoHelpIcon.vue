<template>
  <v-menu>
    <template #activator="{ props: menuActivator }">
      <v-btn
        v-bind="menuActivator"
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
import BaseInfoHelpListItem from "@/components/basisdaten/BaseInfoHelpListItem.vue";
import { useHandbuchService } from "@/composables/basisdaten/handbuchService.ts";
import { useHelpIconCallbacks } from "@/composables/basisdaten/helpIconCallbacks.ts";
import { WAHLHOTLINE } from "@/constants.ts";

const { downloadHandbuch } = useHandbuchService();

const { openWahlraumfinder, startFernzugriff, printTestdruck } =
  useHelpIconCallbacks();

const infoHelpData = [
  { icon: "$phone", title: "Wahl-Hotline", text: WAHLHOTLINE },
  {
    icon: "$fileDocument",
    title: "Schulungsunterlagen und weitere Infos",
    callback: downloadHandbuch,
  },
  {
    icon: "$mapSearch",
    title: "Wahlraumfinder",
    text: "Zuständigen Wahlraum suchen",
    callback: openWahlraumfinder,
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
</script>
