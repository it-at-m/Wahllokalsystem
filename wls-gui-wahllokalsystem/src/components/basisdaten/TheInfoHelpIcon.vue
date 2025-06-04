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
        <base-help-list-item
          v-for="item in infoHelpData"
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
import {
  VBtn,
  VCard,
  VDivider,
  VList,
  VListItem,
  VMenu,
} from "vuetify/components";

import BaseHelpListItem from "@/components/basisdaten/BaseInfoHelpListItem.vue";
import { useHandbuchService } from "@/composables/basisdaten/handbuchService.ts";
import { useTestDruck } from "@/composables/basisdaten/testDruck.ts";

const { getHandbuch } = useHandbuchService();
const { buildTemplate } = useTestDruck();

const infoHelpData = [
  { icon: "$phone", title: "Wahlhotline", text: "089 233 96233" },
  {
    icon: "$fileDocument",
    title: "Schulungsunterlagen und weitere Infos",
    callback: () => {
      return getHandbuch();
    },
  },
  {
    icon: "$mapSearch",
    title: "Wahlraumfinder",
    text: "Zuständigen Wahlraum suchen",
    callback: () => {
      const win = window.open(
        "https://maps.muenchen.de/wahlraumfinder/",
        "_blank"
      );
      if (win) {
        win.focus();
      }
    },
  },
  {
    icon: "$remoteDesktop",
    title: "Fernzugriff starten",
    text: "Zugriff durch Wahlamt erlauben",
    // todo es passiert nch nichts? was soll passieren?
    callback: () => {
      const win = window.open("KioskControlHandler:teamviewer://", "_blank");
      if (win) {
        win.focus();
      }
    },
  },
  {
    icon: "$printer",
    title: "Testdruck",
    text: "Testseite ausdrucken",
    callback: () => {
      const printWindow = window.open(
        "",
        "",
        "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
      );

      if (printWindow) {
        printWindow.document.body.innerHTML = buildTemplate();
        printWindow.print();
        printWindow.close();
      }
    },
  },
];
</script>
