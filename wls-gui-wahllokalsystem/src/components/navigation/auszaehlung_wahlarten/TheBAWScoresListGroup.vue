<template>
  <v-list-group value="BAW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Bezirksausschusses"
      />
    </template>
    <!-- [Vue warn]: Vuetify error: Multiple nodes with the same ID ist ein bekannter vuetify bug und kommt daher,
    dass das list-item für SRW und OBW mit der gleichen Route aufgerufen wird. Siehe
    https://github.com/vuetifyjs/vuetify/issues/20516 -->
    <v-list-item
      :title="titleStimmenZaehlen"
      :to="
        routeWithNameAndParams(ROUTE_AUSZAEHLUNG_STIMMZETTEL, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
          wahlart: WahlWahlartEnum.Baw,
        })
      "
    />
    <v-list-item title="Ungültige Stimmzettel" />
    <v-list-item title="Gültige Stimmzettel" />
    <v-list-item title="Schnellmeldung" />
    <v-list-item title="Kandidatinnen- und Kandidatenstimmen" />
    <v-list-item title="Niederschrift" />
  </v-list-group>
</template>

<script setup lang="ts">
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_AUSZAEHLUNG_STIMMZETTEL } from "@/constants.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { routeWithNameAndParams } = useNavigationUtils();

defineProps<{
  titleStimmenZaehlen: string;
  wahlId: string;
  wahlbezirkId: string;
}>();
</script>
