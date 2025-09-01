<template>
  <v-list-group value="OBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Oberbürgermeisters"
      />
    </template>
    <!-- [Vue warn]: Vuetify error: Multiple nodes with the same ID ist ein bekannter vuetify bug und kommt daher,
    dass das list-item für SRW und BAW mit der gleichen Route aufgerufen wird. Siehe
    https://github.com/vuetifyjs/vuetify/issues/20516 -->
    <v-list-item
      :title="title"
      :to="{
        name: ROUTE_AUSZAEHLUNG_STIMMZETTEL,
        params: { wahlId: String(obwWahlID) },
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
import { storeToRefs } from "pinia";
import { computed } from "vue";

import { ROUTE_AUSZAEHLUNG_STIMMZETTEL } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { getWahlIdOrUndefinedByWahlart } = useWahlenStore();
const { isUWB } = storeToRefs(useUserStore());

const title = computed(() => {
  return isUWB.value
    ? "Zählen der Stimmzettel"
    : "Zählen der Stimmzettelumschläge";
});

const obwWahlID = computed<string | undefined>(() => {
  return getWahlIdOrUndefinedByWahlart(WahlWahlartEnum.Obw);
});
</script>
