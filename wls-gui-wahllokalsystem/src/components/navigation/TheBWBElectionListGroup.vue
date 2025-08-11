<template>
  <v-list-group value="Wahlhandlung">
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
      title="Wahlbriefe erfassen"
      :to="ROUTE_ERFASSUNG_WAHLBRIEFE"
    />
    <v-list-item
      title="Wahlbriefe zulassen"
      :to="ROUTE_WAHLBRIEFE_ZULASSEN"
    />
  </v-list-group>
</template>

<script setup lang="ts">
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLUMGEBUNG,
} from "@/constants.ts";
</script>
