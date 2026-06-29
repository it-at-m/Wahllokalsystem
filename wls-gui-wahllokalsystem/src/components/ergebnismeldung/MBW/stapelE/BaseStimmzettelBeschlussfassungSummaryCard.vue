<template>
  <v-table>
    <thead>
      <tr>
        <th class="font-weight-bold">Übersicht</th>
        <th class="font-weight-bold">Anzahl</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Gültige Stimmzettel</td>
        <td>
          {{ anzahlGueltigeStimmzettel }}
        </td>
      </tr>
      <tr>
        <td>Teilweise gültige Stimmzettel</td>
        <td>
          {{ anzahlTeilweiseGueltigeStimmzettel }}
        </td>
      </tr>
      <tr>
        <td>Ungültige Stimmzettel</td>
        <td>
          {{ anzahlUngueltigeStimmzettel }}
        </td>
      </tr>
      <tr class="bg-grey-lighten-3">
        <td class="font-weight-bold">Gesamt</td>
        <td class="font-weight-bold">{{ anzahlGesamtStimmzettel }}</td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup lang="ts">
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";

import { computed } from "vue";

import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

const props = defineProps<{
  bedenklicheStimmzettel: BedenklicherStimmzettel[];
}>();

const anzahlGueltigeStimmzettel = computed(() => {
  return props.bedenklicheStimmzettel.filter(
    (stimmzettel) => stimmzettel.validity === ValidityEnum.VALID
  ).length;
});

const anzahlTeilweiseGueltigeStimmzettel = computed(() => {
  return props.bedenklicheStimmzettel.filter(
    (stimmzettel) => stimmzettel.validity === ValidityEnum.PARTIAL_VALID
  ).length;
});

const anzahlUngueltigeStimmzettel = computed(() => {
  return props.bedenklicheStimmzettel.filter(
    (stimmzettel) => stimmzettel.validity === ValidityEnum.INVALID
  ).length;
});

const anzahlGesamtStimmzettel = computed(() => {
  return props.bedenklicheStimmzettel.filter(
    (stimmzettel) => !!stimmzettel.validity
  ).length;
});
</script>
