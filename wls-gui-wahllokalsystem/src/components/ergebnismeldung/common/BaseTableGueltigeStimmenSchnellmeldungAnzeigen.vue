<template>
  <v-table>
    <thead>
      <tr>
        <th class="index-column" />
        <th class="font-weight-bold text-left">Wahlvorschlag</th>
        <th class="font-weight-bold text-right">Stapel a</th>
        <th class="font-weight-bold text-right">Stapel b</th>
        <th class="font-weight-bold text-right">Insgesamt</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="vorschlag in ergebnisseAndWahlvorschlaege"
        :key="vorschlag.wahlvorschlag.identifikator"
      >
        <td class="index-column">
          D {{ vorschlag.wahlvorschlag.ordnungszahl }}
        </td>
        <td>{{ vorschlag.wahlvorschlag.kurzname }}</td>
        <td class="text-right">
          {{ vorschlag.ergebnisStapelA.ergebnis ?? 0 }}
        </td>
        <td class="text-right">
          {{ vorschlag.ergebnisStapelB.ergebnis ?? 0 }}
        </td>
        <td class="text-right">
          {{
            (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
            (vorschlag.ergebnisStapelB.ergebnis ?? 0)
          }}
        </td>
      </tr>
    </tbody>
    <tfoot>
      <tr>
        <td class="font-weight-bold index-column">D</td>
        <td class="font-weight-bold">Gültige Stimmen</td>
        <td class="font-weight-bold text-right">{{ totalSumStapelA }}</td>
        <td class="font-weight-bold text-right">{{ totalSumStapelB }}</td>
        <td class="font-weight-bold text-right">
          {{ totalSumStapelA + totalSumStapelB }}
        </td>
      </tr>
    </tfoot>
  </v-table>
</template>

<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";

import { computed } from "vue";

const props = defineProps<{
  ergebnisseAndWahlvorschlaege: MbwErgebnisseAndWahlvorschlag[];
}>();

const totalSumStapelA = computed(() =>
  props.ergebnisseAndWahlvorschlaege.reduce((sum, vorschlag) => {
    return sum + (vorschlag.ergebnisStapelA.ergebnis ?? 0);
  }, 0)
);
const totalSumStapelB = computed(() =>
  props.ergebnisseAndWahlvorschlaege.reduce((sum, vorschlag) => {
    return sum + (vorschlag.ergebnisStapelB.ergebnis ?? 0);
  }, 0)
);
</script>
