<template>
  <v-list-group value="MBW_Scores">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        title="🚧 Wahl des Migrationsbeirats"
      />
    </template>
    <v-list-item
      :title="titleStimmenZaehlen"
      :to="
        routeWithNameAndParams(ROUTE_AUSZAEHLUNG_STIMMZETTEL, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
          wahlart: WahlWahlartEnum.Mbw,
        })
      "
    />
    <v-list-item
      v-for="(route, index) in listItems"
      :key="index"
      :title="route.title"
      :to="
        routeWithNameAndParams(route.routeName, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
        })
      "
    />
  </v-list-group>
</template>

<script setup lang="ts">
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  ROUTE_AUSZAEHLUNG_STIMMZETTEL,
  ROUTE_NIEDERSCHRIFT,
  ROUTE_SCHNELLMELDUNG,
  ROUTE_STAPEL_A_AND_B,
  ROUTE_STAPEL_BC,
  ROUTE_STAPEL_D,
} from "@/constants.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { routeWithNameAndParams } = useNavigationUtils();

defineProps<{
  titleStimmenZaehlen: string;
  wahlId: string;
  wahlbezirkId: string;
}>();

const listItems = [
  { title: "Ungültige Stimmzettel", routeName: ROUTE_STAPEL_D },
  { title: "Gültige Stimmzettel", routeName: ROUTE_STAPEL_A_AND_B },
  { title: "Schnellmeldung", routeName: ROUTE_SCHNELLMELDUNG },
  { title: "Kandidatinnen- und Kandidatenstimmen", routeName: ROUTE_STAPEL_BC },
  { title: "Niederschrift", routeName: ROUTE_NIEDERSCHRIFT },
];
</script>
