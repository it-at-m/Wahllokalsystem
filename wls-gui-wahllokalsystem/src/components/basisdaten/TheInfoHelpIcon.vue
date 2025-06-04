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
          :link="item.link"
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
import { EXAMPLE_ROUTES_NEWROUTE } from "@/constants.ts";

const infoHelpData = [
  { icon: "$phone", title: "Wahlhotline", text: "089 233 96233" },
  {
    icon: "$fileDocument",
    title: "Schulungsunterlagen und weitere Infos",
    link: "newroute",
  },
  {
    icon: "$mapSearch",
    title: "Wahlraumfinder",
    text: "Zuständigen Wahlraum suchen",
    link: EXAMPLE_ROUTES_NEWROUTE,
  },
  {
    icon: "$remoteDesktop",
    title: "Fernzugriff starten",
    text: "Zugriff durch Wahlamt erlauben",
    link: "",
  },
  {
    icon: "$printer",
    title: "Testdruck",
    text: "Testseite ausdrucken",
    link: "",
  },
];
</script>
