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
      v-for="(item, index) in items"
      :key="index"
      :disabled="!obwWahlID"
      :title="item.title"
      :to="
        obwWahlID
          ? routeWithNameAndParams(item.routeName, {
              wahlId: String(obwWahlID),
            })
          : routeWithName(EXAMPLE_ROUTES_NOTFOUND)
      "
    />
    <v-list-item title="Schnellmeldung" />
    <v-list-item title="Niederschrift" />
  </v-list-group>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  EXAMPLE_ROUTES_NOTFOUND,
  ROUTE_AUSZAEHLUNG_STIMMZETTEL,
  ROUTE_STAPEL_A,
  ROUTE_STAPEL_B,
} from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { wahlenActions } = useWahlenStore();
const { routeWithNameAndParams, routeWithName } = useNavigationUtils();

const properties = defineProps<{
  titleStimmenZaehlen: string;
}>();

const obwWahlID = computed<string | undefined>(() => {
  return wahlenActions.getWahlIdOrUndefinedByWahlart(WahlWahlartEnum.Obw);
});

const items = [
  {
    title: properties.titleStimmenZaehlen,
    routeName: ROUTE_AUSZAEHLUNG_STIMMZETTEL,
  },
  { title: "Stapel c", routeName: EXAMPLE_ROUTES_NOTFOUND },
  { title: "Stapel b", routeName: ROUTE_STAPEL_B },
  { title: "Stapel a", routeName: ROUTE_STAPEL_A },
];
</script>
