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
      title="Ungültige Stimmzettel"
      :to="
        routeWithNameAndParams(ROUTE_STAPEL_D, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
        })
      "
    />
    <v-list-item
      title="Gültige Stimmzettel"
      :to="
        routeWithNameAndParams(ROUTE_STAPEL_A_AND_B, {
          wahlId: wahlId,
          wahlbezirkId: wahlbezirkId,
        })
      "
    />
    <v-list-item
      title="Niederschrift"
      :to="
        routeWithNameAndParams(ROUTE_NIEDERSCHRIFT, {
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
  ROUTE_STAPEL_A_AND_B,
  ROUTE_STAPEL_D,
} from "@/constants.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { routeWithNameAndParams } = useNavigationUtils();

defineProps<{
  titleStimmenZaehlen: string;
  wahlId: string;
  wahlbezirkId: string;
}>();
</script>
