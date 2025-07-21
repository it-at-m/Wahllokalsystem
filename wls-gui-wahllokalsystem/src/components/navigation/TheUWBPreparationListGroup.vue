<template>
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
      title="Wählerverzeichnis"
      :to="ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS"
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
</template>

<script setup lang="ts">
import { VListGroup, VListItem } from "vuetify/components";

import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_WAHLSCHLIESSUNG,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
} from "@/constants.ts";
</script>
