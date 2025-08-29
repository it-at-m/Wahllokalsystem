<template>
  <v-list-group value="OBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Oberbürgermeisters"
      />
    </template>
    <v-list-item
      title="Zählen der Stimmzettel"
      :to="{
        name: ROUTE_AUSZAEHLUNG_STIMMZETTEL,
        params: { wahlId: obwWahlID },
      }"
    />
    <v-list-item title="Stapel c" />
    <v-list-item title="Stapel b" />
    <v-list-item title="Stapel a" />
    <v-list-item title="Schnellmeldung" />
    <v-list-item title="Niederschrift" />
  </v-list-group>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { ROUTE_AUSZAEHLUNG_STIMMZETTEL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { getWahlIdOrUndefinedByWahlart } = useWahlenStore();

const obwWahlID = computed<string>(() => {
  return getWahlIdOrUndefinedByWahlart(WahlWahlartEnum.Obw) || "";
});
</script>
