<template>
  <v-list-group value="Wahlvorbereitung">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="Wahlbriefzulassung"
      />
    </template>
    <v-list-item
      title="Beginn Stimmabgabe"
      :to="ROUTE_BEGINN_STIMMABGABE"
    />
    <v-list-item
      title="Wahlumgebung"
      :to="ROUTE_WAHLUMGEBUNG"
    />
    <v-list-item
      title="🚧 Wahlbriefe erfassen"
      :to="'tbdWahlbriefeErfassen'"
    />
    <v-list-item
      title="Wahlbriefe zulassen"
      :to="ROUTE_WAHLBRIEFE_ZULASSEN"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { VListGroup, VListItem } from "vuetify/components";

import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLUMGEBUNG,
} from "@/constants.ts";
</script>
