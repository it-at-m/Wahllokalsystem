<template>
  <v-list-group value="Wahlhandlung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlhandlung"
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
      title="Stimmabgabe"
      :to="ROUTE_STIMMABGABE"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_STIMMABGABE,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
} from "@/constants.ts";
</script>
