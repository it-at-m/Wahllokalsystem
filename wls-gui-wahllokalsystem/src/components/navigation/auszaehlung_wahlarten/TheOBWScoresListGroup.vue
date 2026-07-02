<template>
  <v-list-group value="OBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Oberbürgermeisters"
      />
    </template>
    <v-list-item
      :title="titleStimmenZaehlen"
      :to="
        routeWithNameAndParams(ROUTE_AUSZAEHLUNG_STIMMZETTEL, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
          wahlart: WahlWahlartEnum.Obw,
        })
      "
    />
    <!-- [Vue warn]: Vuetify error: Multiple nodes with the same ID ist ein bekannter vuetify bug und kommt daher,
    dass das list-item für SRW und BAW mit der gleichen Route aufgerufen wird. Siehe
    https://github.com/vuetifyjs/vuetify/issues/20516 -->
    <v-list-item
      v-for="(route, index) in stapelRoutes"
      :key="index"
      :title="route.title"
      :to="
        routeWithNameAndParams(route.routeName, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
        })
      "
    />
    <v-list-item title="Schnellmeldung" />
    <v-list-item title="Niederschrift" />
  </v-list-group>
</template>

<script setup lang="ts">
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import {
  ROUTE_AUSZAEHLUNG_STIMMZETTEL,
  ROUTE_STAPEL_A,
  ROUTE_STAPEL_B,
  ROUTE_STAPEL_C,
} from "@/constants.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { routeWithNameAndParams } = useNavigationService();

defineProps<{
  titleStimmenZaehlen: string;
  wahlId: string;
  wahlbezirkId: string;
}>();

const stapelRoutes = [
  { title: "Stapel c", routeName: ROUTE_STAPEL_C },
  { title: "Stapel b", routeName: ROUTE_STAPEL_B },
  { title: "Stapel a", routeName: ROUTE_STAPEL_A },
];
</script>
