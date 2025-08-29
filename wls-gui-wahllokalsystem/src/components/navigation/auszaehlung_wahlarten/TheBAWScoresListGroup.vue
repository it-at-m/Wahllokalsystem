<template>
  <v-list-group value="BAW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Bezirksausschusses"
      />
    </template>
    <v-list-item
      title="Zählen der Stimmzettel"
      :to="{
        name: ROUTE_AUSZAEHLUNG_STIMMZETTEL,
        params: { wahlId: bawWahlID },
      }"
    />
    <v-list-item title="Ungültige Stimmzettel" />
    <v-list-item title="Gültige Stimmzettel" />
    <v-list-item title="Schnellmeldung" />
    <v-list-item title="Kandidatinnen- und Kandidatenstimmen" />
    <v-list-item title="Niederschrift" />
  </v-list-group>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { ROUTE_AUSZAEHLUNG_STIMMZETTEL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { getWahlIdOrUndefinedByWahlart } = useWahlenStore();

const bawWahlID = computed<string>(() => {
  return getWahlIdOrUndefinedByWahlart(WahlWahlartEnum.Baw) || "";
});
</script>
